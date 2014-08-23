<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%@ include file="include.jsp" %>

<%--
PARAMETERS
  albumId: ID of album.
  coverArtSize: Height and width of cover art.
  coverArtPath: Path to cover art, or nil if generic cover art image should be displayed.
  albumPath: Path to album.
  albumName: Album name to display as caption and img alt.
  showLink: Whether to make the cover art image link to the album page.
  showCaption: Whether to display the album name as a caption below the image.
  count: loop
  single: only one album
--%>
<c:choose>
    <c:when test="${empty param.coverArtSize}">
        <c:set var="size" value="auto"/>
    </c:when>
    <c:otherwise>
        <c:set var="size" value="${param.coverArtSize}px"/>
    </c:otherwise>
</c:choose>

    <sub:url value="main.view" var="mainUrl">
        <sub:param name="id" value="${param.albumId}"/>
    </sub:url>

    <sub:url value="/coverArt.view" var="coverArtUrl">
        <c:if test="${not empty param.coverArtSize}">
            <sub:param name="size" value="${param.coverArtSize}"/>
        </c:if>
        <sub:param name="id" value="${param.albumId}"/>
    </sub:url>

	<c:choose>
		<c:when test="${param.single == true}">
		<img id="photo${param.count}" src="${coverArtUrl}" alt="picture" title="${param.albumName}"></a>
		</c:when>
		<c:otherwise>
			<img id="photo${param.count}" src="${coverArtUrl}" class="stackphotos" title="${param.albumName}" alt="${mainUrl}" onclick="albumlink(this)">
		</c:otherwise>
	</c:choose>	



