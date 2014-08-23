<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%@ include file="include.jsp" %>

<%--
PARAMETERS
  id: ID of file.
  video: Whether the file is a video (default false).
  playEnabled: Whether to show play button (default true).
  addEnabled: Whether to show add next/last buttons (default true).
  downloadEnabled: Whether to show download button (default false).
  starEnabled: Whether to show star/unstar controls (default false).
  starred: Whether the file is currently starred.
  asTable: Whether to put the images in td tags.
--%>

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
            <a href="#" onclick="window.open('${videoUrl}', top.playQueue.CastPlayer.receiverFound ? '_blank' : 'main'); return false;">
                <img src="<spring:theme code="playImage"/>" alt="<fmt:message key="common.play"/>"
                     style="padding-right: 0.1em" title="<fmt:message key="common.play"/>">
            </a>
        </c:when>
        <c:otherwise>
            <a href="#" onclick="top.playQueue.onPlay(${param.id}); return false;">
                <img src="<spring:theme code="playImage"/>" alt="<fmt:message key="common.play"/>"
                     title="<fmt:message key="common.play"/>"></a>
        </c:otherwise>
    </c:choose>
</c:if>
<c:if test="${param.asTable}"></td></c:if>

<c:if test="${param.asTable}"><td></c:if>
<c:if test="${(empty param.addEnabled or param.addEnabled) and not param.video}">
    <a href="#" onclick="top.playQueue.onAdd(${param.id}); return false;">
        <img id="add${param.id}" src="<spring:theme code="addImage"/>" alt="<fmt:message key="main.addlast"/>"
             title="<fmt:message key="main.addlast"/>"></a>
</c:if>
<c:if test="${param.asTable}"></td></c:if>

<c:if test="${param.asTable}"><td></c:if>
<c:if test="${(empty param.addEnabled or param.addEnabled) and not param.video}">
    <a href="#" onclick="top.playQueue.onAddNext(${param.id}); return false;">
        <img id="add${param.id}" src="<spring:theme code="addNextImage"/>" alt="<fmt:message key="main.addnext"/>"
             title="<fmt:message key="main.addnext"/>"></a>
</c:if>
<c:if test="${param.asTable}"></td></c:if>

<c:if test="${param.asTable}"><td></c:if>
<c:if test="${param.downloadEnabled}">
    <sub:url value="/download.view" var="downloadUrl">
        <sub:param name="id" value="${param.id}"/>
    </sub:url>
    <a href="${downloadUrl}">
        <img src="<spring:theme code="downloadImage"/>" alt="<fmt:message key="common.download"/>" title="<fmt:message key="common.download"/>"></a>
</c:if>
<c:if test="${param.asTable}"></td></c:if>
