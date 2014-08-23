<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%--@elvariable id="command" type="org.madsonic.command.PlayerSettingsCommand"--%>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
	
	<c:if test="${customScrollbar}">
		<link href="<c:url value="/style/customScrollbar.css"/>" rel="stylesheet">
		<script type="text/javascript" src="<c:url value="/script/jquery.mousewheel.min.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/script/jquery.mCustomScrollbar.js"/>"></script>
	</c:if>	
</head>
<body class="mainframe bgcolor1">

<div id="content_2" class="content_main">
<!-- CONTENT -->

<script type="text/javascript" src="<c:url value="/script/wz_tooltip.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/tip_balloon.js"/>"></script>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="player"/>
    <c:param name="toast" value="${command.reloadNeeded}"/>
    <c:param name="restricted" value="${not command.admin}"/>
</c:import>
<br>
<fmt:message key="common.unknown" var="unknown"/>

<c:choose>
<c:when test="${empty command.players}">
    <p><fmt:message key="playersettings.noplayers"/></p>
</c:when>
<c:otherwise>

<c:url value="playerSettings.view" var="deleteUrl">
    <c:param name="delete" value="${command.playerId}"/>
</c:url>
<c:url value="playerSettings.view" var="cloneUrl">
    <c:param name="clone" value="${command.playerId}"/>
</c:url>

<table class="indent">
    <tr>
        <td><b><fmt:message key="playersettings.title"/></b></td>
        <td>
            <select name="player" onchange="location='playerSettings.view?id=' + options[selectedIndex].value;">
                <c:forEach items="${command.players}" var="player">
                    <option ${player.id eq command.playerId ? "selected" : ""}
                            title='${player.ipAddress}' value="${player.id}">${player.description}</option>
                </c:forEach>
            </select>
        </td>
    </tr>
    <tr>
        <td style="padding-right:1em"><div class="forward"><a href="${deleteUrl}"><fmt:message key="playersettings.forget"/></a></div></td>
        <td><div class="forward"><a href="${cloneUrl}"><fmt:message key="playersettings.clone"/></a></div></td>
    </tr>
</table>

<form:form commandName="command" method="post" action="playerSettings.view">
<form:hidden path="playerId"/>

<table class="ruleTable indent">
    <c:forEach items="${command.technologyHolders}" var="technologyHolder">
        <c:set var="technologyName">
            <fmt:message key="playersettings.technology.${fn:toLowerCase(technologyHolder.name)}.title"/>
        </c:set>

        <tr>
            <td class="ruleTableHeader">
                <form:radiobutton id="radio-${technologyName}" path="technologyName" value="${technologyHolder.name}"/>
                <b><label for="radio-${technologyName}">${technologyName}</label></b>
            </td>
            <td class="ruleTableCell" style="width:40em">
                <fmt:message key="playersettings.technology.${fn:toLowerCase(technologyHolder.name)}.text"/>
            </td>
        </tr>
    </c:forEach>
</table>

            <table class="indent" style="border-spacing:3pt;">
                <tr>
                    <td style="padding-right: 1.5em"><fmt:message key="playersettings.type"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${empty command.type}">${unknown}</c:when>
                            <c:otherwise>${command.type}</c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <td style="padding-right: 1.5em"><fmt:message key="playersettings.lastseen"/></td>
                    <td><fmt:formatDate value="${command.lastSeen}" type="both" dateStyle="long" timeStyle="medium"/></td>
                </tr>
            </table>

            <table class="indent" style="border-spacing:3pt;">

    <tr>
        <td><fmt:message key="playersettings.name"/></td>
        <td><form:input path="name" size="16"/></td>
        <td colspan="2"><c:import url="helpToolTip.jsp"><c:param name="topic" value="playername"/></c:import></td>
    </tr>

    <tr>
        <td><fmt:message key="playersettings.coverartsize"/></td>
        <td>
            <form:select path="coverArtSchemeName" cssStyle="width:8em">
                <c:forEach items="${command.coverArtSchemeHolders}" var="coverArtSchemeHolder">
                    <c:set var="coverArtSchemeName">
                        <fmt:message key="playersettings.coverart.${fn:toLowerCase(coverArtSchemeHolder.name)}"/>
                    </c:set>
                    <form:option value="${coverArtSchemeHolder.name}" label="${coverArtSchemeName}"/>
                </c:forEach>
            </form:select>
        </td>
        <td colspan="2"><c:import url="helpToolTip.jsp"><c:param name="topic" value="cover"/></c:import></td>
    </tr>

    <tr>
        <td><fmt:message key="playersettings.maxbitrate"/></td>
        <td>
            <form:select path="transcodeSchemeName" cssStyle="width:8em">
                <c:forEach items="${command.transcodeSchemeHolders}" var="transcodeSchemeHolder">
                    <form:option value="${transcodeSchemeHolder.name}" label="${transcodeSchemeHolder.description}"/>
                </c:forEach>
            </form:select>
        </td>
        <td>
            <c:import url="helpToolTip.jsp"><c:param name="topic" value="transcode"/></c:import>
        </td>
        <td class="warning">
            <c:if test="${not command.transcodingSupported}">
                <fmt:message key="playersettings.nolame"/>
            </c:if>
        </td>
    </tr>

