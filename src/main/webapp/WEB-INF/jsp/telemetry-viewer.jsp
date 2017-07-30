<%@ page import="password.pwm.config.PwmSetting" %>
<%@ page import="password.pwm.receiver.SummaryBean" %>
<%@ page import="password.pwm.receiver.TelemetryViewerServlet" %>
<%@ page import="org.joda.time.DateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.Instant" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.FormatStyle" %>
<%--
  ~ Password Management Servlets (PWM)
  ~ http://www.pwm-project.org
  ~
  ~ Copyright (c) 2006-2009 Novell, Inc.
  ~ Copyright (c) 2009-2017 The PWM Project
  ~
  ~ This program is free software; you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation; either version 2 of the License, or
  ~ (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program; if not, write to the Free Software
  ~ Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
  ~
  --%>

<!DOCTYPE html>
<%@ page language="java" session="true" isThreadSafe="true"
         contentType="text/html" %>
<% SummaryBean summaryBean = (SummaryBean)request.getAttribute(TelemetryViewerServlet.SUMMARY_ATTR); %>
<html>
<body>
<div>
    <%=LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)%>
    <br/>
    Server Count = <%=summaryBean.getServerCount()%>
    <h2>Summary Data</h2>
    <table border="1">
        <tr>
            <td><b>SiteHash</b></td>
            <td><b>Description</b></td>
            <td><b>Version</b></td>
            <td><b>Install Age</b></td>
            <td><b>Update Age</b></td>
            <td><b>Ldap</b></td>
            <td><b>OS Name</b></td>
            <td><b>OS Version</b></td>
            <td><b>Servlet Name</b></td>
            <td><b>DB Vendor</b></td>
            <td><b>Appliance</b></td>
        </tr>
        <% for (final String hashID : summaryBean.getSiteSummary().keySet()) { %>
        <% SummaryBean.SiteSummary siteSummary = summaryBean.getSiteSummary().get(hashID); %>
        <tr>
            <td style="max-width: 500px; overflow: auto"><%=hashID%></td>
            <td><%=siteSummary.getDescription()%></td>
            <td><%=siteSummary.getVersion()%></td>
            <td><%=siteSummary.getInstallAge()%></td>
            <td><%=siteSummary.getUpdateAge()%></td>
            <td><%=siteSummary.getLdapVendor()%></td>
            <td><%=siteSummary.getOsName()%></td>
            <td><%=siteSummary.getOsVersion()%></td>
            <td><%=siteSummary.getServletName()%></td>
            <td><%=siteSummary.getDbVendor()%></td>
            <td><%=siteSummary.isAppliance()%></td>
        </tr>
        <% } %>
    </table>
    <h2>LDAP Vendor Count</h2>
    <table border="1">
        <tr>
            <td><b>Ldap</b></td>
            <td><b>Count</b></td>
        </tr>
        <% for (final String ldapVendor : summaryBean.getLdapVendorCount().keySet()) { %>
        <tr>
            <td><%=ldapVendor%></td>
            <td><%=summaryBean.getLdapVendorCount().get(ldapVendor)%></td>
        </tr>
        <% } %>
    </table>
    <h2>App Server Count</h2>
    <table border="1">
        <tr>
            <td><b>App Server Info</b></td>
            <td><b>Count</b></td>
        </tr>
        <% for (final String appServerInfo : summaryBean.getAppServerCount().keySet()) { %>
        <tr>
            <td><%=appServerInfo%></td>
            <td><%=summaryBean.getAppServerCount().get(appServerInfo)%></td>
        </tr>
        <% } %>
    </table>
    <h2>OS Vendor Count</h2>
    <table border="1">
        <tr>
            <td><b>OS Vendor</b></td>
            <td><b>Count</b></td>
        </tr>
        <% for (final String osName : summaryBean.getOsCount().keySet()) { %>
        <tr>
            <td><%=osName%></td>
            <td><%=summaryBean.getOsCount().get(osName)%></td>
        </tr>
        <% } %>
    </table>
    <h2>DB Vendor Count</h2>
    <table border="1">
        <tr>
            <td><b>DB Vendor</b></td>
            <td><b>Count</b></td>
        </tr>
        <% for (final String dbName : summaryBean.getDbCount().keySet()) { %>
        <tr>
            <td><%=dbName%></td>
            <td><%=summaryBean.getDbCount().get(dbName)%></td>
        </tr>
        <% } %>
    </table>
    <h2>Settings</h2>
    <table border="1">
        <tr>
            <td><b>Setting</b></td>
            <td><b>Count</b></td>
        </tr>
        <% for (final String setting: summaryBean.getSettingCount().keySet()) { %>
        <tr>
            <td><%=setting%></td>
            <td><%=summaryBean.getSettingCount().get(setting)%></td>
        </tr>
        <% } %>
    </table>
    <h2>Statistics</h2>
    <table border="1">
        <tr>
            <td><b>Statistic</b></td>
            <td><b>Count</b></td>
        </tr>
        <% for (final String statistic: summaryBean.getStatCount().keySet()) { %>
        <tr>
            <td><%=statistic%></td>
            <td><%=summaryBean.getStatCount().get(statistic)%></td>
        </tr>
        <% } %>
    </table>
    <br/>
    <button>Download CSV</button>
</div>
</body>
</html>
