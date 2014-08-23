<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html><head>
    <%@ include file="head.jsp" %>

	<script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>	
	<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/nowPlayingService.js"/>"></script>	
	
    <script type="text/javascript" language="javascript">
	function newwindow() {
	<!--hide
		window.open('moree.view?','jav','width=750,height=250,resizable=no,scrollbars=yes,toolbar=no,status=yes');
	}
	//-->
	</SCRIPT>
</head>

<body class="bgcolor2 topframe" style="margin:0.4em 1em 0.4em 1em">

<fmt:message key="top.home" var="home"/>
<fmt:message key="top.artist" var="artist"/>
<fmt:message key="top.now_playing" var="nowPlaying"/>
<fmt:message key="top.starred" var="starred"/>
<fmt:message key="top.genres" var="genres"/>
<fmt:message key="top.discover" var="discover"/>
<fmt:message key="top.settings" var="settings"/>
<fmt:message key="top.status" var="status"/>
<fmt:message key="top.radio" var="radio"/>
<fmt:message key="top.moods" var="moods"/>
<fmt:message key="top.history" var="history"/>
<fmt:message key="top.statistics" var="statistics"/>
<fmt:message key="top.playlists" var="playlists"/>
<fmt:message key="top.editor" var="editor"/>
<fmt:message key="top.db" var="db"/>
<fmt:message key="top.logs" var="logs"/>
<fmt:message key="top.upload" var="upload"/>
<fmt:message key="top.podcast" var="podcast"/>
<fmt:message key="top.more" var="more"/>
<fmt:message key="top.chat" var="chat"/>
<fmt:message key="top.help" var="help"/>
<fmt:message key="top.search" var="search"/>

<c:if test="${not model.showRight}">			
<div id="scanningStatus" class="warning" style="display: none; position: absolute; height: auto; width: 200px; left: 10px; top: 55px; border:1 solid white;">
	<img src="<spring:theme code="scanningImage"/>" title="" alt="" width="16" hight="16"> <fmt:message key="main.scanning"/> <span id="scanCount"></span>
</div>		

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
</c:if>

