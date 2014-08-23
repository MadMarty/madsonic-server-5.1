<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>

<%--
PARAMETERS
  id: ID of file.
  video: Whether the file is a video (default false).
  playEnabled: Whether the current user is allowed to play songs (default true).
  playMoreEnabled: 
  addEnabled: Whether the current user is allowed to add songs to the playlist (default true).
  addNextEnabled: Whether the current user is allowed to add songs to the playlist (default flase).
  addLastEnabled: Whether the current user is allowed to add songs to the playlist (default false).
  downloadEnabled: Whether the current user is allowed to download songs (default false).
  starEnabled: Whether to show star/unstar controls (default false).
  starred: Whether the file is currently starred.
  asTable: Whether to put the images in td tags.
  YoutubeEnabled: Whether to show Youtube Control.
--%>

<sub:url value="/download.view" var="downloadUrl">
    <sub:param name="id" value="${param.id}"/>
</sub:url>
<c:if test="${param.starEnabled}">
    <c:if test="${param.asTable}"><td></c:if>
    <a href="#" onclick="toggleStar(${param.id}, '#starImage${param.id}'); return false;">
        <c:choose>
            <c:when test="${param.starred}">
                <img id="starImage${param.id}" src="<spring:theme code="ratingOnImage"/>" alt="">
            </c:when>
            <c:otherwise>
                <img id="starImage${param.id}" src="<spring:theme code="ratingOffImage"/>" alt="">
            </c:otherwise>
        </c:choose>
    </a>
    <c:if test="${param.asTable}"></td></c:if>
</c:if>

<c:if test="${param.asTable}"><td></c:if>
<c:if test="${empty param.playEnabled or param.playEnabled}">
    <c:choose>
        <c:when test="${param.video}">
            <sub:url value="/videoPlayer.view" var="videoUrl">
                <sub:param name="id" value="${param.id}"/>
            </sub:url>
            <%-- Open video in new window if Chromecast is already initialized in play queue. --%>
            <a href="#" onclick="window.open('${videoUrl}', parent.playQueue.CastPlayer.receiverFound ? '_blank' : 'main'); return false;">
                <img src="<spring:theme code="playImage"/>" alt="<fmt:message key="common.play"/>"
                     style="padding-right: 0.1em" title="<fmt:message key="common.play"/>">
            </a>
        </c:when>
        <c:otherwise>
            <a href="#" onclick="parent.playQueue.onPlay(${param.id}); return false;">
			<img src="<spring:theme code="playImage"/>" alt="<fmt:message key="common.play"/>" title="<fmt:message key="common.play"/>"></a>
		<c:if test="${param.asTable}"></td><td></c:if>
		
		<c:if test="${empty param.playAddEnabled or param.playAddEnabled}">
			<a href="javascript:void(0)" onclick="parent.playQueue.onPlayAdd('${param.id}');">
			<img src="<spring:theme code="playAddImage"/>" alt="<fmt:message key="common.playadd"/>" title="<fmt:message key="common.playadd"/>"></a>
		</c:if>
		
		<c:if test="${param.playMoreEnabled}">
			<a href="javascript:void(0)" onclick="parent.playQueue.onPlayMore('${param.id}');">
			<img src="<spring:theme code="playAddImage"/>" alt="<fmt:message key="common.playadd"/>" title="<fmt:message key="common.playadd"/>"></a>
		</c:if>

        </c:otherwise>
    </c:choose>
</c:if>

<c:if test="${param.video}">
<c:if test="${param.addEnabled and param.asTable}"><td></c:if><c:if test="${param.asTable}"></td></c:if>
<c:if test="${param.addNextEnabled and param.asTable}"><td></c:if><c:if test="${param.asTable}"></td></c:if>
<c:if test="${param.addLastEnabled and param.asTable}"><td></c:if><c:if test="${param.asTable}"></td></c:if>
</c:if>

<c:if test="${(empty param.addEnabled or param.addEnabled) and not param.video}">
<c:if test="${param.asTable}"><td></c:if>
    <a href="#"><img id="add${param.id}" src="<spring:theme code="addImage"/>" alt="<fmt:message key="common.add"/>" title="<fmt:message key="common.add"/>"></a>
<c:if test="${param.asTable}"></td></c:if>
</c:if>

<c:if test="${(param.addNextEnabled) and not param.video}">
<c:if test="${param.asTable}"><td></c:if>
        <a href="javascript:void(0)" onclick="parent.playQueue.onAddNext(${param.id});">
		<img src="<spring:theme code="addImage"/>" alt="<fmt:message key="common.addnext"/>" title="<fmt:message key="common.addnext"/>"></a>
<c:if test="${param.asTable}"></td></c:if>
</c:if>

<c:if test="${(param.addLastEnabled) and not param.video}">
<c:if test="${param.asTable}"><td></c:if>
    <a href="javascript:void(0)" onclick="parent.playQueue.onAdd(${param.id});">
    <img src="<spring:theme code="addImage"/>" alt="<fmt:message key="common.addlast"/>" title="<fmt:message key="common.addlast"/>"></a>
<c:if test="${param.asTable}"></td></c:if>
</c:if>

<c:if test="${param.asTable}"><td></c:if>
<c:if test="${param.downloadEnabled}">
    <a href="${downloadUrl}">
        <img src="<spring:theme code="downloadImage"/>" alt="<fmt:message key="common.download"/>"
             title="<fmt:message key="common.download"/>"></a>
</c:if>

<c:if test="${param.asTable}"></td></c:if>
	<c:if test="${model.dir.album}">
		<c:if test="${param.asTable}"><td></c:if>
			<c:if test="${param.YoutubeEnabled}">
			<sub:url value="http://www.youtube.com/results" var="YoutubeUrl" encoding="UTF-8">
				<sub:param name="search_query" value="${param.artist} ${param.title}"/>
			</sub:url>
			<a target="_blank" href="${YoutubeUrl}">
			<img src="<spring:theme code="youtubeImage"/>" alt="YouTube" title="YouTube"></a>
			</c:if>
		<c:if test="${param.asTable}"></td></c:if>
	</c:if>
<c:if test="${param.asTable}"></td></c:if>

<script type="text/javascript">
    $(function () {
        $.contextMenu({
            selector:'#add${param.id}',
            trigger:'left',
            callback:function (key, options) {
                if (key == "addnext") {
                    parent.playQueue.onAddNext(${param.id});
                } else {
                    parent.playQueue.onAdd(${param.id});
                }
            },
            items:{
                "addnext":{name:"<fmt:message key="main.addnext"/>"},
                "addlast":{name:"<fmt:message key="main.addlast"/>"}
            }
        });
    });
</script>
