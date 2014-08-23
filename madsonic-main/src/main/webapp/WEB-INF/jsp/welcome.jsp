<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>


    <c:if test="${model.customScrollbar}">
	<link href="<c:url value="/style/customScrollbar.css"/>" rel="stylesheet">
	<script type="text/javascript" src="<c:url value="/script/jquery.mousewheel.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/script/jquery.mCustomScrollbar.js"/>"></script>
    </c:if>	

    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/interface/starService.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/playlistService.js"/>"></script>
	
    <script type="text/javascript" language="javascript">

	    function init() {

        $("#dialog-select-playlist").dialog({resizable: true, height: 350, position: [300,'center'], modal: true, autoOpen: false,
            buttons: {
                "<fmt:message key="common.cancel"/>": function() {
                    $(this).dialog("close");
                }
            }});
			
    }
	
        function toggleStar(mediaFileId, imageId) {
            if ($(imageId).attr("src").indexOf("<spring:theme code="ratingOnImage"/>") != -1) {
                $(imageId).attr("src", "<spring:theme code="ratingOffImage"/>");
                starService.unstar(mediaFileId);
            }
            else if ($(imageId).attr("src").indexOf("<spring:theme code="ratingOffImage"/>") != -1) {
                $(imageId).attr("src", "<spring:theme code="ratingOnImage"/>");
                starService.star(mediaFileId);
            }
        }
	</script>
</head>
<body class="mainframe bgcolor1" onload="init();">

<!-- content block -->

<div id="content_4" class="content_welcome">
<!-- CONTENT -->

<h1>
	<img src="<spring:theme code="coverImage"/>" alt="">
    <fmt:message key="welcome.title"/> 
</h1>

<c:if test="${empty model.artists and empty model.albums and empty model.songs and empty model.topPlayedSongs and empty model.lastPlayedSongs and empty model.topTrackSongs}">
    <p style="padding-top: 1em"><em><fmt:message key="starred.empty"/></em></p>
</c:if>

<h2>
	<!-- temp. remove artists -->
    <c:forTokens items="artists albums songs topplayed toptracks lastplayed" delims=" " var="cat" varStatus="loopStatus">
        <c:if test="${loopStatus.count > 1}">&nbsp;<img src="<spring:theme code="sepImage"/>" alt="">&nbsp;</c:if>
        <sub:url var="url" value="welcome.view">
            <sub:param name="listType" value="${cat}"/>
        </sub:url>
        <c:choose>
            <c:when test="${model.listType eq cat}">
                <span class="headerSelected"><fmt:message key="welcome.${cat}.title"/></span>
            </c:when>
            <c:otherwise>
                <a href="${url}"><fmt:message key="welcome.${cat}.title"/></a>
            </c:otherwise>
        </c:choose>
    </c:forTokens>
</h2>
<p></p>
<br>
<c:choose>
	<c:when test="${model.listType eq 'artists'}">
	<!-- artists --> 
	<c:if test="${not empty model.artists}">
	<!--<h2><fmt:message key="search.hits.artists"/></h2> -->
		<h1></h1>
		<div class="coverbox">
		<table style="border-collapse:collapse">
			<c:forEach items="${model.artists}" var="artist" varStatus="loopStatus">
				<sub:url value="/main.view" var="mainUrl">
					<sub:param name="path" value="${artist.path}"/>
				</sub:url>
				<tr>
				<td style="padding-left:0.5em;padding-right:1.5em;padding-bottom:0.8em">
					<c:import url="playAddDownload.jsp">
						<c:param name="id" value="${artist.id}"/>
						<c:param name="playEnabled" value="false"/>
						<c:param name="addEnabled" value="false"/>
						<c:param name="downloadEnabled" value="false"/>
						<c:param name="starEnabled" value="true"/>
						<c:param name="starred" value="${not empty artist.starredDate}"/>
						<c:param name="asTable" value="false"/>
					</c:import>
				</td>
					<td>
					<c:import url="coverArt.jsp">
						<c:param name="albumId" value="${artist.id}"/>
						<c:param name="artistName" value="${artist.name}"/>
						<c:param name="coverArtSize" value="45"/>
						<c:param name="showLink" value="true"/>
						<c:param name="showZoom" value="false"/>
						<c:param name="showChange" value="false"/>
						<c:param name="showArtist" value="false"/>
						<c:param name="typArtist" value="true"/>
						<c:param name="appearAfter" value="20"/>
					</c:import>
					</td>
					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:1.25em;padding-right:1.25em;padding-bottom:1.0em">
						<h1><a href="${mainUrl}">${artist.name}</a></h1>
					</td>
					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:1.25em;padding-right:1.25em;padding-bottom:1.0em">
						<str:truncateNicely upper="450">${artist.comment}</str:truncateNicely>
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
	</c:when>
