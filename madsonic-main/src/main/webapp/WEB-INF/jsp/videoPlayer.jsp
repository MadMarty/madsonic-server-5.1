<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
    <link rel="stylesheet" type="text/css" href="<c:url value="/style/videoPlayer.css"/>">
    <script type="text/javascript" src="<c:url value="/script/jwplayer-5.10.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/cast_sender-v1.js"/>"></script>
    <%@ include file="videoPlayerCast.jsp" %>
</head>

<body class="mainframe bgcolor1" style="padding-bottom:0.5em">

<c:set var="licenseInfo" value="${model.licenseInfo}"/>
<%@ include file="licenseNotice.jsp" %>

<c:if test="${licenseInfo.licenseOrTrialValid}">
    <div>
        <div id="overlay">
            <div id="overlay_text">Playing on Chromecast</div>
        </div>
        <div id="jwplayer"><a href="http://www.adobe.com/go/getflashplayer" target="_blank">Get Flash</a></div>
        <div id="media_control">
            <div id="progress_slider"></div>
            <div id="placeholder"></div>
            <div id="play"></div>
            <div id="pause"></div>
            <div id="progress">0:00</div>
            <div id="duration">0:00</div>
            <div id="audio_on"></div>
            <div id="audio_off"></div>
            <div id="volume_slider"></div>
            <div id="casticonactive"></div>
            <div id="casticonidle"></div>
        </div>
    </div>
    <div id="debug"></div>

    <script type="text/javascript">
        var CastPlayer = new CastPlayer();
    </script>
</c:if>

<h2 style="padding-top: 1em; padding-bottom: 0.5em">${model.video.title}</h2>

<sub:url value="main.view" var="backUrl"><sub:param name="id" value="${model.video.id}"/></sub:url>

<div id="back" class="back" style="float:left;padding-right:2em"><a href="${backUrl}"><fmt:message key="common.back"/></a></div>
<div style="clear: both"></div>

<script type="text/javascript">
    var showBackButton = top.playQueue != null && !top.playQueue.CastPlayer.receiverFound;
    $("#back").toggle(showBackButton);
</script>

</body>
</html>
