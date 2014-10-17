<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>
	<%@ include file="jquery.jsp" %> 	
	
        <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/script/fancyzoom/FancyZoom.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/script/fancyzoom/FancyZoomHTML.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/dwr/interface/chatService.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/dwr/interface/nowPlayingService.js"/>"></script>

		<script type="text/javascript" src="<c:url value="/script/similar_artists/similar_artists.js"/>"></script>
		
</head>
<body class="bgcolor1 rightframe" style="padding-top:0em" onload="init()">

		<script type="text/javascript" src="<c:url value="/script/wz_tooltip.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/script/tip_balloon.js"/>"></script>

<script type="text/javascript">
    function init() {
        setupZoom('<c:url value="/"/>');
        dwr.engine.setErrorHandler(null);
		dwr.util.setEscapeHtml(false);
    <c:if test="${model.showChat}">
        chatService.addMessage(null);
    </c:if>
    }
</script>

<div id="scanningStatus" style="display: none;" class="warning">
    <img src="<spring:theme code="scanningImage"/>" title="" alt=""> <fmt:message key="main.scanning"/> <span id="scanCount"></span>
</div>

<c:if test="${model.showNowPlaying}">

    <!-- This script uses AJAX to periodically retrieve what all users are playing. -->
    <script type="text/javascript" language="javascript">

        startGetNowPlayingTimer();
		
		if (typeof($) !== "undefined") {
		//	alert("jQuery loaded successfully!"); 	
			getSimilarArtists(); 
		}
				
        function startGetNowPlayingTimer() {
            nowPlayingService.getNowPlaying(getNowPlayingCallback);
            setTimeout("startGetNowPlayingTimer()", 10000);
        }

        function getNowPlayingCallback(nowPlaying) {
            var html = nowPlaying.length == 0 ? "" : "<h2><fmt:message key="main.nowplaying"/></h2><table>";
            for (var i = 0; i < nowPlaying.length; i++) {
                html += "<tr><td colspan='2' class='detail' style='padding-top:1em;white-space:nowrap'>";

                if (nowPlaying[i].avatarUrl != null) {
                    html += "<img src='" + nowPlaying[i].avatarUrl + "' style='padding-right:5pt' width='24' height='24'>";
                }
                html += "<b>" + nowPlaying[i].username + "</b></td></tr>"

                html += "<tr><td class='detail' style='padding-right:1em'>" +
                        "<a title='" + nowPlaying[i].tooltip + "' target='main' href='" + nowPlaying[i].albumUrl + "'>";

                if (nowPlaying[i].artist != null) {
                    html += "<em>" + nowPlaying[i].artist + "</em><br/>";
                }

                html += nowPlaying[i].title + "</a><br/>" +
                        "<span class='forward'><a href='" + nowPlaying[i].lyricsUrl + "' onclick=\"return popupSize(this, 'lyrics', 430, 550)\">" +
                        "<fmt:message key="main.lyrics"/>" + "</a></span></td><td style='padding-top:1em'>";

                if (nowPlaying[i].coverArtUrl != null) {
                    html += "<a title='" + nowPlaying[i].tooltip + "' rel='zoom' href='" + nowPlaying[i].coverArtZoomUrl + "'>" +
                            "<img src='" + nowPlaying[i].coverArtUrl + "' width='32' height='32'></a>";
                }
                html += "</td></tr>";

                var minutesAgo = nowPlaying[i].minutesAgo;
                if (minutesAgo > 4) {
                    html += "<tr><td class='detail' colspan='2'>" + minutesAgo + " <fmt:message key="main.minutesago"/></td></tr>";
                }
            }
            html += "</table>";
            $('nowPlaying').innerHTML = html;
            prepZooms();
        }
    </script>

    <div id="nowPlaying">
    </div>

</c:if>

