/*
 * Password Management Servlets (PWM)
 * http://www.pwm-project.org
 *
 * Copyright (c) 2006-2009 Novell, Inc.
 * Copyright (c) 2009-2017 The PWM Project
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package password.pwm.receiver;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;
import password.pwm.PwmConstants;
import password.pwm.bean.TelemetryPublishBean;
import password.pwm.util.java.JsonUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FtpDataReader {

    private static final Logger LOGGER = Logger.createLogger(FtpDataReader.class.getName());

    private final Settings telemetrySettings;
    private final PwmReceiverApp app;

    public FtpDataReader(final PwmReceiverApp app, final Settings telemetrySettings) {
        this.app = app;
        this.telemetrySettings = telemetrySettings;
    }

    public void readData(final Storage storage) {
        app.getStatus().setLastFtpStatus("beginning read process");
        app.getStatus().setLastFtpIngest(Instant.now());
        try {
            final FTPClient ftpClient = getFtpClient();
            final List<String> files = getFiles(ftpClient);
            for (final String fileName : files) {
                if (fileName != null && fileName.endsWith(".zip")) {
                    app.getStatus().setLastFtpIngest(Instant.now());
                    app.getStatus().setLastFtpStatus("reading file " + fileName);
                    readFile(ftpClient, fileName, storage);
                } else {
                    LOGGER.info("skipping ftp file " + fileName);
                }
            }
            ftpClient.disconnect();
            app.getStatus().setLastFtpStatus("completed successfully");
            app.getStatus().setLastFtpIngest(Instant.now());

        } catch (Exception e) {
            app.getStatus().setLastFtpIngest(Instant.now());
            app.getStatus().setLastFtpStatus("error during ftp scan: " + e.getMessage());
        }
    }

    private void readFile(final FTPClient ftpClient, final String fileName, final Storage storage) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ftpClient.retrieveFile(fileName, byteArrayOutputStream);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        readZippedByteStream(inputStream, fileName, storage);
    }

    private void readZippedByteStream(final InputStream inputStream, final String fileName, final Storage storage) {
        try {
            final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            final ZipEntry zipEntry = zipInputStream.getNextEntry();
            final String zipEntryName = zipEntry.getName();
            if (zipEntryName != null && zipEntryName.endsWith(".json")) {
                LOGGER.info("reading ftp file " + fileName + ":" + zipEntryName);
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                final byte[] buffer = new byte[1024];
                int len;
                while ((len = zipInputStream.read(buffer)) > 0) {
                    byteArrayOutputStream.write(buffer, 0, len);
                }
                final String resultsStr = byteArrayOutputStream.toString(PwmConstants.DEFAULT_CHARSET.name());
                final TelemetryPublishBean bean = JsonUtil.deserialize(resultsStr, TelemetryPublishBean.class);
                storage.store(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getFiles(final FTPClient ftpClient) throws IOException {
        final String pathname = telemetrySettings.getFtpReadPath();
        final FTPFile[] files = ftpClient.listFiles(pathname);
        final List<String> returnFiles = new ArrayList<>();
        for (final FTPFile ftpFile : files) {
            final String name = ftpFile.getName();
            final String fullPath = pathname + "/" + name;
            returnFiles.add(fullPath);
        }

        return Collections.unmodifiableList(returnFiles);
    }

    FTPClient getFtpClient() throws IOException {
        final FTPSClient ftpClient = new FTPSClient();
        ftpClient.connect(telemetrySettings.getFtpSite());
        final boolean loggedInSuccess = ftpClient.login(telemetrySettings.getFtpUser(), telemetrySettings.getFtpPassword());
        ftpClient.enterLocalPassiveMode();
        LOGGER.info("ftp login complete, success=" + loggedInSuccess);
        return ftpClient;
    }
}
