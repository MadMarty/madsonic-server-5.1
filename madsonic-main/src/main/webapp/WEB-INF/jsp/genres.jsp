<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<html><head>

	<%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
	
	<script type="text/javascript" language="javascript">
       function hideGenre() {
            $('#genreList').hide('swing');
            $('#showGenre').show();
			$('#hideGenre').hide();
			
			
		}
		
        function showGenre() {
            $("#genreList").show('blind');
			$('#hideGenre').show();
            $('#showGenre').hide();
        }
    </script>
</head>
<body class="mainframe bgcolor1">

<div id="content_2">

<h1>

<img src="<spring:theme code="genresImage"/>" alt=""> Genre</h1>
<!-- TODO: songs -->
<h2><c:forTokens items="artists albums songs" delims=" " var="cat" varStatus="loopStatus">
<c:if test="${loopStatus.count > 1}">&nbsp;<img src="<spring:theme code="sepImage"/>" alt="">&nbsp;</c:if>
<sub:url var="url" value="genres.view">
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

<div id="toogleGenre">
<div id="showGenre" style="display:inline;"><a href="javascript:noop()" onclick="showGenre()"> Genre <img src="/icons/black/show.png" alt=""></a></div> 
<div id="hideGenre" style="display:inline;"><a href="javascript:noop()" onclick="hideGenre()"> Genre <img src="/icons/black/hide.png" alt=""></a></div> 
</div>

<div id="genreList" class="genre" style="width:95%;max-width:1100px;padding-bottom:20px;">

	<c:if test="${model.listType eq 'artists'}">
	<div class="genre" style="width:85%; max-width: 1100px; line-height: 1.5; padding: 10px;">
	<c:forEach items="${model.genreArtistList}" var="genreArtistList">
	<span style="font-size: ${genreArtistList.occurrence+11}px;">
	<sub:url var="url" value="genres.view">
		<sub:param name="listType" value="${model.listType}"/>
		<sub:param name="genre" value="${genreArtistList.name}"/>
	</sub:url>
	<a href="${url}" title="${genreArtistList.artistCount}x">${genreArtistList.name}</a>
	</span>
	</c:forEach>
	</div></c:if>

	<c:if test="${model.listType eq 'albums'}">
	<div class="genre" style="width:85%;max-width: 1100px;line-height: 1.5; padding: 10px;">
	<c:forEach items="${model.genreAlbumList}" var="genreAlbumList">
	<span style="font-size: ${genreAlbumList.occurrence+11}px;">
	<sub:url var="url" value="genres.view">
		<sub:param name="listType" value="${model.listType}"/>
		<sub:param name="genre" value="${genreAlbumList.name}"/>
	</sub:url>
	<a href="${url}" title="${genreAlbumList.albumCount}x">${genreAlbumList.name}</a>
	</span>
	</c:forEach>
	</div></c:if>

	<c:if test="${model.listType eq 'songs'}">
	<div class="genre" style="width:85%;max-width: 1100px;line-height: 1.5; padding: 10px;">
	<c:forEach items="${model.genreSongList}" var="genreSongList">
	<span style="font-size: ${genreSongList.occurrence+11}px;">
	<sub:url var="url" value="genres.view">
		<sub:param name="listType" value="${model.listType}"/>
		<sub:param name="genre" value="${genreSongList.name}"/>
	</sub:url>
	<a href="${url}" title="${genreSongList.songCount}x">${genreSongList.name}</a>
	</span>
	</c:forEach>
	</div></c:if>
