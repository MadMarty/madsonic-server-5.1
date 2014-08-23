<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<html><head>

	<%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
	
	<script type="text/javascript" language="javascript">
       function hideGenre() {
            $('#genreList').hide('swing');
		}
		
        function showGenre() {
            $("#genreList").show('blind');
        }
    </script>
</head>
<body class="mainframe bgcolor1">

<div id="content_2">

<h1>

<img src="<spring:theme code="genresImage"/>" alt="">Genre</h1>
<!-- TODO: songs -->
<h2><c:forTokens items="artists albums" delims=" " var="cat" varStatus="loopStatus">
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

<div id="genreList" class="genre" style="width:95%;max-width: 1100px;">

	<c:if test="${model.listType eq 'artists'}">
	<div class="genre" style="width:85%; max-width: 1100px; line-height: 1.5; padding: 10px;">
	<c:forEach items="${model.genreArtistList}" var="genreArtistList">
	<span style="font-size: ${genreArtistList.occurrence+11}px;">
	<sub:url var="url" value="genres.view">
		<sub:param name="listType" value="${model.listType}"/>
		<sub:param name="genre" value="${genreArtistList.name}"/>
	</sub:url>
	<a href="${url}">${genreArtistList.name}</a>
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
	<a href="${url}">${genreAlbumList.name}</a>
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
	<a href="${url}">${genreSongList.name}</a>
	</span>
	</c:forEach>
	</div></c:if>
</div>

	Genre 
	<div id="showGenre" style="display: inline"><a href="javascript:noop()" onclick="showGenre()"> show </a></div> | 
	<div id="hideGenre" style="display: inline"><a href="javascript:noop()" onclick="hideGenre()"> hide </a></div> 
	<br><br>

	<div id="list" class="genre" style="width:95%;max-width: 1100px;">

	<c:if test="${model.listType eq 'songs' and empty model.albums}">
	TODO: not implemented (c;=
	</c:if>
	
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
	</div>	
</div>

<script type="text/javascript" language="javascript">
	<c:if test="${model.genreType ne 'null'}">
		$('#genreList').hide('blind');
	</c:if>
</script>

</body>
</html>