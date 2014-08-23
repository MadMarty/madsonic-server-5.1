<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%--@elvariable id="model" type="Map"--%>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
	
	<link href="<c:url value="/style/customScrollbar.css"/>" rel="stylesheet">	
</head>
<body class="mainframe bgcolor1">

<div id="content_2" class="content_main">
<!-- CONTENT -->

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="share"/>
    <c:param name="toast" value="${model.toast}"/>
    <c:param name="restricted" value="${not model.user.adminRole}"/>
</c:import>

<br>
<form method="post" action="shareSettings.view">

    <table class="indent" style="border-collapse:collapse;white-space:nowrap">
        <tr>
            <th style="padding-left:1em"><fmt:message key="sharesettings.name"/></th>
            <th style="padding-left:1em"><fmt:message key="sharesettings.owner"/></th>
            <th style="padding-left:1em"><fmt:message key="sharesettings.description"/></th>
            <th style="padding-left:1em"><fmt:message key="sharesettings.expires"/></th>
            <th style="padding-left:1em"><fmt:message key="sharesettings.lastvisited"/></th>
            <th style="padding-left:1em"><fmt:message key="sharesettings.visits"/></th>
            <th style="padding-left:1em"><fmt:message key="sharesettings.files"/></th>
            <th style="padding-left:1em"><fmt:message key="sharesettings.expirein"/></th>
            <th style="padding-left:1em"><fmt:message key="common.delete"/></th>
        </tr>

        <c:forEach items="${model.shareInfos}" var="shareInfo" varStatus="loopStatus">
            <c:set var="share" value="${shareInfo.share}"/>
            <c:choose>
                <c:when test="${loopStatus.count % 2 == 1}">
                    <c:set var="htmlClass" value="class='bgcolor2'"/>
                </c:when>
                <c:otherwise>
                    <c:set var="htmlClass" value=""/>
                </c:otherwise>
            </c:choose>

            <sub:url value="main.view" var="albumUrl">
                <sub:param name="path" value="${shareInfo.dir.path}"/>
            </sub:url>

            <tr>
                <td ${htmlClass} style="padding-left:1em"><a href="${model.shareBaseUrl}${share.name}" target="_blank">${share.name}</a></td>
                <td ${htmlClass} style="padding-left:1em">${share.username}</td>
                <td ${htmlClass} style="padding-left:1em"><input type="text" name="description[${share.id}]" size="20" value="${share.description}"/></td>
                <td ${htmlClass} style="padding-left:1em"><fmt:formatDate value="${share.expires}" type="date" dateStyle="medium"/></td>
                <td ${htmlClass} style="padding-left:1em"><fmt:formatDate value="${share.lastVisited}" type="date" dateStyle="medium"/></td>
                <td ${htmlClass} style="padding-left:1em; text-align:right">${share.visitCount}</td>
                <td ${htmlClass} style="padding-left:1em"><a href="${albumUrl}" title="${shareInfo.dir.name}"><str:truncateNicely upper="30">${fn:escapeXml(shareInfo.dir.name)}</str:truncateNicely></a></td>
                <td ${htmlClass} style="padding-left:1em">
                    <label><input type="radio" name="expireIn[${share.id}]" value="7"><fmt:message key="sharesettings.expirein.week"/></label>
                    <label><input type="radio" name="expireIn[${share.id}]" value="30"><fmt:message key="sharesettings.expirein.month"/></label>
                    <label><input type="radio" name="expireIn[${share.id}]" value="365"><fmt:message key="sharesettings.expirein.year"/></label>
                    <label><input type="radio" name="expireIn[${share.id}]" value="0"><fmt:message key="sharesettings.expirein.never"/></label>
                </td>
                <td ${htmlClass} style="padding-left:1em" align="center" style="padding-left:1em"><input type="checkbox" name="delete[${share.id}]" class="checkbox"/></td>
            </tr>
        </c:forEach>

        <tr>
            <td colspan="4" style="padding-top:1.5em">
                <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
                <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">
            </td>
        </tr>

    </table>
</form>

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