</div>

	<div id="list" class="genre" style="width:95%;max-width: 1100px;">

	<c:if test="${model.listType eq 'songs'}">
				
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
						<c:param name="playEnabled" value="true"/>
						<c:param name="addEnabled" value="true"/>
						<c:param name="downloadEnabled" value="true"/>
						<c:param name="starEnabled" value="false"/>
						<c:param name="starred" value="${not empty song.starredDate}"/>
						<c:param name="video" value="${song.video and model.player.web}"/>
						<c:param name="asTable" value="false"/>
					</c:import>
					</td>								
					<td>
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
						<c:param name="appearAfter" value="5"/>
					</c:import>
					</td>				
					
					<span id="songId${loopStatus.count - 1}" style="display: none">${song.id}</span></td>				
		
					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:1.25em;padding-right:1.55em;">
					<str:truncateNicely upper="40">${song.title}</str:truncateNicely>
					</td>

					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:3.25em">
						<a href="${mainUrl}"><str:truncateNicely upper="40">${song.albumName}</str:truncateNicely></a>
					</td>

					<td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.25em;">
						<a href="${artistUrl}">${song.artist}</a>
					</td>
				</tr>
			</c:forEach>
		</table>
				
	</c:if>
				

	<c:if test="${model.listType ne 'songs' and not empty model.albums}">
	
			<c:forEach items="${model.albums}" var="album" varStatus="loopStatus">
							<div style="float:left;padding:6px"> 
						
								<c:import url="coverArt.jsp">
									<c:param name="albumId" value="${album.id}"/>
									<c:param name="albumName" value="${album.albumSetName}"/>
									<c:param name="artistName" value="${album.artist}"/>
									<c:param name="coverArtSize" value="118"/>
									<c:param name="coverArtPath" value="${album.coverArtPath}"/>
									<c:param name="showLink" value="true"/>
									<c:param name="showZoom" value="false"/>
									<c:param name="showChange" value="false"/>
									<c:param name="appearAfter" value="${loopStatus.count * 20}"/>
									<c:param name="showPlayAlbum" value="true"/>									
									<c:param name="showAddAlbum" value="true"/>
									<c:param name="showTopTrack" value="true"/>
									<c:param name="extraPadding" value="true"/>	
								</c:import>

								<div class="detailmini">
								<c:if test="${not empty album.playCount}">

								
								<div class="detailcolordark">
									<fmt:message key="home.playcount"><fmt:param value="${album.playCount}"/></fmt:message>
								</div>
								</c:if>
								<c:if test="${not empty album.lastPlayed}">
								<div class="detailcolordark">
									<fmt:formatDate value="${album.lastPlayed}" dateStyle="short" var="lastPlayedDate"/>
									<fmt:message key="home.lastplayed"><fmt:param value="${lastPlayedDate}"/></fmt:message>
								</div>
								</c:if>
								<c:if test="${not empty album.created}">
								<div class="detailcolordark">
									<fmt:formatDate value="${album.created}" dateStyle="short" var="creationDate"/>
									<fmt:message key="home.created"><fmt:param value="${creationDate}"/></fmt:message>
								</div>
				                                    <c:if test="${not empty album.year}">
				                                        ${album.year}
				                                    </c:if>
								</c:if>
								<c:if test="${not empty album.rating}">
									<c:import url="rating.jsp">
										<c:param name="readonly" value="true"/>
										<c:param name="rating" value="${album.rating}"/>
									</c:import>
								</c:if>
								</div>

							<c:choose>
								<c:when test="${empty album.artist and empty album.albumTitle}">
								<div class="detail"><fmt:message key="common.unknown"/></div>
								</c:when>
								<c:otherwise>

								<sub:url value="main.view" var="parent">
								<sub:param name="id" value="${album.parentId}"/>
								</sub:url>

									<div class="detailcolor"><a href="${parent}"><str:truncateNicely lower="18" upper="18">${album.artist}</str:truncateNicely></a></div>
									
										<c:choose>
											<c:when test="${fn:startsWith(album.albumTitle,'[')}">
												<div class="detail"><str:truncateNicely upper="15">${fn:split(album.albumTitle,']')[1]}</str:truncateNicely></div>
											</c:when>
											<c:otherwise>
												<div class="detail"><str:truncateNicely upper="15">${album.albumTitle}</str:truncateNicely></div>
											</c:otherwise>
										</c:choose>
									
								</c:otherwise>
							</c:choose>
				</div>
			</c:forEach>
	</c:if>
			
			
	</div>	
</div>

<script type="text/javascript" language="javascript">

	$('#hideGenre').hide();
	$('#showGenre').hide();
	
	<c:if test="${model.genreType ne 'null'}">
		$('#genreList').hide('blind');
		$('#showGenre').show();
	</c:if>

</script>

</body>
</html>