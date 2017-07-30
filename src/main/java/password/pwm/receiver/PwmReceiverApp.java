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

import password.pwm.util.java.StringUtil;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PwmReceiverApp {
    private static final String ENV_NAME = "DATA_SERVICE_PROPS";

    private Storage storage;
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private Settings settings;
    private Status status = new Status();

    public PwmReceiverApp() {
        final String propsFile = System.getenv(ENV_NAME);
        if (StringUtil.isEmpty(propsFile)) {
            status.setErrorState("Missing environment variable '" + ENV_NAME + "', can't load configuration");
            return;
        }

        try {
            settings = Settings.readFromFile(propsFile);
        } catch (IOException e) {
            status.setErrorState("can't read configuration: " + e.getMessage());
            return;
        }

        try {
            storage = new Storage(settings);
        } catch (Exception e) {
            status.setErrorState("can't start storage system: " + e.getMessage());
            return;
        }

        if (settings.getFtpSite() != null && !settings.getFtpSite().isEmpty()) {
            final Runnable ftpThread = () -> {
                final FtpDataReader ftpDataReader = new FtpDataReader(this, settings);
                ftpDataReader.readData(storage);
            };
            scheduledExecutorService.scheduleAtFixedRate(ftpThread, 0, 1, TimeUnit.HOURS);
        }
    }

    public Settings getSettings() {
        return settings;
    }

    public Storage getStorage() {
        return storage;
    }

    void close() {
        storage.close();
        scheduledExecutorService.shutdown();
    }

    public Status getStatus() {
        return status;
    }
}
