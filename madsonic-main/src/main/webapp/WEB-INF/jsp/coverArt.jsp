<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%@ include file="include.jsp" %>

<%--
PARAMETERS
  albumId: ID of album.
  coverArtSize: Height and width of cover art.
  typArtist: Default for Artist Cover
  typVideo: Default Video cover
  albumName: Album name to display as caption and img alt.
  artistName: Artistname
  showLink: Whether to make the cover art image link to the album page.
  showZoom: Whether to display a link for zooming the cover art.
  showArtist: Whether to display a link for changing the cover art.
  showChange: Whether to display a link for changing the cover art.
  showCaption: Whether to display the album name as a caption below the image.
  captionLength: Truncate caption after this many characters.
  appearAfter: Fade in after this many milliseconds, or nil if no fading in should happen.
  showPlayAlbum: Display PlayAlbum Button
  showAddAlbum: Display AddAlbum Button
  showTopTrack: Display TopTrack Button
  extraPadding: 8px bottom padding
--%>

<c:choose>
    <c:when test="${empty param.coverArtSize}">
        <c:set var="size" value="auto"/>
    </c:when>
    <c:otherwise>
        <c:set var="size" value="${param.coverArtSize + 8}px"/>
    </c:otherwise>
</c:choose>

<c:set var="height" value="${param.coverArtSize + 8}px"/>

<c:choose>
    <c:when test="${param.typVideo}">
        <c:set var="height" value="${param.coverArtSize + 80}px"/>
    </c:when>
</c:choose>	

<c:choose>
    <c:when test="${empty param.extraPadding}">
        <c:set var="size" value="${param.coverArtSize + 8}px"/>
    </c:when>
</c:choose>
	
<c:set var="opacity" value="${empty param.appearAfter ? 1 : 0}"/>

	<c:choose>
		<c:when test="${not empty param.extraPadding}">
			<c:set var="size" value="118px"/>
 		</c:when>
	</c:choose>
	
	<div style="width:${size};max-width:${size};max-height:${height};"  title="${param.albumName}">
 
    <sub:url value="main.view" var="mainUrl">
        <sub:param name="id" value="${param.albumId}"/>
    </sub:url>

    <sub:url value="/coverArt.view" var="coverArtUrl">
        <c:if test="${not empty param.coverArtSize}">
            <sub:param name="size" value="${param.coverArtSize}"/>
        </c:if>
        <sub:param name="id" value="${param.albumId}"/>
        <sub:param name="typArtist" value="${param.typArtist}"/>
        <c:if test="${param.typVideo eq 'true'}">
            <sub:param name="typVideo" value="true"/>
        </c:if>		
    </sub:url>
	
    <sub:url value="/coverArt.view" var="zoomCoverArtUrl">
        <sub:param name="id" value="${param.albumId}"/>
    </sub:url>

    <str:randomString count="5" type="alphabet" var="divId"/>
    <div class="outerpair1" id="${divId}" style="display:none">
        <div class="outerpair2">
            <div class="shadowbox">
                <div class="innerbox">
                    <c:choose>
                        <c:when test="${param.showLink}"><a href="${mainUrl}" title="${param.albumName}"></c:when>
                        <c:when test="${param.showZoom}"><a href="${zoomCoverArtUrl}" rel="zoom" title="${param.albumName}"></c:when>
                    </c:choose>
                        <img src="${coverArtUrl}" alt="${param.albumName}">
                        <c:if test="${param.showLink or param.showZoom}"></a></c:if>
                </div>
            </div>
        </div>
    </div>
	
	<c:if test="${param.showPlayAlbum}">
    <str:randomString count="5" type="alphabet" var="divIdAddAll"/>
    <str:randomString count="5" type="alphabet" var="divIdCoverBack"/>
	
		<c:choose>
		<c:when test="${not empty param.extraPadding}">
			<div class="divIdCoverBack" id="${divIdCoverBack}" style="opacity: 0.70; clear: both; position: relative; padding: 8px 0 0 0; max-width: 119px; z-index:10; display:none;">
		</c:when>
		<c:otherwise>
			<div class="divIdCoverBack" id="${divIdCoverBack}" style="opacity: 0.70; clear: both; position: relative; padding: 0 0 0 0; max-width: 119px; z-index:10; display:none;">
		</c:otherwise>
	</c:choose>
	
	<img src="icons/default/cover_back.png">
	</div>	
	
	<div id="${divIdAddAll}" style="opacity: 0.85; clear: both; position: relative; top: -90px; left: 85px; padding: 0 0 0 0; max-width: 16px; z-index:10; display:none;">
	<a href="javascript:void(0)" onclick="parent.playQueue.onAdd(${param.albumId});" title="Add" alt="Add"><img src="<spring:theme code="addAlbumImage"/>"></a>
	</div>	
    </c:if>
	
	<c:if test="${param.showAddAlbum}">
    <str:randomString count="5" type="alphabet" var="divIdPlayAll"/>
	<div id="${divIdPlayAll}" style="opacity: 0.85; clear: both; position: relative; top: -112px; left: 60px; padding: 0 0 0 0; max-width: 16px; z-index:10; display:none;">
	<a href="javascript:void(0)" onclick="parent.playQueue.onPlay(${param.albumId});" title="Play" alt="Play"><img src="<spring:theme code="playAlbumImage"/>"></a>
	</div>	
    </c:if>

	<c:if test="${param.showTopTrack}">
    <str:randomString count="5" type="alphabet" var="divIdTopTrack"/>
	<div id="${divIdTopTrack}" style="opacity: 0.85; clear: both; position: relative; top: -134px; left: 35px; padding: 0 0 0 0; max-width: 16px; z-index:10; display:none;">
	<a href="javascript:void(0)" onclick="parent.playQueue.onPlayTopTrack(&quot;${param.artistName}&quot;);" title="Play TopTrack" alt="Play Artist TopTrack"><img src="<spring:theme code="topTrackImage"/>"></a>
	</div>	
    </c:if>
	
    <c:if test="${not empty param.appearAfter}">
        <script type="text/javascript">
            $(document).ready(function () {
                setTimeout("$('#${divId}').fadeIn(600)", ${param.appearAfter});
                setTimeout("$('#${divIdCoverBack}').fadeIn(2500)", ${param.appearAfter});
                setTimeout("$('#${divIdPlayAll}').fadeIn(2000)", ${param.appearAfter});
                setTimeout("$('#${divIdAddAll}').fadeIn(2000)", ${param.appearAfter});
                setTimeout("$('#${divIdTopTrack}').fadeIn(2000)", ${param.appearAfter});
            });
        </script>
    </c:if>
