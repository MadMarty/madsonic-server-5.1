<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
	
	<c:if test="${model.customScrollbar}">
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
    <c:param name="cat" value="transcoding"/>
    <c:param name="toast" value="${model.toast}"/>
</c:import>

<form method="post" action="transcodingSettings.view">
<table class="indent">
    <tr>
        <th><fmt:message key="transcodingsettings.name"/></th>
        <th><fmt:message key="transcodingsettings.sourceformat"/></th>
        <th><fmt:message key="transcodingsettings.targetformat"/></th>
        <th><fmt:message key="transcodingsettings.step1"/></th>
        <th><fmt:message key="transcodingsettings.step2"/></th>
        <th><fmt:message key="transcodingsettings.step3"/></th>
        <th style="padding-left:1em"><fmt:message key="common.delete"/></th>
    </tr>

    <c:forEach items="${model.transcodings}" var="transcoding">
        <tr>
            <td><input style="font-family:monospace" type="text" name="name[${transcoding.id}]" size="15" value="${transcoding.name}"/></td>
            <td><input style="font-family:monospace" type="text" name="sourceFormats[${transcoding.id}]" size="50" value="${transcoding.sourceFormats}"/></td>
            <td><input style="font-family:monospace" type="text" name="targetFormat[${transcoding.id}]" size="8" value="${transcoding.targetFormat}"/></td>
            <td><input style="font-family:monospace" type="text" name="step1[${transcoding.id}]" size="140" value="${transcoding.step1}"/></td>
            <td><input style="font-family:monospace" type="text" name="step2[${transcoding.id}]" size="120" value="${transcoding.step2}"/></td>
            <td><input style="font-family:monospace" type="text" name="step3[${transcoding.id}]" size="60" value="${transcoding.step3}"/></td>
            <td align="center" style="padding-left:1em"><input type="checkbox" name="delete[${transcoding.id}]" class="checkbox"/></td>
        </tr>
    </c:forEach>

    <tr>
        <th colspan="6" align="left" style="padding-top:1em"><fmt:message key="transcodingsettings.add"/></th>
    </tr>

    <tr>
        <td><input style="font-family:monospace" type="text" name="name" size="15" value="${model.newTranscoding.name}"/></td>
        <td><input style="font-family:monospace" type="text" name="sourceFormats" size="50" value="${model.newTranscoding.sourceFormats}"/></td>
        <td><input style="font-family:monospace" type="text" name="targetFormat" size="8" value="${model.newTranscoding.targetFormat}"/></td>
        <td><input style="font-family:monospace" type="text" name="step1" size="140" value="${model.newTranscoding.step1}"/></td>
        <td><input style="font-family:monospace" type="text" name="step2" size="120" value="${model.newTranscoding.step2}"/></td>
        <td><input style="font-family:monospace" type="text" name="step3" size="60" value="${model.newTranscoding.step3}"/></td>
        <td/>
    </tr>

    <tr>
        <td colspan="6" style="padding-top:0.1em">
            <input type="checkbox" id="defaultActive" name="defaultActive" class="checkbox" checked/>
            <label for="defaultActive"><fmt:message key="transcodingsettings.defaultactive"/></label>
        </td>
    </tr>
</table>


    <table style="white-space:nowrap" class="indent">
        <tr>
            <td style="font-weight: bold;">
                <fmt:message key="advancedsettings.downsamplecommand"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="downsamplecommand"/></c:import>
            </td>
            <td>
                <input style="font-family:monospace" type="text" name="downsampleCommand" size="150" value="${model.downsampleCommand}"/>
            </td>
        </tr>
        <tr>
            <td style="font-weight: bold;">
                <fmt:message key="advancedsettings.hlscommand"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="hlscommand"/></c:import>
            </td>
            <td>
                <input style="font-family:monospace" type="text" name="hlsCommand" size="150" value="${model.hlsCommand}"/>
            </td>
        </tr>
    </table>


    <p style="padding-top:0.75em">
        <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
        <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'" style="margin-right:1.3em">
        <a href="http://www.subsonic.org/pages/transcoding.jsp" target="_blank"><fmt:message key="transcodingsettings.recommended"/></a>
    </p>

</form>

<c:if test="${not empty model.error}">
    <p class="warning"><fmt:message key="${model.error}"/></p>
</c:if>

<div style="width:60%">
    <fmt:message key="transcodingsettings.info"><fmt:param value="${model.transcodeDirectory}"/><fmt:param value="${model.brand}"/></fmt:message>
</div>

<!-- CONTENT -->
</div>

</body>

<c:if test="${model.customScrollbar_DISABLED}">
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