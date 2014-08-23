<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
</head>

<body class="mainframe bgcolor1">

<div id="content_2" class="content_main">
<!-- CONTENT -->

<script type="text/javascript" src="<c:url value="/script/wz_tooltip.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/tip_balloon.js"/>"></script>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="api"/>
    <c:param name="toast" value="${model.toast}"/>
</c:import>
<br>
<form method="post" action="apiSettings.view">

<table>
<tr>
<td><fmt:message key="apisettings.api_lastfm"/></td>
<td><input name="API_LASTFM" id="API_LASTFM" size="45" value=""/></td>
</tr>

<tr>
<td><fmt:message key="apisettings.api_musicbrainz"/></td>
<td><input name="API_MUSICBRAINZ" id="API_MUSICBRAINZ" size="45" value="" /></td>
</tr>

<tr>
<td><fmt:message key="apisettings.api_facebook"/></td>
<td><input name="API_FACEBOOK" id="API_FACEBOOK" size="45" value=""/></td>
</tr>

<tr>
<td><fmt:message key="apisettings.api_google"/></td>
<td><input name="API_GOOGLE" id="API_GOOGLE" size="45" value=""/></td>
</tr>

</table>
<br>
    <p>
        <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
        <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">
    </p>
</form>

<!-- CONTENT -->

</div>
</body></html>