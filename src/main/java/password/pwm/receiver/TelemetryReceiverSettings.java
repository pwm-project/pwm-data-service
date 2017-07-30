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

import lombok.Getter;
import password.pwm.util.java.JsonUtil;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@Getter
public class TelemetryReceiverSettings {
    private String ftpSite;
    private String ftpUser;
    private String ftpPassword;
    private String ftpReadPath;
    private String dataPath;

    static TelemetryReceiverSettings readFromFile(final String filename) throws IOException {
        final Properties properties = new Properties();
        properties.load(new FileReader(filename));
        final String jsonVersion = JsonUtil.serialize(properties);
        return JsonUtil.deserialize(jsonVersion, TelemetryReceiverSettings.class);
    }
}
