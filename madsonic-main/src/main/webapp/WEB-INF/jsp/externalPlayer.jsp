<%--@elvariable id="model" type="java.util.Map"--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/swfobject.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>

    <meta name="og:title" content="${fn:escapeXml(model.songs[0].artist)} &mdash; ${fn:escapeXml(model.songs[0].albumName)}"/>
    <meta name="og:type" content="album"/>

    <c:if test="${not empty model.songs}">
        <sub:url value="/coverArt.view" var="coverArtUrl">
            <sub:param name="id" value="${model.songs[0].id}"/>
            <sub:param name="size" value="500"/>
    </sub:url>
        <meta name="og:image" content="http://${model.redirectFrom}${coverArtUrl}"/>
    </c:if>

	<script type="text/javascript">
	var is_android = false;
	  
	if (navigator.userAgent.match(/android/i)) {
		href = "madsonic://madsonic/get/share/" + "${model.share.name}";
		is_android = true;
	}
	if (is_android) {
		location.href = href;
	}
	</script>	
	
    <script type="text/javascript">
        function init() {
            var flashvars = {
                id:"player1",
                <c:if test="${not model.songs[0].video}">plugins:"revolt.swf",</c:if>
                screencolor:"000000",
                frontcolor:"<spring:theme code="textColor"/>",
                backcolor:"<spring:theme code="backgroundColor"/>",
                skin:"<c:url value="/flash/newtubedark.zip"/>",
                stretching: "fill",
                repeat: "list",
                "logo.timeout": 1,
                "controlbar.forcenextprev": "true",
                "playlist.position": "bottom",
                "playlist.size": 300,
                "viral.allowmenu": "false",
                "viral.oncomplete": "false",
                "viral.onpause": "false"   	
            };
            var params = {
                allowfullscreen:"true",
                allowscriptaccess:"always"
            };
            var attributes = {
                id:"player1",
                name:"player1"
            };
            swfobject.embedSWF("<c:url value="/flash/jw-player-5.10.swf"/>", "placeholder", "500", "600", "9.0.0", false, flashvars, params, attributes);
        }

        function playerReady(thePlayer) {
            var player = $("player1");
            var list = new Array();

        <c:forEach items="${model.songs}" var="song" varStatus="loopStatus">
        <%--@elvariable id="song" type="org.madsonic.domain.MediaFile"--%>
        <sub:url value="/stream" var="streamUrl">
            <sub:param name="id" value="${song.id}"/>
            <sub:param name="player" value="${model.player}"/>
        </sub:url>
        <sub:url value="/coverArt.view" var="coverUrl">
            <sub:param name="id" value="${song.id}"/>
            <sub:param name="size" value="500"/>
        </sub:url>

            // TODO: Use video provider for aac, m4a
            list[${loopStatus.count - 1}] = {
                file: "${streamUrl}",
                image: "${coverUrl}",
                title: "${fn:escapeXml(song.title)}",
                provider: "${song.video ? "video" : "sound"}",
                description: "${fn:escapeXml(song.artist)}"
            };

        <c:if test="${not empty song.durationSeconds}">
            list[${loopStatus.count-1}].duration = ${song.durationSeconds};
        </c:if>

        </c:forEach>

            player.sendEvent("LOAD", list);
            player.sendEvent("PLAY");
        }

    </script>
	
</head>

<body class="mainframe bgcolor1" style="padding-top:2em" onload="init();">

<div style="margin:auto;width:500px">
    <h1 >${empty model.share.description ? model.songs[0].artist : fn:escapeXml(model.share.description)}</h1>
    <div style="float:left;padding-right:1.5em">
        <h2 style="margin:0;">${empty model.share.description ? model.songs[0].albumName : model.share.username}</h2>
    </div>
    <div class="detail" style="float:right">Streaming by <a href="http://madsonic.org/" target="_blank"><b>Madsonic</b></a></div>

    <div style="clear:both;padding-top:1em">
        <div id="placeholder">
            <a href="http://www.adobe.com/go/getflashplayer" target="_blank"><fmt:message key="playlist.getflash"/></a>
        </div>
    </div>
</div>
</body>
</html>
