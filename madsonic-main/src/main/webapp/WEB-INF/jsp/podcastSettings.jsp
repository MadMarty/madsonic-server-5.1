<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%--@elvariable id="command" type="org.madsonic.command.PodcastSettingsCommand"--%>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
	<link href="<c:url value="/style/customScrollbar.css"/>" rel="stylesheet">	
</head>
<body class="mainframe bgcolor1">

<div id="content_2" class="content_main">
<!-- CONTENT -->

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="podcast"/>
    <c:param name="toast" value="${command.toast}"/>
</c:import>

<form:form commandName="command" action="podcastSettings.view" method="post">

<br>
<table class="indent">
    <tr>
        <td><fmt:message key="podcastsettings.update"/></td>
        <td>
            <form:select path="interval" cssStyle="width:20em">
                <fmt:message key="podcastsettings.interval.manually" var="never"/>
                <fmt:message key="podcastsettings.interval.hourly" var="hourly"/>
                <fmt:message key="podcastsettings.interval.daily" var="daily"/>
                <fmt:message key="podcastsettings.interval.weekly" var="weekly"/>

                <form:option value="-1" label="${never}"/>
                <form:option value="1" label="${hourly}"/>
                <form:option value="24" label="${daily}"/>
                <form:option value="168" label="${weekly}"/>
            </form:select>
        </td>
    </tr>

    <tr>
        <td><fmt:message key="podcastsettings.keep"/></td>
        <td>
            <form:select path="episodeRetentionCount" cssStyle="width:20em">
                <fmt:message key="podcastsettings.keep.all" var="all"/>
                <fmt:message key="podcastsettings.keep.one" var="one"/>

                <form:option value="-1" label="${all}"/>
                <form:option value="1" label="${one}"/>

                <c:forTokens items="2 3 4 5 10 20 30 50 100 200" delims=" " var="count">
                    <fmt:message key="podcastsettings.keep.many" var="many"><fmt:param value="${count}"/></fmt:message>
                    <form:option value="${count}" label="${many}"/>
                </c:forTokens>

            </form:select>
        </td>
    </tr>

    <tr>
        <td><fmt:message key="podcastsettings.download"/></td>
        <td>
            <form:select path="episodeDownloadCount" cssStyle="width:20em">
                <fmt:message key="podcastsettings.download.all" var="all"/>
                <fmt:message key="podcastsettings.download.one" var="one"/>
                <fmt:message key="podcastsettings.download.none" var="none"/>

                <form:option value="-1" label="${all}"/>
                <form:option value="1" label="${one}"/>

                <c:forTokens items="2 3 4 5 10 20 50" delims=" " var="count">
                    <fmt:message key="podcastsettings.download.many" var="many"><fmt:param value="${count}"/></fmt:message>
                    <form:option value="${count}" label="${many}"/>
                </c:forTokens>
                <form:option value="0" label="${none}"/>

            </form:select>
        </td>
    </tr>

    <tr>
        <td><fmt:message key="podcastsettings.folder"/></td>
        <td><form:input path="folder" cssStyle="width:20em"/></td>
    </tr>

    <tr>
        <td><fmt:message key="podcastsettings.download.limit"/></td>
		<td><form:select path="episodeDownloadLimit" size="1">
		<form:option value="1" label="1 "/>
		<form:option value="2" label="2 "/>
		<form:option value="3" label="3 "/>
		<form:option value="5" label="5 "/>
		<form:option value="10" label="10 "/>
		</form:select></td>	
    </tr>
	
	
    <tr>
        <td style="padding-top:1.5em" colspan="2">
            <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
            <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">
        </td>
    </tr>

</table>

</form:form>
<!-- CONTENT -->
</div>
</body></html>