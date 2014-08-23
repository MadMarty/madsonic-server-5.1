<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%--@elvariable id="command" type="org.madsonic.command.PersonalSettingsCommand"--%>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>

    <script type="text/javascript" language="javascript">
        function enableLastFmFields() {
            $("#lastFm").is(":checked") ? $("#lastFmTable").show() : $("#lastFmTable").hide();
        }
		
        function showShortcut() {
            $('#avatarSettings').show('blind');
            $('#showSettings').hide();
            $('#hideSettings').show();
            }

        function hideShortcut() {
            $('#avatarSettings').hide('blind');
            $('#hideSettings').hide();
            $('#showSettings').show();
            }
		
    </script>

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
    <c:param name="cat" value="default"/>
    <c:param name="restricted" value="false"/>
    <c:param name="toast" value="${command.reloadNeeded}"/>
</c:import>
<br>
<h1><fmt:message key="personalsettings.title"><fmt:param>${command.user.username}</fmt:param></fmt:message></h1>

<fmt:message key="common.default" var="defaultLabel"/>
        <fmt:message key="common.rows" var="rows"/>
        <fmt:message key="common.columns" var="columns"/>
		
<form:form method="post" action="defaultSettings.view" commandName="command">

    <table style="white-space:nowrap" class="indent">

        <tr>
            <td><fmt:message key="personalsettings.language"/></td>
            <td>
                <form:select path="localeIndex" cssStyle="width:15em">
                    <form:option value="-1" label="${defaultLabel}"/>
                    <c:forEach items="${command.locales}" var="locale" varStatus="loopStatus">
                        <form:option value="${loopStatus.count - 1}" label="${locale}"/>
                    </c:forEach>
                </form:select>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="language"/></c:import>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="personalsettings.theme"/></td>
            <td>
                <form:select path="themeIndex" cssStyle="width:15em">
                    <form:option value="-1" label="${defaultLabel}"/>
                    <c:forEach items="${command.themes}" var="theme" varStatus="loopStatus">
                        <form:option value="${loopStatus.count - 1}" label="${theme.name}"/>
                    </c:forEach>
                </form:select>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="theme"/></c:import>
                    </td>
                </tr>
            </table>

    <table style="white-space:nowrap" class="indent">

            <h2><fmt:message key="personalsettings.home"/>:</h2>
            <table>
                <tr>
                    <td><fmt:message key="personalsettings.listtype"/></td>
                    <td>
                        <form:select path="listType">
                            <c:forTokens items="random newest hot allArtist starredArtist starred tip highest alphabetical frequent recent top new" delims=" " var="cat" varStatus="loopStatus">
                                <form:option value="${cat}"><fmt:message key="home.${cat}.text"/></form:option>
                            </c:forTokens>
                        </form:select>
                        <c:import url="helpToolTip.jsp"><c:param name="topic" value="listtype"/></c:import>
                    </td>
                </tr>
                <tr>
                    <td><fmt:message key="common.rows"/></td>
                    <td>
                        <form:select path="listRows">
                            <c:forTokens items="1 2 3 4 5 6 7 8 9 10 15 20 25 30 40 50 75 100" delims=" " var="listrows">
                                <form:option value="${listrows}"><fmt:message key="home.listrows"><fmt:param value="${listrows}"/></fmt:message>${listrows gt 1 ? pluralizer : ""}</form:option>
                            </c:forTokens>
                        </form:select>
                        <c:import url="helpToolTip.jsp"><c:param name="topic" value="listrows"/></c:import>
                    </td>
                </tr>
                <tr>
                    <td><fmt:message key="common.columns"/></td>
                    <td>
                        <form:select path="listColumns">
                            <c:forEach begin="1" end="18" var="listcolumns">
                                <form:option value="${listcolumns}"><fmt:message key="home.listcolumns"><fmt:param value="${listcolumns}"/></fmt:message>${listcolumns gt 1 ? pluralizer : ""}</form:option>
                            </c:forEach>
                        </form:select>
                        <c:import url="helpToolTip.jsp"><c:param name="topic" value="listcolumns"/></c:import>
            </td>
        </tr>
    </table>

    <table class="indent">
        <tr>
            <th style="padding:0 0.5em 0.5em 0;text-align:left;"><fmt:message key="personalsettings.display"/></th>
            <th style="padding:0 0.5em 0.5em 0.5em;text-align:center;"><fmt:message key="personalsettings.browse"/></th>
            <th style="padding:0 0 0.5em 0.5em;text-align:center;"><fmt:message key="personalsettings.playlist"/></th>
            <th style="padding:0 0 0.5em 0.5em">
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="visibility"/></c:import>
            </th>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.discnumber"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.discNumberVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.discNumberVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.tracknumber"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.trackNumberVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.trackNumberVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.artist"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.artistVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.artistVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.album"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.albumVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.albumVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.mood"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.moodVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.moodVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.genre"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.genreVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.genreVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.year"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.yearVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.yearVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.bitrate"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.bitRateVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.bitRateVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.duration"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.durationVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.durationVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.format"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.formatVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.formatVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.filesize"/></td>
            <td style="text-align:center"><form:checkbox path="mainVisibility.fileSizeVisible" cssClass="checkbox"/></td>
            <td style="text-align:center"><form:checkbox path="playlistVisibility.fileSizeVisible" cssClass="checkbox"/></td>
        </tr>
	<tr>
            <td> 
	   </td>
        </tr>
        <tr>
            <td><fmt:message key="personalsettings.captioncutoff"/></td>
            <td style="text-align:center"><form:input path="mainVisibility.captionCutoff" size="3"/></td>
            <td style="text-align:center"><form:input path="playlistVisibility.captionCutoff" size="3"/></td>
        </tr>
    </table>

	  <table class="indent">
        <tr>
            <th style="padding:0 0.5em 0.5em 0;text-align:left;"><fmt:message key="personalsettings.display"/></th>
            <th style="padding:0 0.5em 0.5em 0.5em;text-align:center;">Icon</th>
            <th style="padding:0 0 0.5em 0.5em;text-align:center;">Option</th>
        </tr>
        <tr>
            <td>Rank</td>
			<td><img src="<c:url value="/icons/default/rank.png"/>" width="16" height="15" /></td>
			<td style="text-align:center"><form:checkbox path="buttonVisibility.rankVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td>Starred</td>
			<td><img src="<c:url value="/icons/madsonic_dark/rating_off.png"/>" width="16" height="15" /></td>
			<td style="text-align:center"><form:checkbox path="buttonVisibility.starredVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td>Play</td>
			<td><img src="<c:url value="/icons/madsonic_dark/play.png"/>" width="16" height="15" /></td>
			<td style="text-align:center"><form:checkbox path="buttonVisibility.playVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td>Play + Add</td>
			<td><img src="<c:url value="/icons/madsonic_dark/playadd.png"/>" width="16" height="15" /></td>
			<td style="text-align:center"><form:checkbox path="buttonVisibility.playAddVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td>Play + Add more</td>
			<td><img src="<c:url value="/icons/madsonic_dark/playadd.png"/>" width="16" height="15" /></td>
			<td style="text-align:center"><form:checkbox path="buttonVisibility.playMoreVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td>Add Context menu</td>
			<td><img src="<c:url value="/icons/madsonic_dark/add.png"/>" width="16" height="15" /></td>
			<td style="text-align:center"><form:checkbox path="buttonVisibility.addContextVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td>Add next</td>
			<td><img src="<c:url value="/icons/madsonic_dark/add.png"/>" width="16" height="15" /></td>
			<td style="text-align:center"><form:checkbox path="buttonVisibility.addNextVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td>Add last</td>
			<td><img src="<c:url value="/icons/madsonic_dark/add.png"/>" width="16" height="15" /></td>
			<td style="text-align:center"><form:checkbox path="buttonVisibility.addLastVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td>Download</td>
			<td><img src="<c:url value="/icons/madsonic_dark/download.png"/>" width="16" height="15" /></td>
			<td style="text-align:center"><form:checkbox path="buttonVisibility.downloadVisible" cssClass="checkbox"/></td>
        </tr>
        <tr>
            <td>Youtube</td>
			<td><img src="<c:url value="/icons/default/youtube.png"/>" width="16" height="15" /></td>
			<td style="text-align:center"><form:checkbox path="buttonVisibility.youtubeVisible" cssClass="checkbox"/></td>
        </tr>
    </table>
	
    <table class="indent">	
		<tr> 
			<td><form:checkbox path="customScrollbarEnabled" id="customScrollbar" cssClass="checkbox"/></td>
			<td><label for="customScrollbar">CustomScrollbar Enabled</label></td>
        </tr>	
		<tr> 
			<td><form:checkbox path="customAccordionEnabled" id="customAccordion" cssClass="checkbox"/></td>
			<td><label for="customAccordion">CustomAccordion Enabled</label></td>
        </tr>	
    </table>	

    <table class="indent">	
		<tr>
			<td><form:checkbox path="playQueueResizeEnabled" id="playQueueResize" cssClass="checkbox"/></td>
			<td><label for="playQueueResize">PlayQueue Resizeable</label></td>
        </tr>	
		<tr>
			<td><form:checkbox path="leftFrameResizeEnabled" id="leftFrameResize" cssClass="checkbox"/></td>
			<td><label for="leftFrameResize">LeftFrame Resizeable</label></td>
        </tr>	
    </table>	
    <table class="indent">
        <tr>
            <td><form:checkbox path="showNowPlayingEnabled" id="nowPlaying" cssClass="checkbox"/></td>
            <td><label for="nowPlaying"><fmt:message key="personalsettings.shownowplaying"/></label></td>
        </tr>
		<tr>	
            <td><form:checkbox path="showChatEnabled" id="chat" cssClass="checkbox"/></td>
            <td><label for="chat"><fmt:message key="personalsettings.showchat"/></label></td>

            <td style="padding-left:2em"><form:checkbox path="autoHideChatEnabled" id="autoHideChat" cssClass="checkbox"/></td>
            <td><label for="autoHideChat">AutoHide Chat-Panel</label></td>
		</tr>
        <tr>
            <td><form:checkbox path="nowPlayingAllowed" id="nowPlayingAllowed" cssClass="checkbox"/></td>
            <td><label for="nowPlayingAllowed"><fmt:message key="personalsettings.nowplayingallowed"/></label></td>
            <td style="padding-left:2em"><form:checkbox path="partyModeEnabled" id="partyModeEnabled" cssClass="checkbox"/></td>
            <td><label for="partyModeEnabled"><fmt:message key="personalsettings.partymode"/></label>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="partymode"/></c:import>
            </td>
        </tr>
    </table>

            <c:if test="${command.user.adminRole}">
    <table class="indent">
        <tr>
            <td><form:checkbox path="finalVersionNotificationEnabled" id="final" cssClass="checkbox"/></td>
            <td><label for="final"><fmt:message key="personalsettings.finalversionnotification"/></label></td>
        </tr>
        <tr>
            <td><form:checkbox path="betaVersionNotificationEnabled" id="beta" cssClass="checkbox"/></td>
            <td><label for="beta"><fmt:message key="personalsettings.betaversionnotification"/></label></td>
        </tr>
        <tr>
            <td><form:checkbox path="songNotificationEnabled" id="song" cssClass="checkbox"/></td>
            <td><label for="song"><fmt:message key="personalsettings.songnotification"/></label></td>
        </tr>
    </table>
            </c:if>

	<p style="padding-top:1em;padding-bottom:1em">
        <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em"/>
        <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">
    </p>
			
	
	<span id="showSettings" style="display:inline;">
	<div class="forward" style="margin-top:10px;" ><a href="javascript:noop()"onclick="showShortcut()">Show Avatar Settings</a></div></span>

	<span id="hideSettings" style="display:none;">
	<div class="forward" style="margin-top:10px;" ><a href="javascript:noop()"onclick="hideShortcut()">Hide Avatar Settings</a></div></span>
	
	<div id="avatarSettings" style="display:none;margin-top:5px;">

    <h2><fmt:message key="personalsettings.avatar.title"/></h2>

    <p style="padding-top:1em; width:80%;">
        <c:forEach items="${command.avatars}" var="avatar">
            <c:url value="avatar.view" var="avatarUrl">
                <c:param name="id" value="${avatar.id}"/>
            </c:url>
            <span style="white-space:nowrap;">
                <form:radiobutton id="avatar-${avatar.id}" path="avatarId" value="${avatar.id}"/>
                <label for="avatar-${avatar.id}"><img src="${avatarUrl}" alt="${avatar.name}" width="${avatar.width}" height="${avatar.height}" style="padding-right:2em;padding-bottom:1em"/></label>
            </span>
        </c:forEach>
    </p>
    <p>
        <form:radiobutton id="noAvatar" path="avatarId" value="-1"/>
        <label for="noAvatar"><fmt:message key="personalsettings.avatar.none"/></label>
    </p>
    <p>
        <form:radiobutton id="customAvatar" path="avatarId" value="-2"/>
        <label for="customAvatar"><fmt:message key="personalsettings.avatar.custom"/>
            <c:if test="${not empty command.customAvatar}">
                <sub:url value="avatar.view" var="avatarUrl">
                    <sub:param name="username" value="${command.user.username}"/>
                </sub:url>
                <img src="${avatarUrl}" alt="${command.customAvatar.name}" width="${command.customAvatar.width}" height="${command.customAvatar.height}" style="padding-right:2em"/>
            </c:if>
        </label>
    </p>
</form:form>

	<form method="post" enctype="multipart/form-data" action="avatarUpload.view">
		<table>
			<tr>
				<td style="padding-right:1em"><fmt:message key="personalsettings.avatar.changecustom"/></td>
				<td style="padding-right:1em"><input type="file" id="file" name="file" size="40"/></td>
				<td style="padding-right:1em"><input type="submit" value="<fmt:message key="personalsettings.avatar.upload"/>"/></td>
			</tr>
		</table>
	</form>
	
	</div>
<br>
<!--  
<p class="detail" style="text-align:right">
    <fmt:message key="personalsettings.avatar.courtesy"/>
</p>
-->

<c:if test="${command.reloadNeeded}">
    <script language="javascript" type="text/javascript">
		    parent.frames.main.location.href="defaultSettings.view?";
    </script>
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
						scrollAmount:300 /*scroll buttons pixels scroll amount: integer (pixels)*/
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

$(".content_main").resize(function(e){
	$(".content_main").mCustomScrollbar("update");
});
</script>
</c:if>	

</html>