</div>

<div style="text-align:left;padding-left:2px; padding-top:2px;padding-bottom:6px;">

	<c:if test="${param.showArtist}">
        <sub:url value="/artist.view" var="ArtistUrl">
            <sub:param name="name" value="${param.artistName}"/>
            <sub:param name="showAlbum" value="true"/>
        </sub:url>
        <a class="detail" href="${ArtistUrl}">Artist</a>
    </c:if>

    <c:if test="${param.showArtist and param.showChange}">
        |
    </c:if>

    <c:if test="${param.showChange}">
        <sub:url value="/changeCoverArt.view" var="changeCoverArtUrl">
            <sub:param name="id" value="${param.albumId}"/>
        </sub:url>
        <a class="detail" href="${changeCoverArtUrl}"><fmt:message key="coverart.change"/></a>
    </c:if>

    <c:if test="${param.showZoom and param.showArtist}">
        |
    </c:if>

    <c:if test="${param.showZoom}">
        <a class="detail" rel="zoom" title="${param.albumName}" href="${zoomCoverArtUrl}"><fmt:message key="coverart.zoom"/></a>
    </c:if>

	<c:choose>
		<c:when test="${fn:startsWith(param.albumName,'[')}">
			<c:if test="${not param.showZoom and not param.showChange and param.showCaption}">
				<span class="detailmini"><str:truncateNicely upper="${param.captionLength}">${fn:split(param.albumName,']')[1]}</str:truncateNicely></span>
			</c:if>
		</c:when>
		<c:otherwise>
			<c:if test="${not param.showZoom and not param.showChange and param.showCaption}">
				<c:if test="${param.captionLength > 0}">
					<span class="detailmini"><str:truncateNicely upper="${param.captionLength}">${param.albumName}</str:truncateNicely></span>
				</c:if>
			</c:if>
		</c:otherwise>
	</c:choose>
</div>