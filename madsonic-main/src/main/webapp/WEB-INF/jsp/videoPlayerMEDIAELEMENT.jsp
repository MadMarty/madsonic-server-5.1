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
        <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
        <link rel="stylesheet" href="<c:url value="/script/mediaelement/mediaelementplayer.min.css"/>"></link>
    </head>

    <body class="mainframe bgcolor1" style="padding-top:5.0em; padding-bottom:0.5em" onload="init();">

            <div class="wrapper">
                <video id="videoPlayer" preload="none">
                    <source type="application/x-mpegURL" src="" />
                    <source type='video/webm; codecs="vp8.0, vorbis"' src="" />
                    <source type="video/x-flv" src="" />
                    <source type="video/youtube" src="" />
                    <track kind="subtitles" label="Foreign Parts" src="${model.subtitles}" srclang="en"/>
                    <p> Your browser does not support video playback and needs to be updated! </p>
                </video>
            </div>

            <div style="padding-top:0.7em;padding-bottom:0.7em">

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
            var position = 0;
            var timeOffset = 0;
            var maxBitRate = ${model.maxBitRate};
            var hlsUrl, webmUrl, flvUrl, youtubeUrl;
            var player = false;
            var manualTimeControl = false;
            var status = "";

            function init() {

                var videoElement = document.getElementsByTagName("video")[0];
                
                player = new MediaElementPlayer (videoElement, {
                    // Force plugins/native
                    mode: 'auto',
                    // Enables Flash to resize to content size
                    enableAutosize: true,
                    // Start Volume
                    startVolume: 1.0,
                    // The order of controls you want on the control bar
                    features: ['playpause','current','progress','duration','tracks','volume','fullscreen','captioncustomiser'],
                    // Hide controls when playing and mouse is not over the video
                    alwaysShowControls: false,
                    // Turns keyboard support on and off for this instance
                    enableKeyboard: true,
                    // Duration
                    duration: ${model.duration},
                    //Poster
                    poster: "${model.poster}",
                    // Enable forced subtitles
                    startLanguage: 'en',
                    // Manual Time Control
                    manualTimeControl: true,

                        success: function (media, domObject) {

                            // Load Content
                            updateUrls();
                            media.setSrc([{src:hlsUrl,type:'application/x-mpegURL'},{src:webmUrl,type:'video/webm; codecs="vp8.0, vorbis"'},{src:flvUrl,type:'video/x-flv'},{src:youtubeUrl,type:'video/youtube'}]);

                            // Check if the current source is a stream or hls.
                            if(media.src.indexOf("stream") != -1)
                                manualTimeControl = true;
                            else
                                manualTimeControl = false;

                            media.load();

                            // Timer Callback
                            media.addEventListener('timeupdate', function() {

                                position = Math.round(media.currentTime);
                                updatePosition();

                            }, false);
                        }
                    });
                   
                    player.setCurrentTime = function(time) {

                        player.currentTime = time;
                        
                        if(manualTimeControl || player.media.pluginType === 'flash')
                        {
                            timeOffset = Math.round(time);
                            loadVideo();
                        }
                    };
            }

            function loadVideo() {
                updateUrls();
                player.setSrc([{src:hlsUrl,type:'application/x-mpegURL'},{src:webmUrl,type:'video/webm; codecs="vp8.0, vorbis"'},{src:flvUrl,type:'video/x-flv'},{src:youtubeUrl,type:'video/youtube'}]);
                player.pause();
                player.load();
                player.play();
            }

            function updateUrls() {
                hlsUrl = "${hlsURL}&bitRate=" + maxBitRate;
                webmUrl = "${streamURL}&maxBitRate=" + maxBitRate + "&timeOffset=" + timeOffset + "&format=webm" + "&player=${model.player}";
                flvUrl = "${streamURL}&maxBitRate=" + maxBitRate + "&timeOffset=" + timeOffset + "&format=flv" + "&player=${model.player}";
                youtubeUrl = "${model.remoteStreamUrl}"; 

            }

            function updatePosition() {
                var pos = getPosition();
                player.currentTime = pos;
                player.updateCurrent();
            }

            function changeBitRate() {
                var bitRate = document.getElementById('maxBitRate');
                maxBitRate = bitRate.options[bitRate.selectedIndex].value;
                timeOffset = getPosition();
                loadVideo();
            }

            function getPosition() {
                return parseInt(timeOffset) + parseInt(position);
            }

        </script>
    </body>
</html>