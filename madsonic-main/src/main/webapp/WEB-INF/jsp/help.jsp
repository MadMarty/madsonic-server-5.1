<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html><head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
</head>
<body class="mainframe bgcolor1">

<c:choose>
    <c:when test="${empty model.buildDate}">
        <fmt:message key="common.unknown" var="buildDateString"/>
    </c:when>
    <c:otherwise>
        <fmt:formatDate value="${model.buildDate}" dateStyle="short" pattern="yyyyMMdd" var="buildDateString"/>
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${empty model.buildTime}">
        <fmt:message key="common.unknown" var="buildTimeString"/>
    </c:when>
    <c:otherwise>
        <fmt:formatDate value="${model.buildTime}" type="time" pattern="hhmm" var="buildTimeString"/>
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${empty model.localVersion}">
        <fmt:message key="common.unknown" var="versionString"/>
    </c:when>
    <c:otherwise>
        <c:set var="versionString" value="${model.localVersion}"/>
    </c:otherwise>
</c:choose>

<h1>
    <img src="<spring:theme code="helpImage"/>" alt="">
    <fmt:message key="help.title"><fmt:param value="${model.brand}"/></fmt:message>
</h1>

<c:if test="${model.newVersionAvailable}">
    <p class="warning"><fmt:message key="help.upgrade"><fmt:param value="${model.brand}"/><fmt:param value="${model.latestVersion}"/></fmt:message>
	<img src="icons/default/new.png" width="48" hight="48" title="New Version"/>
	</p>
</c:if>

<table width="700" class="ruleTable indent">
    <tr><td class="ruleTableHeader"><fmt:message key="help.version.title"/></td><td class="ruleTableCell">MADSONIC ${versionString}.${buildDateString}.${buildTimeString}</td></tr>
    <tr><td class="ruleTableHeader"></td><td class="ruleTableCell">MADSONIC REST API v${model.RESTInfo}</td></tr>
    <tr><td class="ruleTableHeader">License</td><td class="ruleTableCell status">Madsonic Free Edition, for personal use only as described below.</td></tr>
    <tr><td class="ruleTableHeader"><fmt:message key="help.server.title"/></td><td class="ruleTableCell">${model.serverInfo} (<sub:formatBytes bytes="${model.usedMemory}"/> / <sub:formatBytes bytes="${model.totalMemory}"/>)</td></tr>
    <tr><td class="ruleTableHeader"></td><td class="ruleTableCell">${model.cacheInfo}</td></tr>
    <tr><td class="ruleTableHeader"></td><td class="ruleTableCell">Spring.Framework v${model.springInfo}, Spring.Security v${model.databaseInfo}</td></tr>
    <tr><td class="ruleTableHeader"></td><td class="ruleTableCell">HyperSQL DataBase v2.3.2</td></tr>
	<tr><td class="ruleTableHeader"></td>
	<c:choose>
    <c:when test="${!model.transcoderFound}">
		<td class="ruleTableCell error">${model.ffmpegInfo}</td>
    </c:when>
    <c:otherwise>
		<td class="ruleTableCell info">${model.ffmpegInfo}</td>
    </c:otherwise>
	</c:choose>
	</tr>
    <tr><td class="ruleTableHeader"><fmt:message key="help.license.title"/></td><td class="ruleTableCell">
	<a href="http://www.gnu.org/copyleft/gpl.html" target="_blank"><img style="float:right;margin-left: 10px" alt="GPL 3.0" src="<c:url value="/icons/default/gpl.png"/>"></a>
	<fmt:message key="help.license.text"><fmt:param value="${model.brand}"/></fmt:message></td></tr>
    <tr><td class="ruleTableHeader"><fmt:message key="help.contact.title"/></td><td class="ruleTableCell"><fmt:message key="help.contactmadsonic.text"><fmt:param value="${model.brand}"/></fmt:message></td></tr>
    <tr><td class="ruleTableHeader"></td><td class="ruleTableCell"><fmt:message key="help.contact.text"><fmt:param value="${model.brand}"/></fmt:message></td></tr>
    <tr><td class="ruleTableHeader"><fmt:message key="help.madsonic_homepage.title"/></td><td class="ruleTableCell"><a target="_blank" href="http://www.madsonic.org/">www.madsonic.org</a><img style="float:right;margin-left: 10px" alt="Madsonic" src="<c:url value="/icons/default/madsonic_banner.png"/>"></td></tr>
    <tr><td class="ruleTableHeader"><fmt:message key="help.madsonic_forum.title"/></td><td class="ruleTableCell"><a target="_blank" href="http://forum.madsonic.org/">forum.madsonic.org</a> <img style="float:right;margin-left: 10px" alt="Madsonic" src="<c:url value="/icons/default/madsonic_banner.png"/>"></td></tr>
    <tr><td class="ruleTableHeader">GitHub</td><td class="ruleTableCell"><a target="_blank" href="https://github.com/MadMarty">www.github.com</a><img style="float:right;margin-left: 10px" alt="Github" src="<c:url value="/icons/default/github.png"/>"></td></tr>
<!--	<tr><td class="ruleTableHeader">Browser</td><td class="ruleTableCell"><a target="_blank" href="http://www.mozilla.org/firefox/new/">www.mozilla.org</a> <img style="float:right;margin-left: 10px" alt="Firefox" src="<c:url value="/icons/default/firefox_banner.png"/>"> </td></tr> -->
<!--	<tr><td class="ruleTableHeader">Browser</td><td class="ruleTableCell"><a target="_blank" href="https://www.google.com/intl/de/chrome/browser/">www.google.com</a> <img style="float:right;margin-left: 10px" alt="Chrome" src="<c:url value="/icons/default/chrome_banner.png"/>"> </td></tr> -->
	<tr><td class="ruleTableHeader">Donate</td><td class="ruleTableCell">
	<table width="25%">
		<tr><td>Madsonic</td><td><a href="<c:url value="/madsonic.view"/>"><img src="<spring:theme code="paypalImage"/>" alt=""></a></td></tr>
	</table>
	</td></tr>
	</table>
</body></html>