</c:choose>

<c:choose>
	<c:when test="${model.listType eq 'albums'}">
	<!-- albums -->
	<c:if test="${not empty model.albums}">
	<!--<h2><fmt:message key="search.hits.albums"/></h2>-->
		<h1></h1>
		<div class="coverbox">
		<table style="border-collapse:collapse">
			<c:forEach items="${model.albums}" var="album" varStatus="loopStatus">

				<sub:url value="/main.view" var="mainUrl">
					<sub:param name="path" value="${album.path}"/>
				</sub:url>

				<sub:url value="/main.view" var="artistUrl">
					<sub:param name="path" value="${album.parentPath}"/>
				</sub:url>				
				
				<tr>
				<td style="padding-left:0.5em;padding-right:1.5em;padding-bottom:1.0em">
					<c:import url="playAddDownload.jsp">
						<c:param name="id" value="${album.id}"/>
						<c:param name="playEnabled" value="false"/>
						<c:param name="addEnabled" value="false"/>
						<c:param name="downloadEnabled" value="false"/>
						<c:param name="starEnabled" value="true"/>
						<c:param name="starred" value="${not empty album.starredDate}"/>
						<c:param name="asTable" value="false"/>
					</c:import>
				</td>
					<td>
					<c:import url="coverArt.jsp">
						<c:param name="albumId" value="${album.id}"/>
						<c:param name="artistName" value="${album.name}"/>
						<c:param name="coverArtSize" value="45"/>
						<c:param name="showLink" value="true"/>
						<c:param name="showZoom" value="false"/>
						<c:param name="showChange" value="false"/>
						<c:param name="showArtist" value="false"/>
						<c:param name="typArtist" value="true"/>
						<c:param name="appearAfter" value="20"/>
					</c:import>
					</td>
					
					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:1.25em;padding-right:3.25em;padding-bottom:1.0em;">
						<h1><a href="${mainUrl}">${album.albumSetName}</a></h1>
					</td>

					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:0.25em;padding-right:1.25em;padding-bottom:1.0em;">
						<h1><a href="${artistUrl}">${album.artist}</a></h1>
					</td>
				</tr>

			</c:forEach>
		</table>
	</c:if>
	</div>
	</c:when>
</c:choose>

