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
  scale: Image width: 100%
--%>

<c:choose>
    <c:when test="${empty param.coverArtSize}">
        <c:set var="size" value="auto"/>
    </c:when>
    <c:otherwise>
        <c:set var="size" value="${param.coverArtSize * param.scale}px"/>
    </c:otherwise>
</c:choose>

<c:set var="opacity" value="${empty param.appearAfter ? 1 : 0}"/>
	
<div style="width:${size}; max-width:${size}; padding: 2px" title="${param.albumName}">
 
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
    <div style="position: relative; width: 0; height: 0"></div>
    <div class="outerpair1" id="${divId}" style="display:none;">
        <div class="outerpair2">
            <div class="shadowbox">
                <div class="innerbox">
                    <c:choose>
                        <c:when test="${param.showLink}"><a href="${mainUrl}" title="${param.albumName}"></c:when>
                        <c:when test="${param.showZoom}"><a href="${zoomCoverArtUrl}" rel="zoom" title="${param.albumName}"></c:when>
                    </c:choose>
                    <img width="100%" src="${coverArtUrl}" alt="${param.albumName}">
                        <c:if test="${param.showLink or param.showZoom}"></a></c:if>
                </div>
            </div>
        </div>
    </div>
    <c:if test="${not empty param.appearAfter}">
        <script type="text/javascript">
            $(document).ready(function () {
                setTimeout("$('#${divId}').fadeIn(100)", ${param.appearAfter});
            });
        </script>
    </c:if>
</div>
