<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%--@elvariable id="command" type="org.madsonic.command.MusicFolderSettingsCommand"--%>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
	
<!--	<c:if test="${customScrollbar}">
		<link href="<c:url value="/style/customScrollbar.css"/>" rel="stylesheet">
		<script type="text/javascript" src="<c:url value="/script/jquery.mousewheel.min.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/script/jquery.mCustomScrollbar.js"/>"></script>
	</c:if>		-->
	
</head>
<body class="mainframe bgcolor1">

<div id="content_2" class="content_main">
<!-- CONTENT -->

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="musicFolder"/>
    <c:param name="toast" value="${command.reload}"/>
</c:import>
<br>
<form:form commandName="command" action="musicFolderSettings.view" method="post">

<table class="indent">
    <tr>
        <th><fmt:message key="musicfoldersettings.name"/></th>
        <th><fmt:message key="musicfoldersettings.path"/></th>
        <th style="padding-left:1em">Index</th>
        <th style="padding-left:1em">Type</th>
        <th style="padding-left:1em">Group</th>		
        <th style="padding-left:1em"><fmt:message key="musicfoldersettings.enabled"/></th>
        <th style="padding-left:1em"><fmt:message key="common.delete"/></th>
        <th></th>
    </tr>

    <c:forEach items="${command.musicFolders}" var="folder" varStatus="loopStatus">
        <tr>
            <td><form:input path="musicFolders[${loopStatus.count-1}].name" size="25"/></td>
            <td><form:input path="musicFolders[${loopStatus.count-1}].path" size="70"/></td>
            <td><form:select path="musicFolders[${loopStatus.count-1}].index" size="1">
				<form:option value="1" label="Index 1 (all)"/>
				<form:option value="2" label="Index 2 "/>
				<form:option value="3" label="Index 3 "/>
				<form:option value="4" label="Index 4 "/>
				</form:select></td>
            <td><form:select path="musicFolders[${loopStatus.count-1}].type" size="1">
				<form:option value="1" label="Music - Artists"/>
				<form:option value="2" label="Music - Various Artists"/>				
				<form:option value="3" label="Music - Compilation"/>
				<form:option value="4" label="Music - Other"/>
				<form:option value="5" label="Video - Series"/>
				<form:option value="6" label="Video - Movies"/>
				<form:option value="7" label="Photo - Collection"/>
				<form:option value="8" label="Photo - Flat"/>
				<form:option value="9" label="Other"/>
				</form:select></td>
            <td><form:select path="musicFolders[${loopStatus.count-1}].group" size="1">
				<form:option value="0" label="dont group"/>
				<form:option value="1" label="Music"/>
				<form:option value="2" label="Video"/>
				<form:option value="3" label="Photo"/>				
				</form:select></td>
            <td align="center" style="padding-left:1em"><form:checkbox path="musicFolders[${loopStatus.count-1}].enabled" cssClass="checkbox"/></td>
            <td align="center" style="padding-left:1em"><form:checkbox path="musicFolders[${loopStatus.count-1}].delete" cssClass="checkbox"/></td>
            <td><c:if test="${not folder.existing}"><span class="warning"><fmt:message key="musicfoldersettings.notfound"/></span></c:if></td>
        </tr>
    </c:forEach>

    <tr>
        <th colspan="6" align="left" style="padding-top:1em"><fmt:message key="musicfoldersettings.add"/></th>
    </tr>

    <tr>
        <td><form:input path="newMusicFolder.name" size="25"/></td>
        <td><form:input path="newMusicFolder.path" size="70"/></td>
		<td><form:select path="newMusicFolder.index" size="1">
		<form:option value="1" label="Index 1 (all)"/>
		<form:option value="2" label="Index 2 "/>
		<form:option value="3" label="Index 3 "/>
		<form:option value="4" label="Index 4 "/>
		</form:select></td>	
            <td><form:select path="newMusicFolder.type" size="1">
				<form:option value="1" label="Music - Artists"/>
				<form:option value="2" label="Music - Various Artists"/>				
				<form:option value="3" label="Music - Compilation"/>
				<form:option value="4" label="Music - Other"/>
				<form:option value="5" label="Video - Series"/>
				<form:option value="6" label="Video - Movies"/>
				<form:option value="7" label="Photo - Collection"/>
				<form:option value="8" label="Photo - Flat"/>
				<form:option value="9" label="Other"/>
		</form:select></td>
            <td><form:select path="newMusicFolder.group" size="1">
				<form:option value="0" label="dont group"/>
				<form:option value="1" label="Music"/>
				<form:option value="2" label="Video"/>
				<form:option value="3" label="Photo"/>				
				</form:select></td>		
        <td align="center" style="padding-left:1em"><form:checkbox path="newMusicFolder.enabled" cssClass="checkbox"/></td>
        <td></td>
    </tr>