<c:choose>
	<c:when test="${model.listType eq 'songs'}">
	<!-- songs --> 
	<c:if test="${not empty model.songs}">
		<!--<h2><fmt:message key="search.hits.songs"/></h2>-->
		<h1></h1>
		<div class="coverbox">
		<table style="border-collapse:collapse;white-space:nowrap;">
			<c:forEach items="${model.songs}" var="song" varStatus="loopStatus">

				<sub:url value="/main.view" var="mainUrl">
					<sub:param name="path" value="${song.parentPath}"/>
				</sub:url>

				<sub:url value="/main.view" var="artistUrl">
					<c:if test="${not empty song.artistPath}">
						<sub:param name="path" value="${song.artistPath}"/>
					</c:if>
					<c:if test="${empty song.artistPath}">
						<sub:param name="path" value="${song.parentPath}"/>
					</c:if>
				</sub:url>	
				
				<tr>
					<td style="padding-left:0.5em;padding-right:1.5em;">
					<c:import url="playAddDownload.jsp">
						<c:param name="id" value="${song.id}"/>
						<c:param name="playEnabled" value="false"/>
						<c:param name="addEnabled" value="false"/>
						<c:param name="downloadEnabled" value="false"/>
						<c:param name="starEnabled" value="true"/>
						<c:param name="starred" value="${not empty song.starredDate}"/>
						<c:param name="video" value="${song.video and model.player.web}"/>
						<c:param name="asTable" value="false"/>
					</c:import>
					</td>

					<td>
					<c:import url="coverArt.jsp">
						<c:param name="albumId" value="${song.id}"/>
						<c:param name="artistName" value="${song.name}"/>
						<c:param name="coverArtSize" value="45"/>
						<c:param name="showLink" value="true"/>
						<c:param name="showZoom" value="false"/>
						<c:param name="showChange" value="false"/>
						<c:param name="showArtist" value="false"/>
						<c:param name="typArtist" value="true"/>
						<c:param name="appearAfter" value="20"/>
					</c:import>
					</td>				

					<td>
					<c:import url="playAddDownload.jsp">
						<c:param name="id" value="${song.id}"/>
						<c:param name="playEnabled" value="true"/>
						<c:param name="addEnabled" value="true"/>
						<c:param name="downloadEnabled" value="false"/>
						<c:param name="starEnabled" value="false"/>
						<c:param name="starred" value=""/>
						<c:param name="video" value="${song.video and model.player.web}"/>
						<c:param name="asTable" value="false"/>
					</c:import>
					</td>								
					
					<span id="songId${loopStatus.count - 1}" style="display: none"><h1>${song.id}</h1></span></td>				
		
					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:1.25em;padding-right:1.55em;">
					<str:truncateNicely upper="40">${song.title}</str:truncateNicely>
					</td>

					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:3.25em">
						<h1><a href="${mainUrl}"><str:truncateNicely upper="40">${song.albumName}</str:truncateNicely></a></h1>
					</td>

					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.25em;">
						<h1><a href="${artistUrl}">${song.artist}</a></h1>
					</td>
				</tr>

			</c:forEach>
		</table>
	</c:if>
	</div>
	</c:when>
</c:choose>


<c:choose>
	<c:when test="${model.listType eq 'topplayed'}">
			<c:if test="${not empty model.topPlayedSongs}">
			<div class="coverbox">

			<table style="border-collapse:collapse;white-space:nowrap;">

					<c:forEach items="${model.topPlayedSongs}" var="song" varStatus="loopStatus">

						<sub:url value="/main.view" var="mainUrl">
							<sub:param name="path" value="${song.parentPath}"/>
						</sub:url>
						
						<sub:url value="/main.view" var="artistUrl">
							<c:if test="${not empty song.artistPath}">
								<sub:param name="path" value="${song.artistPath}"/>
							</c:if>
							<c:if test="${empty song.artistPath}">
								<sub:param name="path" value="${song.parentPath}"/>
							</c:if>
						</sub:url>	

						<tr>
							<td style="padding-left:0.5em;padding-right:1.5em;">
								<c:import url="playAddDownload.jsp">
									<c:param name="id" value="${song.id}"/>
									<c:param name="playEnabled" value="false"/>
									<c:param name="addEnabled" value="false"/>
									<c:param name="downloadEnabled" value="false"/>
									<c:param name="starEnabled" value="true"/>
									<c:param name="starred" value="${not empty song.starredDate}"/>
									<c:param name="asTable" value="false"/>
								</c:import>
							</td>
							<td>
								<c:import url="coverArt.jsp">
									<c:param name="albumId" value="${song.id}"/>
									<c:param name="artistName" value="${song.name}"/>
									<c:param name="coverArtSize" value="45"/>
									<c:param name="showLink" value="true"/>
									<c:param name="showZoom" value="false"/>
									<c:param name="showChange" value="false"/>
									<c:param name="showArtist" value="false"/>
									<c:param name="typArtist" value="true"/>
									<c:param name="appearAfter" value="20"/>
								</c:import>
							<td>
							<c:import url="playAddDownload.jsp">
								<c:param name="id" value="${song.id}"/>
								<c:param name="playEnabled" value="true"/>
								<c:param name="addEnabled" value="true"/>
								<c:param name="downloadEnabled" value="false"/>
								<c:param name="starEnabled" value="false"/>
								<c:param name="starred" value=""/>
								<c:param name="video" value="${song.video and model.player.web}"/>
								<c:param name="asTable" value="false"/>
							</c:import>
							</td>								
								
							</td>	
							<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:0.25em;padding-right:1.25em">
								<str:truncateNicely upper="40">${song.title}</str:truncateNicely>
							</td>
							<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.25em">
								<h1>
								<a href="${mainUrl}"><str:truncateNicely upper="40">${song.albumName}</str:truncateNicely></a>
								</h1>
							</td>
							<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.50em">
								<h1>
								<a href="${artistUrl}"><str:truncateNicely upper="40">${song.artist}</str:truncateNicely></a>
								</h1>
							</td>
							<c:choose>
								<c:when test="${model.listType eq 'topplayed'}">
									<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.50em">
										<span class="detailcolor">(${song.playCount}x)</span>
									</td>
								</c:when>
							</c:choose>
							<c:choose>
								<c:when test="${model.listType eq 'overall'}">
									<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.50em">
										<span class="detailcolor">(${song.playCount}x)</span>
									</td>
								</c:when>
							</c:choose>				
							<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:0.75em">
								<span class="detail">${fn:substring(song.lastPlayed, 0, 16)}</span>
							</td>
							</tr>
							
					</c:forEach>
				</table>
			</c:if>
			</div>
	</c:when>