<c:if test="${model.showChat}">
    <script type="text/javascript">

        var revision = 0;
        var revision2 = 0;
        var blink = 0;
        var msgLength = 0;
        startGetMessagesTimer();

        function startGetMessagesTimer() {
            chatService.getMessages(revision, getMessagesCallback);
            setTimeout("startGetMessagesTimer()", 10000);
        }

        function addMessage() {
            chatService.addMessage($("message").value);
            dwr.util.setValue("message", null, { escapeHtml:false });
            setTimeout("startGetMessagesTimer()", 500);
        }
        function clearMessages() {
            chatService.clearMessages();
            setTimeout("startGetMessagesTimer()", 500);
        }
        function getMessagesCallback(messages) {

		msgLength = messages.messages.length;
		
            if (messages == null) {
                return;
            }
			
            revision = messages.revision;

			if (revision2 != revision) {
			   revision2 = revision;
			   if (messages.messages.length > 0 && !blink) {
				  blink = 1;
				  blinkTitle();
			   } else {
				 blink = 0;
			   }
			   var frames = top.window.frames;
			   for (var i = 0; i < 5; i++) {
					 frames[i].addEventListener('focus', function() {
					   top.right.blink = 0;
					   top.document.title = 'Madsonic';
			   }); }
			}

            // Delete all the rows except for the "pattern" row
            dwr.util.removeAllRows("chatlog", { filter:function(div) {
                return (div.id != "pattern");
            }});

            // Create a new set cloned from the pattern row
            for (var i = 0; i < messages.messages.length; i++) {
                var message = messages.messages[i];
                var id = i + 1;
                dwr.util.cloneNode("pattern", { idSuffix:id });
                dwr.util.setValue("user" + id, message.username);
                dwr.util.setValue("date" + id, " [" + formatDate(message.date) + "]");
                dwr.util.setValue("content" + id, message.content);
                $("pattern" + id).show();
            }

            var clearDiv = $("clearDiv");
            if (clearDiv) {
                if (messages.messages.length == 0) {
                    clearDiv.hide();
                } else {
                    clearDiv.show();
                }
            }
        }
        function formatDate(date) {
            var hours = date.getHours();
            var minutes = date.getMinutes();
            var result = hours < 10 ? "0" : "";
            result += hours;
            result += ":";
            if (minutes < 10) {
                result += "0";
            }
            result += minutes;
            return result;
        }
		
	function blinkTitle() {
	if (blink && msgLength > 0) {
      top.document.title = "New Message!";
      setTimeout("top.document.title = 'Madsonic'",1500);
      setTimeout("blinkTitle()",3000);
   }
}
    </script>

    <script type="text/javascript">

        startGetScanningStatusTimer();

        function startGetScanningStatusTimer() {
            nowPlayingService.getScanningStatus(getScanningStatusCallback);
        }

        function getScanningStatusCallback(scanInfo) {
            dwr.util.setValue("scanCount", scanInfo.count);
            if (scanInfo.scanning) {
                $("scanningStatus").show();
                setTimeout("startGetScanningStatusTimer()", 1000);
            } else {
                $("scanningStatus").hide();
                setTimeout("startGetScanningStatusTimer()", 15000);
            }
        }
    </script>

    <h2><fmt:message key="main.chat"/></h2>
    <div style="padding-top:0.3em;padding-bottom:0.3em">
        <input id="message" value=" <fmt:message key="main.message"/>" style="width:90%" onclick="dwr.util.setValue('message', null, { escapeHtml:false });" onkeypress="dwr.util.onReturn(event, addMessage); revision2=revision;"/>
		<!--<c:import url="toolTip.jsp"><c:param name="topic" value="chat with your friends"/></c:import>-->
    </div>

    <table>
        <tbody id="chatlog">
        <tr id="pattern" style="display:none;margin:0;padding:0 0 0.15em 0;border:0"><td>
            <span id="user" class="detailchat" style="font-weight:bold"></span>&nbsp;<span id="date" class="detailchat"></span> <span id="content" class="detailwarp"></span></td>
        </tr>
        </tbody>
    </table>

    <c:if test="${model.user.adminRole}">
        <div id="clearDiv" style="display:none;" class="forward"><a href="#" onclick="clearMessages(); return false;"> <fmt:message key="main.clearchat"/></a></div>
    </c:if>
</c:if>

</body>

<script type="text/javascript">
//    	if(typeof(jQuery) === undefined) { // if jQuery isn't loaded yet...
//				alert("NO jQuery.");
//    		if (time_elapsed <= 15000) { // and we havn't given up trying...
//    			setTimeout(function() {
//    				load.tryReady(200);
//    			}, 200);
//    		}
//    		else {
//    			alert("Timed out while loading jQuery.");
//    		}
//    	}
//
//        if (typeof($) !== "undefined") {
//         //   alert("jQuery loaded successfully!"); // , typeof($)); // jQuery Loaded (function)		
//		}
//		
//	if (${model.showNowPlaying}) { 
//		$(startGetNowPlayingTimer); 
//		$(getSimilarArtists); 
//		}
</script>
</html>