</table>

    <div style="padding-top: 1.2em;padding-bottom: 0.3em">
        <span style="white-space: nowrap">
            <fmt:message key="musicfoldersettings.scan"/>
            <form:select path="interval">
                <fmt:message key="musicfoldersettings.interval.never" var="never"/>
                <fmt:message key="musicfoldersettings.interval.one" var="one"/>
                <form:option value="-1" label="${never}"/>
                <form:option value="1" label="${one}"/>

                <c:forTokens items="2 3 7 14 30 60" delims=" " var="interval">
                    <fmt:message key="musicfoldersettings.interval.many" var="many"><fmt:param value="${interval}"/></fmt:message>
                    <form:option value="${interval}" label="${many}"/>
                </c:forTokens>
            </form:select>
            <form:select path="hour">
                <c:forEach begin="0" end="23" var="hour">
                    <fmt:message key="musicfoldersettings.hour" var="hourLabel"><fmt:param value="${hour}"/></fmt:message>
                    <form:option value="${hour}" label="${hourLabel}"/>
                </c:forEach>
            </form:select>
        </span>
    </div>

    <p class="forward"><a href="musicFolderSettings.view?scanNow"><fmt:message key="musicfoldersettings.scannow"/></a></p>

    <c:if test="${command.scanning}">
        <p style="width:60%"><b><fmt:message key="musicfoldersettings.nowscanning"/></b></p>
    </c:if>

    <div>
        <form:checkbox path="fastCache" cssClass="checkbox" id="fastCache"/>
        <form:label path="fastCache"><fmt:message key="musicfoldersettings.fastcache"/></form:label>
    </div>

    <p class="detail" style="width:60%;white-space:normal;">
        <fmt:message key="musicfoldersettings.fastcache.description"/>
    </p>

    <p class="forward"><a href="musicFolderSettings.view?expunge"><fmt:message key="musicfoldersettings.expunge"/></a></p>
    <p class="detail" style="width:60%;white-space:normal;margin-top:-10px;">
        <fmt:message key="musicfoldersettings.expunge.description"/>
    </p>

    <%--<div>--%>
        <%--<form:checkbox path="organizeByFolderStructure" cssClass="checkbox" id="organizeByFolderStructure"/>--%>
        <%--<form:label path="organizeByFolderStructure"><fmt:message key="musicfoldersettings.organizebyfolderstructure"/></form:label>--%>
    <%--</div>--%>

    <%--<p class="detail" style="width:60%;white-space:normal;">--%>
        <%--<fmt:message key="musicfoldersettings.organizebyfolderstructure.description"/>--%>
    <%--</p>--%>

    <p >
        <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
        <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">
    </p>

</form:form>

<c:if test="${command.reload}">
    <script type="text/javascript">
        parent.frames.upper.location.href="top.view?";
        parent.frames.left.location.href="left.view?";
        parent.frames.right.location.href="right.view?";
    </script>
</c:if>

<!-- CONTENT -->
</div>

</body>
<!--
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
						scrollAmount:250 /*scroll buttons pixels scroll amount: integer (pixels)*/
					},
					advanced:{
						updateOnBrowserResize:true, /*update scrollbars on browser resize (for layouts based on percentages): boolean*/
						updateOnContentResize:true, /*auto-update scrollbars on content resize (for dynamic content): boolean*/
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

</script>
</c:if>	
-->
</html>