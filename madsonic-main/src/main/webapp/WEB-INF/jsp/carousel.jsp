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
--%>
<c:choose>
    <c:when test="${empty param.coverArtSize}">
        <c:set var="size" value="auto"/>
    </c:when>
    <c:otherwise>
        <c:set var="size" value="${param.coverArtSize}px"/>
    </c:otherwise>
</c:choose>

<div style="width:${size}; max-width:${size}; height:${size}; max-height:${size}" title="${param.albumName}">
    <sub:url value="main.view" var="mainUrl">
        <sub:param name="id" value="${param.albumId}"/>
    </sub:url>

    <sub:url value="/coverArt.view" var="coverArtUrl">
        <c:if test="${not empty param.coverArtSize}">
            <sub:param name="size" value="${param.coverArtSize}"/>
        </c:if>
        <sub:param name="id" value="${param.albumId}"/>
    </sub:url>
    <sub:url value="/coverArt.view" var="zoomCoverArtUrl">
        <sub:param name="id" value="${param.albumId}"/>
    </sub:url>

	<c:if test="${param.showLink}"><a href="${mainUrl}" title="${param.albumName}"></c:if>
	<c:choose>
		<c:when test="${param.albumYear != null && !param.albumYear['empty']}">
			<img class = "cloudcarousel" src="${coverArtUrl}" alt="${mainUrl}" title="${param.albumArtist} - ${param.albumName} [${param.albumYear}]">
		</c:when>
		<c:otherwise>
			<img class = "cloudcarousel" src="${coverArtUrl}" alt="${mainUrl}" title="${param.albumArtist} - ${param.albumName}">
		</c:otherwise>
	</c:choose>
	<c:if test="${param.showLink}"></a></c:if>
</div>

<div style="text-align:right; padding-right: 8px;">

	<c:choose>
		<c:when test="${fn:startsWith(param.albumName,'[')}">
			<c:if test="${param.showCaption}">
				<span class="detailmini"><str:truncateNicely upper="20">${fn:split(param.albumName,']')[1]}</str:truncateNicely></span>
			</c:if>
		</c:when>
		<c:otherwise>
			<c:if test="${param.showCaption}">
				<span class="detailmini"><str:truncateNicely upper="20">${param.albumName}</str:truncateNicely></span>
			</c:if>
		</c:otherwise>
	</c:choose>
</div>