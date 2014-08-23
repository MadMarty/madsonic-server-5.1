<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
    <head>
	    <%@ include file="head.jsp" %>
	
        <sub:url value="/stream" var="streamURL">
            <sub:param name="id" value="${model.video.id}"/>
        </sub:url>
        <sub:url value="/hls" var="hlsURL">
            <sub:param name="id" value="${model.video.id}"/>
        </sub:url>
        <script type="text/javascript" src="<c:url value="/script/mediaelement/jquery.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/script/mediaelement/mediaelement-and-player.min.js"/>"></script>

		
        <link rel="stylesheet" href="<c:url value="/script/mediaelement/mediaelementplayer.min.css"/>"></link>
    </head>

    <body class="mainframe bgcolor1" style="padding-top:5.0em; padding-bottom:0.5em" onload="init();">
	
		<video id="videoPlayer" width="640" height="360" >
		
		<!-- Pseudo HTML5 -->
		<source type="video/youtube" src="http://www.youtube.com/watch?v=nOEw9iiopwI" />
		</video>
	
	
	<script>

	$('video').mediaelementplayer({
		success: function(media, node, player) {
			$('#' + node.id + '-mode').html('mode: ' + media.pluginType);
		}
	});

	</script>

    </body>
</html>