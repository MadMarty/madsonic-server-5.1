<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <style type="text/css">
        #progressBar {width: 350px; height: 10px; border: 1px solid black; display:none;}
        #progressBarContent {width: 0; height: 10px; background: url("<c:url value="/icons/default/progress.png"/>") repeat;}
    </style>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
</head>
<body class="mainframe bgcolor1">

<h1>
    <img id="pageimage" src="<spring:theme code="moreImage"/>" alt="" />
    <fmt:message key="more.title"/><span class="desc"></span>
</h1>

<a href="http://www.madsonic.org" target="_blank"><img alt="Apps" src="icons/default/madsonic.png" style="float: right;margin-left: 3em; margin-right: 3em"/></a>
<h2><img src="<spring:theme code="androidImage"/>" alt=""/>&nbsp;<fmt:message key="more.madsonic.title"/></h2>
<fmt:message key="more.madsonic.text"/>
<br>
<a href="http://subsonic.org/pages/apps.jsp" target="_blank"><img alt="Apps" src="icons/default/apps.png" style="float: right;margin-left: 3em; margin-right: 3em"/></a>
<h2><img src="<spring:theme code="androidImage"/>" alt=""/>&nbsp;<fmt:message key="more.apps.title"/></h2>
<fmt:message key="more.apps.text"/>
<br>
<h2><img src="<spring:theme code="html5Image"/>" alt=""/>&nbsp;
<a href="jam/index.html" target="_blank"><img alt="Jamstash" src="icons/default/jamstash.png" style="float: right;margin-left: 3em; margin-right: 3em"/></a>
<fmt:message key="more.jamstash.title"/></h2>
<fmt:message key="more.jamstash.text"/>
<br>
<h2><img src="<spring:theme code="podcastImage"/>" alt=""/>&nbsp;<fmt:message key="more.podcast.title"/></h2>
<fmt:message key="more.podcast.text"/>
</div>
</body>
</html>