</c:choose>

<c:choose>
	<c:when test="${model.listType eq 'lastplayed'}">
			<c:if test="${not empty model.lastPlayedSongs}">
			<h2></h2>
		<div class="coverbox">
		<table style="border-collapse:collapse;white-space:nowrap;">

					<c:forEach items="${model.lastPlayedSongs}" var="song" varStatus="loopStatus">

						<sub:url value="/main.view" var="mainUrl">
							<sub:param name="path" value="${song.parentPath}"/>
						</sub:url>
						
						<sub:url value="/main.view" var="artistUrl">
							<c:if test="${not empty song.artistPath}">
								<sub:param name="path" value="${song.artistPath}"/>
							</c:if>
							<c:if test="${empty song.artistPath}">
								<sub:param name="path" value="${song.parentPath}"/>
							</c:if>
						</sub:url>	
						<tr>
							<c:import url="playAddDownload.jsp">
								<c:param name="id" value="${song.id}"/>
								<c:param name="playEnabled" value="${model.user.streamRole and not model.partyModeEnabled}"/>
								<c:param name="addEnabled" value="${model.user.streamRole and (not model.partyModeEnabled or not song.directory)}"/>
								<c:param name="downloadEnabled" value="false"/>
								<c:param name="starEnabled" value="true"/>
								<c:param name="starred" value="${not empty song.starredDate}"/>
								<c:param name="video" value="false"/>
								<c:param name="asTable" value="true"/>
							</c:import>

							<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:0.25em;padding-right:1.25em">
								<str:truncateNicely upper="40">${song.title}</str:truncateNicely>
							</td>

							<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.25em">
								<a href="${mainUrl}"><span class="detail"><str:truncateNicely upper="40">${song.albumName}</str:truncateNicely></span></a>
							</td>

							<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.50em">
								<a href="${artistUrl}"><span class="detail"><str:truncateNicely upper="40">${song.artist}</str:truncateNicely></span></a>
							</td>
							<c:choose>
								<c:when test="${model.listType eq 'topplayed'}">
									<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.50em">
										<span class="detailcolor">(${song.playCount}x)</span>
									</td>
								</c:when>
							</c:choose>
							<c:choose>
								<c:when test="${model.listType eq 'overall'}">
									<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.50em">
										<span class="detailcolor">(${song.playCount}x)</span>
									</td>
								</c:when>
							</c:choose>				
							<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:0.75em">
								<span class="detail">${fn:substring(song.lastPlayed, 0, 16)}</span>
							</td>
							</tr>
							
					</c:forEach>
				</table>
			</c:if>
			</div>
	</c:when>
</c:choose>

