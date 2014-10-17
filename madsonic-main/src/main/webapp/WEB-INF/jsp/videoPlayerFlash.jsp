<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>

    <sub:url value="videoPlayer.view" var="baseUrl"><sub:param name="id" value="${model.video.id}"/></sub:url>
    <sub:url value="main.view" var="backUrl"><sub:param name="id" value="${model.video.id}"/></sub:url>

    <sub:url value="/stream" var="streamUrl">
        <sub:param name="id" value="${model.video.id}"/>
    </sub:url>
	
    <sub:url value="/stream" var="streamUrlPath">
        <sub:param name="path" value="${model.video.path}"/>
    </sub:url>
	
    <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/swfobject.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
    <script type="text/javascript" language="javascript">

	var player;
	var position;
	var maxBitRate = ${model.maxBitRate};
	var timeOffset = ${model.timeOffset};
	var subtitleFontSize = 15;
	var subtitleFontColor = "#FFFFFF";
	var subtitleBackdrop = "false";
	var streamUrlPath = "${streamUrlPath}"; 
	var srtPath = streamUrlPath.substring(0,streamUrlPath.length-6)+'737274';
		
	function CCSettingCCRatio(){
		var ratio = document.getElementById('CCSeetingsRatioCheck').value;
		if(ratio == "on"){
			 document.getElementById('CCSeetingsRatio').style.display = 'inline';
		}
		else{
			document.getElementById('CCSeetingsRatio').style.display = 'none';
		}
	}
		
		
        function init() {
            var flashvars = {
                id:"player1",
                skin:"<c:url value="/flash/newtubedark.zip"/>",
//              plugins:"metaviewer-1",
                screencolor:"000000",
                plugins:"./flash/captions.swf",
                'captions.file':srtPath,
                'captions.back':subtitleBackdrop,
                'captions.offset':timeOffset,
                'captions.fontSize':subtitleFontSize,
                'captions.color':subtitleFontColor,
                controlbar:"over",
                autostart:"false",
                bufferlength:3,
                backcolor:"<spring:theme code="backgroundColor"/>",
                frontcolor:"<spring:theme code="textColor"/>",
                provider:"video",
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

            var width = "${model.popout ? '100%' : '680'}";
            var height = "${model.popout ? '82%' : '460'}";
            swfobject.embedSWF("<c:url value="/flash/jw-player-5.10.swf"/>", "placeholder1", width, height, "9.0.0", false, flashvars, params, attributes);
        }

        function playerReady(thePlayer) {
            player = $("player1");
            player.addModelListener("TIME", "timeListener");

        <c:if test="${not (model.trial and model.trialExpired)}">
            play();
        </c:if>
        }

        function play() {
            var list = new Array();

        <c:if test="${model.duration > 0}">
				flashvars = {
					file:"${streamUrl}&maxBitRate=" + maxBitRate + "&timeOffset=" + timeOffset + "&player=${model.player}",
					duration:${model.duration} - timeOffset,
					provider:"video"
				};		
        </c:if>				
        <c:if test="${model.duration == 0}">
				flashvars = {
					file:"${model.streamUrl}"
				};
        </c:if>
		
            player.sendEvent("LOAD", flashvars);
            player.sendEvent("PLAY");
        }

        function timeListener(obj) {
            var newPosition = Math.round(obj.position);
            if (newPosition != position) {
                position = newPosition;
                updatePosition();
            }
        }

        function updatePosition() {
            var pos = getPosition();

            var minutes = Math.round(pos / 60);
            var seconds = pos % 60;
			
            var result = minutes + ":";
            if (seconds < 10) {
                result += "0";
            }
            result += seconds;
            $("position").innerHTML = result;
		}

        function changeTimeOffset() {
            timeOffset = $("timeOffset").getValue();
            play();
        }

        function changeBitRate() {
            maxBitRate = $("maxBitRate").getValue();
            timeOffset = getPosition();
            play();
		}

        function popout() {
            var url = "${baseUrl}&maxBitRate=" + maxBitRate + "&timeOffset=" + getPosition() + "&popout=true";
            popupSize(url, "video", 720, 600);
            window.location.href = "${backUrl}";
        }

        function popin() {
            window.opener.location.href = "${baseUrl}&maxBitRate=" + maxBitRate + "&timeOffset=" + getPosition();
            window.close();
        }

        function getPosition() {
            return parseInt(timeOffset) + parseInt(position);
        }

    </script>
	<!--AR MOD START-->
	<script type="text/javascript" src="<c:url value="/script/aspectratio/aspectratio.js"/>"></script>
    <script type="text/javascript" language="javascript">
	function ar_init(){
            var flashvars = {
                id:"player1",
                skin:"<c:url value="/flash/newtubedark.zip"/>",
                screencolor:"000000",
				plugins:"./flash/captions.swf",
				'captions.file':srtPath,
				'captions.back':subtitleBackdrop,
				'captions.offset':timeOffset,
				'captions.fontSize':subtitleFontSize,
				'captions.color':subtitleFontColor,
                controlbar:"over",
                autostart:"false",
                bufferlength:3,
                backcolor:"<spring:theme code="backgroundColor"/>",
                frontcolor:"<spring:theme code="textColor"/>",
                provider:"video",
				stretching:"exactfit",
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

            var width = "${model.popout ? '100%' : '100%'}";
            var height = "${model.popout ? '90%' : '100%'}";
            swfobject.embedSWF("<c:url value="/flash/jw-player-5.10.swf"/>", "placeholder1", width, height, "9.0.0", false, flashvars, params, attributes);
        }
	</script><!--AR MOD END-->
</head>

<body class="mainframe bgcolor1" style="padding-bottom:0.5em" onload="init();">
<c:if test="${not model.popout}">
    <h1>${model.video.title}</h1>
</c:if>

<c:if test="${model.trial}">
    <fmt:formatDate value="${model.trialExpires}" dateStyle="long" var="expiryDate"/>

    <p class="warning" style="padding-top:1em">
        <c:choose>
            <c:when test="${model.trialExpired}">
                <fmt:message key="networksettings.trialexpired"><fmt:param>${expiryDate}</fmt:param></fmt:message>
            </c:when>
            <c:otherwise>
                <fmt:message
                        key="networksettings.trialnotexpired"><fmt:param>${expiryDate}</fmt:param></fmt:message>
            </c:otherwise>
        </c:choose>
    </p>
</c:if>


<div id="wrapper" style="padding-top:1em">
    <div id="placeholder1"><a href="http://www.adobe.com/go/getflashplayer" target="_blank"><fmt:message key="playlist.getflash"/></a></div>
</div>

<div style="padding-top:0.7em;padding-bottom:0.7em">

<c:choose>
    <c:when test="${model.popout}">
        <span class="back" style="padding-right:1em;"><a href="javascript:popin();"><fmt:message key="common.back"/></a></span>
    </c:when>
    <c:otherwise>
        <span class="back" style="padding-right:1em;"><a href="${backUrl}"><fmt:message key="common.back"/></a></span>
        <span class="forward" style="padding-right:2em;"><a href="javascript:popout();"><fmt:message key="videoPlayer.popout"/></a></span>
    </c:otherwise>
</c:choose>

    <span id="position" style="padding-right:0.5em">0:00</span>
	
	<span style="padding-right:0.2em">Offset</span>
    <select id="timeOffset" onchange="changeTimeOffset();" style="padding-left:0.25em;padding-right:0.25em;margin-right:0.5em">
        <c:forEach items="${model.skipOffsets}" var="skipOffset">
            <c:choose>
                <c:when test="${skipOffset.value - skipOffset.value mod 60 eq model.timeOffset - model.timeOffset mod 60}">
                    <option selected="selected" value="${skipOffset.value}">${skipOffset.key}</option>
                </c:when>
                <c:otherwise>
                    <option value="${skipOffset.value}">${skipOffset.key}</option>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </select>
	
	<span style="padding-right:0.2em;padding-left:0.5em;">Bitrate:</span>
    <select id="maxBitRate" onchange="changeBitRate();" style="padding-left:0.25em;margin-left:0em;margin-right:0em">
        <c:forEach items="${model.filteredBitRates}" var="bitRate">
            <c:choose>
                <c:when test="${bitRate eq model.maxBitRate}">
                    <option selected="selected" value="${bitRate}">${bitRate} Kbps</option>
                </c:when>
                <c:otherwise>
                    <option value="${bitRate}">${bitRate} Kbps</option>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </select>
    
    	<!--AR MOD START-->
	<input type="hidden" id="customAspectRatioCheck" value="auto"><image id="customAspectRatioCheckImg" height="17px" width="17px" src="<c:url value="/script/aspectratio/ar_off.png"/>" style="margin-top:-0.3em;padding-left:0.25em;margin-right:0.2em;cursor:pointer;display:inline" alt="change aspect ratio" title="enable manual aspectratio selection" onClick="ar_check()">
	<div id="changeAspectRatio" style="display:inline;visibility:hidden">
	<select id="aspectRatio" onchange="changeAspectRatio();" style="padding-left:0.25em;padding-right:0.25em;margin-right:0.5em">
					<option value="1:1">1:1</option>
					<option value="4:3">4:3</option>
					<option selected="selected"  value="16:9">16:9</option>
					<option value="16:10">16:10</option>
					<option value="14:9">14:9</option>
					<option value="5:4">5:4</option>
					<option value="1.85">1.85</option>
					<option value="2.21">2.21</option>
					<option value="2.35">2.35</option>
					<option value="custom">custom</option>
    </select>
	<div id="customAspectRatio" style="display:none;padding-left:0.25em;padding-right:0.25em;margin-right:0.5em">
		<input type="text" size="3" id="cust_ar_x"></input>:<input type="text" size="3" id="cust_ar_y"></input><input type="button" id="cust_ar_button" onclick="custAspectRatio()" value="ok">
	</div>
	</div>
	<!--AR MOD END-->

</div>

</body>
</html>
