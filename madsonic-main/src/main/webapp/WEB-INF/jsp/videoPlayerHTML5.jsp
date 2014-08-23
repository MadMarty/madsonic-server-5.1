<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<html>
<head>
    <%@ include file="head.jsp" %>
    <sub:url value="videoPlayer.view" var="baseUrl"><sub:param name="id" value="${model.video.id}"/></sub:url>
    <sub:url value="main.view" var="backUrl"><sub:param name="id" value="${model.video.id}"/></sub:url>
	
    <sub:url value="/stream" var="streamUrl">
        <sub:param name="id" value="${model.video.id}"/>
    </sub:url>
    <script type="text/javascript" src="<c:url value="/script/jwplayer.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/jwplayer.html5.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/swfobject.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
    <script type="text/javascript" language="javascript">

        var position;
        var maxBitRate = ${model.maxBitRate};
        var timeOffset = 0;

        function init() {

            var flashvars = {
                autostart: "false",
                bufferlength: 3,
		sources: [{
			file: "${streamUrl}&maxBitRate=" + maxBitRate + "&timeOffset=" + timeOffset + "&player=${model.player}",
			type: 'webm'
		}],
		modes: [
			{ type: 'html5' },
			{ type: 'flash', src: '/flash/jw-player-6.10' }
		]
            };
            var params = {
                allowfullscreen:"true",
                allowscriptaccess:"always"
            };
            var attributes = {
                id:"player1",
                name:"player1"
            };

            flashvars.width = "${model.popout ? '100%' : '600'}";
            flashvars.height = "${model.popout ? '85%' : '360'}";

	    jwplayer("player1").setup(flashvars);
	    jwplayer().onReady(playerReady);

	    swfobject.embedSWF("<c:url value="/flash/jw-player-6.10.swf"/>", "placeholder1", flashvars.width, flashvars.height, "9.0.0", false, flashvars, params, attributes);
        }

        function playerReady(thePlayer) {
            jwplayer().onTime(timeListener);

        <c:if test="${not (model.trial and model.trialExpired)}">
            play();
        </c:if>
        }

        function play() {
            var list = new Array();
            list[0] = {
                file:"${streamUrl}&maxBitRate=" + maxBitRate + "&timeOffset=" + timeOffset + "&player=${model.player}",
		type: "webm",
                duration:${model.duration} - timeOffset,
                provider:"video"
            };
            jwplayer().load(list);
            jwplayer().play();
        }

        function timeListener(obj) {
	    position = Math.floor(jwplayer().getPosition());
            updatePosition();
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
            popupSize(url, "video", 600, 400);
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
	<div id="player1">HTML5 player</div>
</div>

<div style="padding-top:0.7em;padding-bottom:0.7em">

    <span id="position" style="padding-right:0.5em">0:00</span>
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

    <select id="maxBitRate" onchange="changeBitRate();" style="padding-left:0.25em;padding-right:0.25em;margin-right:0.5em">
        <c:forEach items="${model.bitRates}" var="bitRate">
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
</div>

<c:choose>
    <c:when test="${model.popout}">
        <div class="back"><a href="javascript:popin();"><fmt:message key="common.back"/></a></div>
    </c:when>
    <c:otherwise>
        <div class="back" style="float:left;padding-right:2em"><a href="${backUrl}"><fmt:message key="common.back"/></a></div>
        <div class="forward" style="float:left;"><a href="javascript:popout();"><fmt:message key="videoPlayer.popout"/></a></div>
    </c:otherwise>
</c:choose>

</body>
</html>