<c:choose>
	<c:when test="${model.listType eq 'toptracks'}">
			<c:if test="${not empty model.topTrackSongs}">
			<div class="coverbox">

			<table style="border-collapse:collapse;white-space:nowrap;">

					<c:forEach items="${model.topTrackSongs}" var="song" varStatus="loopStatus">

						<sub:url value="/main.view" var="mainUrl">
							<sub:param name="path" value="${song.parentPath}"/>
						</sub:url>
						
						<sub:url value="/main.view" var="artistUrl">
							<c:if test="${not empty song.artistPath}">
								<sub:param name="path" value="${song.artistPath}"/>
							</c:if>
							<c:if test="${empty song.artistPath}">
								<sub:param name="path" value="${song.parentPath}"/>
							</c:if>
						</sub:url>	

						<tr>
							<td style="padding-left:0.5em;padding-right:1.5em;">
								<c:import url="playAddDownload.jsp">
									<c:param name="id" value="${song.id}"/>
									<c:param name="playEnabled" value="false"/>
									<c:param name="addEnabled" value="false"/>
									<c:param name="downloadEnabled" value="false"/>
									<c:param name="starEnabled" value="true"/>
									<c:param name="starred" value="${not empty song.starredDate}"/>
									<c:param name="asTable" value="false"/>
								</c:import>
							</td>
							<td>
								<c:import url="coverArt.jsp">
									<c:param name="albumId" value="${song.id}"/>
									<c:param name="artistName" value="${song.name}"/>
									<c:param name="coverArtSize" value="45"/>
									<c:param name="showLink" value="true"/>
									<c:param name="showZoom" value="false"/>
									<c:param name="showChange" value="false"/>
									<c:param name="showArtist" value="false"/>
									<c:param name="typArtist" value="true"/>
									<c:param name="appearAfter" value="20"/>
								</c:import>
							<td>
							<c:import url="playAddDownload.jsp">
								<c:param name="id" value="${song.id}"/>
								<c:param name="playEnabled" value="true"/>
								<c:param name="addEnabled" value="true"/>
								<c:param name="downloadEnabled" value="false"/>
								<c:param name="starEnabled" value="false"/>
								<c:param name="starred" value=""/>
								<c:param name="video" value="${song.video and model.player.web}"/>
								<c:param name="asTable" value="false"/>
							</c:import>
							</td>								
								
							</td>	
							<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:0.25em;padding-right:1.25em">
								<str:truncateNicely upper="40">${song.title}</str:truncateNicely>
							</td>
							<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.25em">
								<h1>
								<a href="${mainUrl}"><str:truncateNicely upper="40">${song.albumName}</str:truncateNicely></a>
								</h1>
							</td>
							<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.50em">
								<h1>
								<a href="${artistUrl}"><str:truncateNicely upper="40">${song.artist}</str:truncateNicely></a>
								</h1>
							</td>
							<c:choose>
								<c:when test="${model.listType eq 'topplayed'}">
									<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.50em">
										<span class="detailcolor">(${song.playCount}x)</span>
									</td>
								</c:when>
							</c:choose>
							<c:choose>
								<c:when test="${model.listType eq 'overall'}">
									<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.50em">
										<span class="detailcolor">(${song.playCount}x)</span>
									</td>
								</c:when>
							</c:choose>				
							</tr>
							
					</c:forEach>
				</table>
			</c:if>
			</div>
	</c:when>
</c:choose>


<!-- CONTENT -->
</div>
</div>
</body>

