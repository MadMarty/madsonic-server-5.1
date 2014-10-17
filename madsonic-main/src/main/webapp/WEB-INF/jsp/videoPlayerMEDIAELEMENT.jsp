<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
    <head>
	    <%@ include file="head.jsp" %>

        <sub:url value="${model.streamUrl}" var="youtubeUrl">
        </sub:url>
		
        <sub:url value="/stream" var="streamURL">
            <sub:param name="id" value="${model.video.id}"/>
        </sub:url>
        
        <sub:url value="/hls" var="hlsURL">
            <sub:param name="id" value="${model.video.id}"/>
        </sub:url>
		
        <script type="text/javascript" src="<c:url value="/script/mediaelement/jquery.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/script/mediaelement/mediaelement-and-player.min.js"/>"></script>
        <link rel="stylesheet" href="<c:url value="/script/mediaelement/mediaelementplayer.css"/>"></link> 
		
		<script>
		/* <![CDATA[ */
			jQuery(document).ready(function($) {
				$('video').mediaelementplayer({
					videoWidth: '100%',
					videoHeight: '100%',
					audioWidth: '100%',
					videoVolume: 'horizontal',
					features: ['playpause','progress','current','duration','tracks','volume','fullscreen']
				});
			});
		/* ]]> */
		</script>

    </head>
	
    <style type="text/css">
	#video-container {
		max-width:640px;
		margin:16px auto 16px;
		padding:8px 64px 32px;
	}
	
	.mejs-container {
	/*	box-shadow:0 0 64px rgba(255, 255, 255, .2); */
	}
    </style>
	
    <body class="mainframe bgcolor1" style="padding-top:5.0em; padding-bottom:0.5em">
	
	<div id="video-container">
	
		<video id="videoPlayer" width="640" height="360" controls="controls" preload="none" poster="/icons/default/background.png" > <!-- poster="/icons/default/background.png" -->
	
		<!-- YT   --> <source type="video/youtube" src="${youtubeUrl}" />
		<c:if test="${model.duration ne 0}">
		<!-- HLS  --> <source type="application/x-mpegURL" src="${hlsURL}&bitRate=${model.maxBitRate}" /> 
		<!-- FLV  --> <source type="video/x-flv" src="${streamURL}&maxBitRate=${model.maxBitRate}&timeOffset=0&format=flv&player=${model.player}" />
		<!-- WEBM --> <source type='video/webm; codecs="vp8.0, vorbis"' src="${streamURL}&maxBitRate=${model.maxBitRate}&timeOffset=0&format=webm&player=${model.player}" />
		<!-- MP4  --> <source type="video/mp4" src="${streamURL}&maxBitRate=${model.maxBitRate}&timeOffset=0&format=mkv&player=${model.player}" />
		</c:if>				
		
		</video>
	</div>		

	
		<div style="margin-left:620px;padding-bottom:0.7em">

		<select id="maxBitRate" items="${model.bitRates}" onchange="changeBitRate();" style="padding-left:0.25em;padding-right:0.25em;margin-right:0.5em">
			<c:forEach items="${model.bitRates}" var="bitRate">
				<c:choose>
					<c:when test="${bitRate eq model.maxBitRate}">
						<option selected="selected" value="${bitRate}">${bitRate}</option>
					</c:when>
					<c:otherwise>
						<option value="${bitRate}">${bitRate}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>

		<span id="kbps">Kbps</span>
	</div>
	
	<script>

	var maxBitRate = ${model.maxBitRate};
	var player = false;
	
	$('video').mediaelementplayer({
		success: function(media, node, player) {
			$('#' + node.id + '-mode').html('mode: ' + media.pluginType);
		}
	});
	
	function changeBitRate() {
		var bitRate = document.getElementById('maxBitRate');
		maxBitRate = bitRate.options[bitRate.selectedIndex].value;
		loadVideo();
	}
	
	function loadVideo() {
		player.pause();
		player.load();
		player.play();
	}
			
	</script>
    </body>
</html>