</table>

<table class="indent" style="border-spacing:3pt">

    <tr>
        <td>
            <form:checkbox path="dynamicIp" id="dynamicIp" cssClass="checkbox"/>
            <label for="dynamicIp"><fmt:message key="playersettings.dynamicip"/></label>
        </td>
        <td><c:import url="helpToolTip.jsp"><c:param name="topic" value="dynamicip"/></c:import></td>
    </tr>

    <tr>
        <td>
            <form:checkbox path="autoControlEnabled" id="autoControlEnabled" cssClass="checkbox"/>
            <label for="autoControlEnabled"><fmt:message key="playersettings.autocontrol"/></label>
        </td>
        <td><c:import url="helpToolTip.jsp"><c:param name="topic" value="autocontrol"/></c:import></td>
    </tr>
</table>

    <c:if test="${not empty command.allTranscodings}">
        <table class="indent">
            <tr><td><b><fmt:message key="playersettings.transcodings"/></b></td></tr>
            <c:forEach items="${command.allTranscodings}" var="transcoding" varStatus="loopStatus">
                <c:if test="${loopStatus.count % 8 == 1}"><tr></c:if>
                <td style="padding-right:2em">
                    <form:checkbox path="activeTranscodingIds" id="transcoding${transcoding.id}" value="${transcoding.id}" cssClass="checkbox"/>
                    <label for="transcoding${transcoding.id}">${transcoding.name}</label>
                </td>
                <c:if test="${loopStatus.count % 8 == 0 or loopStatus.count eq fn:length(command.allTranscodings)}"></tr></c:if>
            </c:forEach>
        </table>
    </c:if>

    <input type="submit" value="<fmt:message key="common.save"/>" style="margin-top:1em;margin-right:0.3em">
    <input type="button" value="<fmt:message key="common.cancel"/>" style="margin-top:1em" onclick="location.href='nowPlaying.view'">
</form:form>

</c:otherwise>
</c:choose>

<c:if test="${command.reloadNeeded}">
    <script language="javascript" type="text/javascript">parent.frames.playQueue.location.href="playQueue.view?"</script>
</c:if>

<!-- CONTENT -->
</div>

</body>

<c:if test="${customScrollbar}">
<script type="text/javascript">    

		(function($){
			$(window).load(function(){
				$("#content_2").mCustomScrollbar({
					set_width:false, /*optional element width: boolean, pixels, percentage*/
					set_height:false, /*optional element height: boolean, pixels, percentage*/
					horizontalScroll:false, /*scroll horizontally: boolean*/
					scrollInertia:850, /*scrolling inertia: integer (milliseconds)*/
					mouseWheel:true, /*mousewheel support: boolean*/
					mouseWheelPixels:"auto", /*mousewheel pixels amount: integer, "auto"*/
					autoDraggerLength:true, /*auto-adjust scrollbar dragger length: boolean*/
					autoHideScrollbar:false, /*auto-hide scrollbar when idle*/
					scrollButtons:{ /*scroll buttons*/
						enable:true, /*scroll buttons support: boolean*/
						scrollType:"continuous", /*scroll buttons scrolling type: "continuous", "pixels"*/
						scrollSpeed:"auto", /*scroll buttons continuous scrolling speed: integer, "auto"*/
						scrollAmount:150 /*scroll buttons pixels scroll amount: integer (pixels)*/
					},
					advanced:{
						updateOnBrowserResize:true, /*update scrollbars on browser resize (for layouts based on percentages): boolean*/
						updateOnContentResize:false, /*auto-update scrollbars on content resize (for dynamic content): boolean*/
						autoExpandHorizontalScroll:false, /*auto-expand width for horizontal scrolling: boolean*/
						autoScrollOnFocus:true, /*auto-scroll on focused elements: boolean*/
						normalizeMouseWheelDelta:false /*normalize mouse-wheel delta (-1/1)*/
					},
					contentTouchScroll:true, /*scrolling by touch-swipe content: boolean*/
					callbacks:{
						onScrollStart:function(){}, /*user custom callback function on scroll start event*/
						onScroll:function(){}, /*user custom callback function on scroll event*/
						onTotalScroll:function(){}, /*user custom callback function on scroll end reached event*/
						onTotalScrollBack:function(){}, /*user custom callback function on scroll begin reached event*/
						onTotalScrollOffset:0, /*scroll end reached offset: integer (pixels)*/
						onTotalScrollBackOffset:0, /*scroll begin reached offset: integer (pixels)*/
						whileScrolling:function(){} /*user custom callback function on scrolling event*/
					},
					theme:"light" /*"light", "dark", "light-2", "dark-2", "light-thick", "dark-thick", "light-thin", "dark-thin"*/
				});
			});
		})(jQuery);

$(".content_main").resize(function(e){
	$(".content_main").mCustomScrollbar("update");
});
</script>
</c:if>	

</html>