<table style="margin:0"><tr valign="middle">

	<c:if test="${model.leftframeSize ne '0'}">
    <td class="logo"><a href="help.view?" target="main"><img src="<spring:theme code="logoImage"/>" title="${help}" alt=""></a></td>

    <c:if test="${not model.musicFoldersExist}">
        <td style="padding-right:2em">
            <p class="warning"><fmt:message key="top.missing"/></p>
        </td>
    </c:if>
   </c:if>
    <td>
        <table><tr align="center">


			<c:if test="${model.leftframeSize ne '0'}">
			<td style="min-width:1em;padding-right:1.0em;padding-left:1em">
			</c:if>

			<c:if test="${model.leftframeSize eq '0'}">
			<td style="min-width:0em;padding-right:0em;padding-left:0em">
			</c:if>
		   </td>
			
		<c:if test="${model.showIconHome}">
			<sub:url value="home.view" var="homeUrl">
				<sub:param name="listType" value="${model.listType}"/>
				<sub:param name="listRows" value="${model.listRows}"/>
				<sub:param name="listColumns" value="${model.listColumns}"/>
			</sub:url>
           <td style="min-width:4em;padding-right:0.8em">
			<a href="${homeUrl}" target="main"><img src="<spring:theme code='homeImage'/>" title="${home}" alt="${home}"/></a><br>
			<a href="${homeUrl}" target="main">${home}</a>
		   </td>
	   </c:if>
	   <c:if test="${model.showIconArtist}">
           <td style="min-width:4em;padding-right:0.8em">
                <a href="artist.view?" target="main"><img src="<spring:theme code="artistImage"/>" title="${artist}" alt="${artist}"></a><br>
                <a href="artist.view?" target="main">${artist}</a>
            </td>
		</c:if>
		<c:if test="${model.showIconPlaying}">
            <td style="min-width:4em;padding-right:0.8em">
                <a href="nowPlaying.view?" target="main"><img src="<spring:theme code="nowPlayingImage"/>" title="${nowPlaying}" alt="${nowPlaying}"></a><br>
                <a href="nowPlaying.view?" target="main">${nowPlaying}</a>
            </td>
		</c:if>
   	   <c:if test="${model.showIconCover}">
	    <td style="min-width:4em;padding-right:0.8em">
			<a href="welcome.view?" target="main"><img src="<spring:theme code="coverImage"/>" title="${discover}" alt="${discover}"></a><br>
			<a href="welcome.view?" target="main">${discover}</a>
		</td>
	   </c:if>		
		<c:if test="${model.showIconStarred}">
			<td style="min-width:4em;padding-right:0.8em">
                <a href="starred.view?" target="main"><img src="<spring:theme code="starOnImage"/>" title="${starred}" alt="${starred}"></a><br>
                <a href="starred.view?" target="main">${starred}</a>
            </td>
		</c:if>
		<c:if test="${model.showIconGenre}">
			<td style="min-width:4em;padding-right:0.8em">
				<a href="genres.view?" target="main"><img src="<spring:theme code="genresImage"/>" title="${genres}" alt="${genres}"></a><br>
				<a href="genres.view?" target="main">${genres}</a>
			</td>
		</c:if>
		<c:if test="${model.showIconMoods}">
			<td style="min-width:4em;padding-right:0.8em">
				<a href="moods.view?" target="main"><img src="<spring:theme code="moodsImage"/>" title="${moods}" alt="${moods}"></a><br>
				<a href="moods.view?" target="main">${moods}</a>
			</td>
		</c:if>
		<c:if test="${model.showIconRadio}">
			<td style="min-width:4em;padding-right:0.8em">
				<a href="radio.view?" target="main"><img src="<spring:theme code="radioImage"/>" title="${radio}" alt="${radio}"></a><br>
				<a href="radio.view?" target="main">${radio}</a>
			</td>
		</c:if>
		<c:if test="${model.showIconPodcast}">
			<td style="min-width:4em;padding-right:0.8em">
                <a href="podcastReceiver.view?" target="main"><img src="<spring:theme code="podcastLargeImage"/>" title="${podcast}" alt="${podcast}"></a><br>
                <a href="podcastReceiver.view?" target="main">${podcast}</a>
            </td>
		</c:if>
		
		<c:if test="${model.showIconLastFM}">
			<td style="min-width:4em;padding-right:0.8em">
                <a href="lastfmUser.view?" target="main"><img src="<spring:theme code="lastfmLargeImage"/>" title="LastFM" alt="LastFM"></a><br>
                <a href="lastfmUser.view?" target="main">LastFM</a>
            </td>
		</c:if>
		
		<c:if test="${model.showIconSettings}">			
            <c:if test="${model.user.settingsRole}">
                <td style="min-width:4em;padding-right:0.8em">
                    <a href="settings.view?" target="main"><img src="<spring:theme code="settingsImage"/>" title="${settings}" alt="${settings}"></a><br>
                    <a href="settings.view?" target="main">${settings}</a>
                </td>
            </c:if>
		</c:if>
		<c:if test="${model.showIconStatus}">			
            <td style="min-width:4em;padding-right:0.8em">
                <a href="status.view?" target="main"><img src="<spring:theme code="statusImage"/>" title="${status}" alt="${status}"></a><br>
                <a href="status.view?" target="main">${status}</a>
            </td>
		</c:if>
		<c:if test="${model.showIconSocial}">			
            <c:if test="${not model.showRight}">			
            <td style="min-width:4em;padding-right:0.8em">
                <a href="chat.view?" target="main"><img src="<spring:theme code="chatImage"/>" title="${chat}" alt="${chat}"></a><br>
                <a href="chat.view?" target="main">${chat}</a>
            </td>
            </c:if>
		</c:if>
		<c:if test="${model.showIconHistory}">
            <td style="min-width:4em;padding-right:0.8em">
                <a href="history.view?" target="main"><img src="<spring:theme code="historyImage"/>" title="${history}" alt="${history}"></a><br>
                <a href="history.view?" target="main">${history}</a>
            </td>
		</c:if>
		<c:if test="${model.showIconStatistics}">
			<td style="min-width:4em;padding-right:0.8em">
                <a href="statistics.view?" target="main"><img src="<spring:theme code="chartImage"/>" title="${statistics}" alt="${statistics}"></a><br>
                <a href="statistics.view?" target="main">${statistics}</a>
            </td>
		</c:if>
			<!--
            <td style="min-width:4em;padding-right:0.8em">
                <a href="home.view?listSize=100&listType=highest" target="main"><img src="icons/default/TOP100.png" title="TOP100" alt="TOP100"></a><br>
                <a href="home.view?listSize=100&listType=highest" target="main">TOP100</a>
            </td>
            <td style="min-width:4em;padding-right:0.8em">
                <a href="home.view?listSize=100&listType=newest" target="main"><img src="icons/default/NEW100.png" title="NEW100" alt="NEW100"></a><br>
                <a href="home.view?listSize=100&listType=newest" target="main">NEW100</a>
            </td>
			-->
		<c:if test="${model.showIconPlaylists}">
			<td style="min-width:4em;padding-right:0.8em">
                <a href="loadPlaylist.view?" target="main"><img src="<spring:theme code="playlistImage"/>" title="${playlists}" alt="${playlists}"></a><br>
                <a href="loadPlaylist.view?" target="main">${playlists}</a>
            </td>
		</c:if>
		<c:if test="${model.showIconPlaylistEditor}">
			<td style="min-width:4em;padding-right:0.8em">
                <a href="playlistEditor.view?" target="main"><img src="<spring:theme code="playlistEditImage"/>" title="${editor}" alt="${editor}"></a><br>
                <a href="playlistEditor.view?" target="main">${editor}</a>
            </td>
		</c:if>
		<c:if test="${model.showIconMore}">			
			<td style="min-width:4em;padding-right:0.8em">
                <a href="more.view?" target="main"><img src="<spring:theme code="moreImage"/>" title="${more}" alt="${more}"></a><br>
                <a href="more.view?" target="main">${more}</a>
            </td>
		</c:if>
		<c:if test="${not model.newVersionAvailable}">  
			<c:if test="${model.showIconAbout}">			
				<td style="min-width:4em;padding-right:0.8em">
					<a href="help.view?" target="main"><img src="<spring:theme code="helpImage"/>" title="${help}" alt="${help}"></a><br>
					<a href="help.view?" target="main">${help}</a>
				</td>
			</c:if>
		</c:if>
		<c:if test="${model.user.adminRole}">
			<td style="min-width:4em;padding-right:0.8em">
				<a href="db.view?" target="_blank"><img src="<spring:theme code="dbImage"/>" title="${db}" alt="${db}"></a><br>
				<a href="db.view?" target="_blank">${db}</a>
			</td>
			<td style="min-width:4em;padding-right:0.8em">
				<a href="log.view?" target="main"><img src="<spring:theme code="logsImage"/>" title="${logs}" alt="${logs}"></a><br>
				<a href="log.view?" target="main">${logs}</a>
			</td>
		</c:if>
		
		<c:if test="${model.user.uploadRole}">
			<td style="width:4em;padding-right:0.8em"> <A HREF="javascript:newwindow()" ><img src="<spring:theme code="loadImage_mini"/>" title="${upload}"/>${upload}</a></td>
		</c:if>

		<td style="padding-left:5pt;text-align:center;">
			<p class="detail" style="line-height:1.5">
				<a href="j_spring_security_logout" target="_parent"><img src="<spring:theme code="logoffImage"/>"  style="width:auto;" title="<fmt:message key="top.logout"><fmt:param value="${model.user.username}"/></fmt:message>" alt="<fmt:message key="top.logout"><fmt:param value="${model.user.username}"/></fmt:message>"></a>

			<c:if test="${not model.licensed}">
			<br>
				<a href="premium.view" target="main"><img src="<spring:theme code="donateSmallImage"/>" alt=""></a>
				<c:choose>
					<c:when test="${model.licenseInfo.licenseValid}">
						<a href="premium.view" target="main"><fmt:message key="top.gotpremium"/></a>
					</c:when>
					<c:otherwise>
						<a href="premium.view" target="main"><fmt:message key="top.getpremium"/></a>
						<c:if test="${model.licenseInfo.trialDaysLeft gt 0}">
							<br>
							<a href="premium.view" target="main"><fmt:message key="top.trialdaysleft"><fmt:param value="${model.licenseInfo.trialDaysLeft}"/></fmt:message></a>
						</c:if>
					</c:otherwise>
				</c:choose>
			</c:if>						
			</p>
            </td>
	
			<!--
				<td>
				<table><tr align="middle">
				<td style="min-width:4em;padding-left: 2em; padding-right:2.5em">
				<embed src="http://www.adamdorman.com/flash/flip_clock_black_24_w-secs.swf" width="100" height="32" type="application/x-shockwave-flash"  wmode="opaque" quality="high"></embed>
				</td><tr>
				<td style="min-width:4em;padding-right:1.5em">			
			-->			
            <c:if test="${model.newVersionAvailable}">  
				<c:if test="${model.NotificationEnabled}"> 
					<c:if test="${model.user.adminRole}">   <!-- or ${model.newVersionAvailable} -->
							<td style="padding-left:15pt">
								<p class="warning">
								<a href="help.view" target="main">
								<img src="icons/default/new.png" width="48" hight="48" title="Version ${model.latestVersion}"/>
								</a>
								<!-- <fmt:message key="top.upgrade"><fmt:param value="${model.brand}"/><fmt:param value="${model.latestVersion}"/></fmt:message> -->
								</p>
							</td>
					</c:if>
				</c:if>
			</c:if>
        </tr></table>
    </td>

</tr></table>

</body></html>