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

<div id="content_2" class="content_main">
<!-- CONTENT -->

<h1>
	<img src="<spring:theme code="starOnImage"/>" alt="">
    <fmt:message key="starred.title"/>
</h1>

<h2>
    <c:forTokens items="artists albums songs links videos sets" delims=" " var="cat" varStatus="loopStatus">
        <c:if test="${loopStatus.count > 1}">&nbsp;<img src="<spring:theme code="sepImage"/>" alt="">&nbsp;</c:if>
        <sub:url var="url" value="starred.view">
            <sub:param name="listType" value="${cat}"/>
        </sub:url>
        <c:choose>
            <c:when test="${model.listType eq cat}">
                <span class="headerSelected"><fmt:message key="starred.${cat}.title"/></span>
            </c:when>
            <c:otherwise>
                <a href="${url}"><fmt:message key="starred.${cat}.title"/></a>
            </c:otherwise>
        </c:choose>
    </c:forTokens>
</h2>

<!--
<c:if test="${not empty model.albums}">
    <h2><fmt:message key="search.hits.albums"/></h2>
<div>
    <c:forEach items="${model.albums}" var="album" varStatus="loopStatus">

        <div class="albumThumb"> 
            <c:import url="coverArt.jsp">
                <c:param name="albumId" value="${album.id}"/>
                <c:param name="albumName" value="${album.title}"/>
                <c:param name="coverArtSize" value="${model.coverArtSize}"/>
                <c:param name="showLink" value="true"/>
                <c:param name="showZoom" value="false"/>
                <c:param name="showChange" value="false"/>
                <c:param name="appearAfter" value="${loopStatus.count * 30}"/>
            </c:import>
            <c:choose>
                <c:when test="${empty album.artist and empty album.title}">
                    <div class="detail"><fmt:message key="common.unknown"/></div>
                </c:when>
                <c:otherwise>
                    <div class="detail"><b><str:truncateNicely lower="12" upper="18">${album.artist}</str:truncateNicely></b></div>
                    <div class="detail"><str:truncateNicely lower="12" upper="18">${album.name}</str:truncateNicely></div>
                </c:otherwise>
            </c:choose>
        </div> 
    </c:forEach>

	</div>
    </c:if>
	-->
	

	<c:if test="${model.listType eq 'artists' and empty model.artists}">
	<p style="padding-top: 1em"><em><fmt:message key="starred.empty"/></em></p>
	</c:if>
	<c:if test="${model.listType eq 'artists'}">
    <h2><br></h2>
    <table style="border-collapse:collapse">
        <c:forEach items="${model.artists}" var="artist" varStatus="loopStatus">
            <sub:url value="/main.view" var="mainUrl">
                <sub:param name="path" value="${artist.path}"/>
            </sub:url>
            <tr>
			<td>
                <c:import url="playAddDownload.jsp">
                    <c:param name="id" value="${artist.id}"/>
					<c:param name="playEnabled" value="${model.user.streamRole and not model.partyMode and model.buttonVisibility.playVisible}"/>
					<c:param name="playAddEnabled" value="${model.user.streamRole and not model.partyMode and model.buttonVisibility.playAddVisible}"/>
					<c:param name="playMoreEnabled" value="${model.user.streamRole and not model.partyMode and model.buttonVisibility.playMoreVisible}"/>
					<c:param name="addEnabled" value="${model.user.streamRole and (not model.partyMode or not artist.directory) and model.buttonVisibility.addContextVisible}"/>
					<c:param name="addNextEnabled" value="${model.user.streamRole and (not model.partyMode or not artist.directory) and model.buttonVisibility.addNextVisible}"/>
					<c:param name="addLastEnabled" value="${model.user.streamRole and (not model.partyMode or not artist.directory) and model.buttonVisibility.addLastVisible}"/>						
					<c:param name="downloadEnabled" value="${model.user.downloadRole and not model.partyMode and model.buttonVisibility.downloadVisible}"/>
					<c:param name="starEnabled" value="true"/>
                    <c:param name="starred" value="${not empty artist.starredDate}"/>
                    <c:param name="asTable" value="true"/>
                </c:import>
				</td>
				<td style="padding-left:1.5em;padding-right:1.0em;">
				<c:import url="coverArtThumb.jsp">
							<c:param name="albumId" value="${artist.id}"/>
							<c:param name="artistName" value="${artist.name}"/>
							<c:param name="coverArtSize" value="50"/>
							<c:param name="scale" value="1.0"/>
							<c:param name="showLink" value="true"/>
							<c:param name="showZoom" value="false"/>
							<c:param name="showChange" value="false"/>
							<c:param name="showArtist" value="false"/>
							<c:param name="typArtist" value="true"/>
							<c:param name="appearAfter" value="${loopStatus.count * 15}"/>
				</c:import>
				</td>	
                <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:1em;padding-right:1em">
                    <a href="${mainUrl}">${artist.name}</a>
                </td>
            </tr>
        </c:forEach>
    </table>
	</c:if>

	<c:if test="${model.listType eq 'albums' and empty model.albums}">
	<p style="padding-top: 1em"><em><fmt:message key="starred.empty"/></em></p>
	</c:if>
	<c:if test="${model.listType eq 'albums'}">
    <h2><br></h2>
    <table style="border-collapse:collapse">
        <c:forEach items="${model.albums}" var="album" varStatus="loopStatus">

				<sub:url value="/main.view" var="mainUrl">
					<sub:param name="path" value="${album.path}"/>
				</sub:url>

				<tr>
				<td>
					<c:import url="playAddDownload.jsp">
						<c:param name="id" value="${album.id}"/>
                        <c:param name="playEnabled" value="${model.user.streamRole and not model.partyMode and model.buttonVisibility.playVisible}"/>
                        <c:param name="playAddEnabled" value="${model.user.streamRole and not model.partyMode and model.buttonVisibility.playAddVisible}"/>
                        <c:param name="playMoreEnabled" value="${model.user.streamRole and not model.partyMode and model.buttonVisibility.playMoreVisible}"/>
                        <c:param name="addEnabled" value="${model.user.streamRole and (not model.partyMode or not album.directory) and model.buttonVisibility.addContextVisible}"/>
                        <c:param name="addNextEnabled" value="${model.user.streamRole and (not model.partyMode or not album.directory) and model.buttonVisibility.addNextVisible}"/>
                        <c:param name="addLastEnabled" value="${model.user.streamRole and (not model.partyMode or not album.directory) and model.buttonVisibility.addLastVisible}"/>						
                        <c:param name="downloadEnabled" value="${model.user.downloadRole and not model.partyMode and model.buttonVisibility.downloadVisible}"/>
						<c:param name="starEnabled" value="true"/>
						<c:param name="starred" value="${not empty album.starredDate}"/>
						<c:param name="asTable" value="true"/>
					</c:import>
					</td>					
					<td style="padding-left:1.5em;padding-right:1.0em;">
					<c:import url="coverArtThumb.jsp">
								<c:param name="albumId" value="${album.id}"/>
								<c:param name="artistName" value="${album.name}"/>
								<c:param name="coverArtSize" value="50"/>
								<c:param name="scale" value="0.5"/>
								<c:param name="showLink" value="true"/>
								<c:param name="showZoom" value="false"/>
								<c:param name="showChange" value="false"/>
								<c:param name="showArtist" value="false"/>
								<c:param name="typArtist" value="true"/>
								<c:param name="appearAfter" value="${loopStatus.count * 15}"/>
					</c:import>
					</td>			
					
					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:1em;padding-right:1em">
						<a href="${mainUrl}">${album.albumSetName}</a>
					</td>
					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1em">
						<span class="detail">${album.artist}</span>
					</td>
				</tr>

			</c:forEach>
		</table>
	</c:if>

	<c:if test="${model.listType eq 'songs' and empty model.songs}">
	<p style="padding-top: 1em"><em><fmt:message key="starred.empty"/></em></p>
	</c:if>
	<c:if test="${model.listType eq 'songs'}">
    <h2>	
	<select id="moreActions" onchange="actionSelected(this.options[selectedIndex].id);" style="margin-bottom:1.0em">
		<option id="top" selected="selected"><fmt:message key="main.more"/></option>
		<optgroup label="<fmt:message key="playlist.more.starred"/>"/>
		<option id="savePlaylist"><fmt:message key="playlist.save"/></option>
		<optgroup label="<fmt:message key="main.more.selection"/>"/>
		<option id="selectAll"><fmt:message key="playlist.more.selectall"/></option>
		<option id="selectNone"><fmt:message key="playlist.more.selectnone"/></option>
		<option id="appendPlaylist"><fmt:message key="playlist.append"/></option>
		<option id="saveasPlaylist"><fmt:message key="playlist.save"/></option>
		</select>

		<div id="dialog-select-playlist" title="<fmt:message key="main.addtoplaylist.title"/>" style="display: none;">
			<p><fmt:message key="main.addtoplaylist.text"/></p>
			<div id="dialog-select-playlist-list"></div>
		</div>
		
		</h2>
		<table style="border-collapse:collapse">
			<c:forEach items="${model.songs}" var="song" varStatus="loopStatus">

				<sub:url value="/main.view" var="mainUrl">
					<sub:param name="path" value="${song.parentPath}"/>
				</sub:url>

				<tr>
				<td>
					<c:import url="playAddDownload.jsp">
						<c:param name="id" value="${song.id}"/>
						<c:param name="playEnabled" value="${model.user.streamRole and not model.partyMode and model.buttonVisibility.playVisible}"/>
						<c:param name="playAddEnabled" value="${model.user.streamRole and not model.partyMode and model.buttonVisibility.playAddVisible}"/>
						<c:param name="playMoreEnabled" value="${model.user.streamRole and not model.partyMode and model.buttonVisibility.playMoreVisible}"/>
						<c:param name="addEnabled" value="${model.user.streamRole and (not model.partyMode or not song.directory) and model.buttonVisibility.addContextVisible}"/>
						<c:param name="addNextEnabled" value="${model.user.streamRole and (not model.partyMode or not song.directory) and model.buttonVisibility.addNextVisible}"/>
						<c:param name="addLastEnabled" value="${model.user.streamRole and (not model.partyMode or not song.directory) and model.buttonVisibility.addLastVisible}"/>						
						<c:param name="downloadEnabled" value="${model.user.downloadRole and not model.partyMode and model.buttonVisibility.downloadVisible}"/>
						<c:param name="starEnabled" value="true"/>
						<c:param name="starred" value="${not empty song.starredDate}"/>
						<c:param name="video" value="${song.video and model.player.web}"/>
						<c:param name="asTable" value="true"/>
					</c:import>
				</td>
				<td style="padding-left:1.5em;padding-right:1.0em;">
				<c:import url="coverArtThumb.jsp">
							<c:param name="albumId" value="${song.id}"/>
							<c:param name="artistName" value="${song.name}"/>
							<c:param name="coverArtSize" value="50"/>
							<c:param name="scale" value="0.5"/>
							<c:param name="showLink" value="true"/>
							<c:param name="showZoom" value="false"/>
							<c:param name="showChange" value="false"/>
							<c:param name="showArtist" value="false"/>
							<c:param name="typArtist" value="true"/>
							<c:param name="appearAfter" value="${loopStatus.count * 15}"/>
				</c:import>
				</td>
					<td ${htmlclass} style="padding-left:0.25em"><input type="checkbox" class="checkbox" id="songIndex${loopStatus.count - 1}">
					<span id="songId${loopStatus.count - 1}" style="display: none">${song.id}</span></td>				
					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:1em;padding-right:1em">
							${song.title}
					</td>
					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1em">
						<a href="${mainUrl}"><span class="detail">${song.albumName}</span></a>
					</td>
					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1em">
						<span class="detail">${song.artist}</span>
					</td>
				</tr>
			</c:forEach>
    </table>
	</c:if>

	<c:if test="${model.listType eq 'videos' and empty model.videos}">	
	<p style="padding-top: 1em"><em><fmt:message key="starred.empty"/></em></p>
	</c:if>
	<c:if test="${model.listType eq 'videos'}">
	<h2><br></h2>
    <table style="border-collapse:collapse">
        <c:forEach items="${model.videos}" var="song" varStatus="loopStatus">

				<sub:url value="/main.view" var="mainUrl">
					<sub:param name="id" value="${song.id}"/>
				</sub:url>

				<tr>
					<c:import url="playAddDownload.jsp">
						<c:param name="id" value="${song.id}"/>
						<c:param name="playEnabled" value="${model.user.streamRole and not model.partyMode and model.buttonVisibility.playVisible}"/>
						<c:param name="playAddEnabled" value="${model.user.streamRole and not model.partyMode and model.buttonVisibility.playAddVisible}"/>
						<c:param name="playMoreEnabled" value="${model.user.streamRole and not model.partyMode and model.buttonVisibility.playMoreVisible}"/>
						<c:param name="addEnabled" value="${model.user.streamRole and (not model.partyMode or not song.directory) and model.buttonVisibility.addContextVisible}"/>
						<c:param name="addNextEnabled" value="${model.user.streamRole and (not model.partyMode or not song.directory) and model.buttonVisibility.addNextVisible}"/>
						<c:param name="addLastEnabled" value="${model.user.streamRole and (not model.partyMode or not song.directory) and model.buttonVisibility.addLastVisible}"/>						
						<c:param name="downloadEnabled" value="${model.user.downloadRole and not model.partyMode and model.buttonVisibility.downloadVisible}"/>
						<c:param name="starEnabled" value="true"/>
						<c:param name="starred" value="${not empty song.starredDate}"/>
						<c:param name="video" value="${song.video and model.player.web}"/>
						<c:param name="asTable" value="true"/>
					</c:import>
					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:0.75em;padding-right:1.25em">
							${song.title}
					</td>
					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.25em">
						<a href="${mainUrl}"><span class="detail">${song.albumName}</span></a>
					</td>
					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:0.25em">
						<span class="detail">${song.artist}</span>
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>

	<c:if test="${model.listType eq 'links' and empty model.links}">	
		<p style="padding-top: 1em"><em><fmt:message key="starred.empty"/></em></p>
	</c:if>	
	<c:if test="${model.listType eq 'links'}">
		<h2><br></h2>
		<table style="border-collapse:collapse">
			<c:forEach items="${model.links}" var="song" varStatus="loopStatus">

				<sub:url value="/main.view" var="mainUrl">
					<sub:param name="path" value="${song.parentPath}"/>
				</sub:url>

				<tr>
					<c:import url="playAddDownload.jsp">
						<c:param name="id" value="${song.id}"/>
						<c:param name="playEnabled" value="${model.user.streamRole and not model.partyMode and model.buttonVisibility.playVisible}"/>
						<c:param name="playAddEnabled" value="${model.user.streamRole and not model.partyMode and model.buttonVisibility.playAddVisible}"/>
						<c:param name="playMoreEnabled" value="${model.user.streamRole and not model.partyMode and model.buttonVisibility.playMoreVisible}"/>
						<c:param name="addEnabled" value="${model.user.streamRole and (not model.partyMode or not song.directory) and model.buttonVisibility.addContextVisible}"/>
						<c:param name="addNextEnabled" value="${model.user.streamRole and (not model.partyMode or not song.directory) and model.buttonVisibility.addNextVisible}"/>
						<c:param name="addLastEnabled" value="${model.user.streamRole and (not model.partyMode or not song.directory) and model.buttonVisibility.addLastVisible}"/>						
						<c:param name="downloadEnabled" value="${model.user.downloadRole and not model.partyMode and model.buttonVisibility.downloadVisible}"/>
						<c:param name="starEnabled" value="true"/>
						<c:param name="starred" value="${not empty song.starredDate}"/>
						<c:param name="video" value="${song.video and model.player.web}"/>
						<c:param name="asTable" value="true"/>
					</c:import>

					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:0.75em;padding-right:1.25em">
							${song.title}
					</td>

					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.25em">
						<a href="${mainUrl}"><span class="detail">${song.albumName}</span></a>
					</td>

					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:0.25em">
						<span class="detail">${song.artist}</span>
					</td>
				</tr>

			</c:forEach>
		</table>
	</c:if>

	<c:if test="${model.listType eq 'sets' and empty model.sets}">	
		<p style="padding-top: 1em"><em><fmt:message key="starred.empty"/></em></p>
	</c:if>	
	<c:if test="${model.listType eq 'sets'}">
		<h2><br></h2>
		<table style="border-collapse:collapse">
			<c:forEach items="${model.sets}" var="song" varStatus="loopStatus">

				<sub:url value="/main.view" var="mainUrl">
					<sub:param name="id" value="${song.id}"/>
				</sub:url>

				<tr>
				<td>
					<c:import url="playAddDownload.jsp">
						<c:param name="id" value="${song.id}"/>
						<c:param name="playEnabled" value="false"/>
						<c:param name="addEnabled" value="false"/>
                        <c:param name="downloadEnabled" value="false"/>
						<c:param name="starEnabled" value="true"/>
						<c:param name="starred" value="${not empty song.starredDate}"/>
						<c:param name="video" value="${song.video and model.player.web}"/>
						<c:param name="asTable" value="true"/>
					</c:import>
				</td>
				<td style="padding-left:1.5em;padding-right:1.0em;">
				<c:import url="coverArtThumb.jsp">
							<c:param name="albumId" value="${song.id}"/>
							<c:param name="artistName" value="${song.name}"/>
							<c:param name="coverArtSize" value="50"/>
							<c:param name="scale" value="1.0"/>
							<c:param name="showLink" value="true"/>
							<c:param name="showZoom" value="false"/>
							<c:param name="showChange" value="false"/>
							<c:param name="showArtist" value="false"/>
							<c:param name="typArtist" value="true"/>
							<c:param name="appearAfter" value="${loopStatus.count * 15}"/>
				</c:import>
				</td>	
					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:0.25em;padding-right:1.25em">
							${song.title}
					</td>

					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.25em">
						<a href="${mainUrl}"><span class="detail">${song.albumName}</span></a>
					</td>

					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.25em">
						<span class="detail">${song.artist}</span>
					</td>
				</tr>

			</c:forEach>
		</table>
	</c:if>	


<!-- CONTENT -->
</div>

</body>

<script type="text/javascript" language="javascript">

		function actionSelected(id) {
		
        if (id == "top") {
            return;
        } else if (id == "savePlaylist") {
            onSaveStarredPlaylist();			
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
	
	
		function onSaveStarredPlaylist() {
		
            selectAll(true);
			
			var mediaFileIds = new Array();
			
			for (var i = 0; i < ${fn:length(model.songs)}; i++) {
			
				var checkbox = $("#songIndex" + i);
				if (checkbox && checkbox.is(":checked")) {
					mediaFileIds.push($("#songId" + i).html());
				}
			}
			playlistService.saveStarredPlaylist(mediaFileIds, function (){
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
		$("#content_2").mCustomScrollbar({
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

$(".content_main").resize(function(e){
	$(".content_main").mCustomScrollbar("update");
});
</script>
</c:if>	
</html>