<script type="text/javascript" language="javascript">

		function actionSelected(id) {
		
        if (id == "top") {
            return;
        } else if (id == "savePlaylist") {
            onSavePlaylist();
        } else if (id == "selectAll") {
            selectAll(true);
        } else if (id == "selectNone") {
            selectAll(false);
        } else if (id == "appendPlaylist") {
            onAppendPlaylist();
        } else if (id == "saveasPlaylist") {
            onSaveasPlaylist();
        }        
        $("#moreActions").prop("selectedIndex", 0);
        }

        function getSelectedIndexes() {
        var result = "";
			for (var i = 0; i < ${fn:length(model.songs)}; i++) {
				var checkbox = $("#songIndex" + i);
				if (checkbox != null  && checkbox.is(":checked")) {
					result += "i=" + i + "&";
				}
			}
			return result;
		}

		function selectAll(b) {
			for (var i = 0; i < ${fn:length(model.songs)}; i++) {
				var checkbox = $("#songIndex" + i);
				if (checkbox != null) {
					if (b) {
						checkbox.attr("checked", "checked");
					} else {
						checkbox.removeAttr("checked");
					}
				}
			}
		}
		
		function onAppendPlaylist() {
			playlistService.getWritablePlaylists(playlistCallback);
		}
		
		function playlistCallback(playlists) {
		
			$("#dialog-select-playlist-list").empty();
			for (var i = 0; i < playlists.length; i++) {
				var playlist = playlists[i];
				$("<p class='dense'><b><a href='#' onclick='appendPlaylist(" + playlist.id + ")'>" + playlist.name + "</a></b></p>").appendTo("#dialog-select-playlist-list");
			}
			$("#dialog-select-playlist").dialog("open");
		}
		
		function appendPlaylist(playlistId) {
			$("#dialog-select-playlist").dialog("close");

			var mediaFileIds = new Array();
			
			for (var i = 0; i < ${fn:length(model.songs)}; i++) {
			
				var checkbox = $("#songIndex" + i);
				if (checkbox && checkbox.is(":checked")) {
					mediaFileIds.push($("#songId" + i).html());
				}
			}
			playlistService.appendToPlaylist(playlistId, mediaFileIds, function (){parent.left.updatePlaylists();});
		}
		
		function onSavePlaylist() {
		
            selectAll(true);
			
			var mediaFileIds = new Array();
			
			for (var i = 0; i < ${fn:length(model.songs)}; i++) {
			
				var checkbox = $("#songIndex" + i);
				if (checkbox && checkbox.is(":checked")) {
					mediaFileIds.push($("#songId" + i).html());
				}
			}
			playlistService.savePlaylist(mediaFileIds, function (){
			parent.left.updatePlaylists();
            $().toastmessage("showSuccessToast", "<fmt:message key="playlist.toast.saveasplaylist"/>");
			});
		}
	
		function onSaveasPlaylist() {
		
			var mediaFileIds = new Array();
			
			for (var i = 0; i < ${fn:length(model.songs)}; i++) {
			
				var checkbox = $("#songIndex" + i);
				if (checkbox && checkbox.is(":checked")) {
					mediaFileIds.push($("#songId" + i).html());
				}
			}
			playlistService.savePlaylist(mediaFileIds, function (){
			parent.left.updatePlaylists();
            $().toastmessage("showSuccessToast", "<fmt:message key="playlist.toast.saveasplaylist"/>");
			});
		}	
		
</script>

<c:if test="${model.customScrollbar}">
<script type="text/javascript">        
(function($){
	$(window).load(function(){
		$("#content_4").mCustomScrollbar({
			set_width:false, /*optional element width: boolean, pixels, percentage*/
			set_height:false, /*optional element height: boolean, pixels, percentage*/
			horizontalScroll:false, /*scroll horizontally: boolean*/
			scrollInertia:400, /*scrolling inertia: integer (milliseconds)*/
			scrollEasing:"easeOutCubic", /*scrolling easing: string*/
			mouseWheel:"auto", /*mousewheel support and velocity: boolean, "auto", integer*/
			autoDraggerLength:true, /*auto-adjust scrollbar dragger length: boolean*/
			scrollButtons:{ /*scroll buttons*/
				enable:true, /*scroll buttons support: boolean*/
				scrollType:"pixels", /*scroll buttons scrolling type: "continuous", "pixels"*/
				scrollSpeed:35, /*scroll buttons continuous scrolling speed: integer*/
				scrollAmount:250 /*scroll buttons pixels scroll amount: integer (pixels)*/
			},
			advanced:{
				updateOnBrowserResize:true, /*update scrollbars on browser resize (for layouts based on percentages): boolean*/
				updateOnContentResize:true, /*auto-update scrollbars on content resize (for dynamic content): boolean*/
				autoExpandHorizontalScroll:false /*auto expand width for horizontal scrolling: boolean*/
			},
			callbacks:{
				onScroll:function(){}, /*user custom callback function on scroll event*/
				onTotalScroll:function(){}, /*user custom callback function on bottom reached event*/
				onTotalScrollOffset:0 /*bottom reached offset: integer (pixels)*/
			}
		});
	});
})(jQuery);

$(".content_welcome").resize(function(e){
	$(".content_welcome").mCustomScrollbar("update");
});
</script>
</c:if>

	
</html>