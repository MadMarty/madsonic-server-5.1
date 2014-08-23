<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
	
	<link href="<c:url value="/style/customScrollbar.css"/>" rel="stylesheet">	
</head>
<body class="mainframe bgcolor1">

<div id="content_2" class="content_main">
<!-- CONTENT -->

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="lastfm"/>
    <c:param name="toast" value="${model.toast}"/>
    <c:param name="done" value="${model.done}"/>
    <c:param name="warn" value="${model.warn}"/>
    <c:param name="warnInfo" value="${model.warnInfo}"/>
    <c:param name="bug" value="${model.bug}"/>	
    <c:param name="bugInfo" value="${model.bugInfo}"/>	
</c:import>
<br>
<p class="forward"><a href="lastfmSettings.view?ScanNow"><fmt:message key="lastfmSettings.artistcover.title"/></a></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><fmt:message key="lastfmSettings.artistcover"/></p>
<br>
<p class="forward"><a href="lastfmSettings.view?ScanInfo"><fmt:message key="lastfmSettings.artistsummary.title"/> (Full)</a></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><fmt:message key="lastfmSettings.artistsummaryinfo1"/></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><fmt:message key="lastfmSettings.artistsummaryinfo2"/></p>
<br>
<p class="forward"><a href="lastfmSettings.view?ScanNewInfo"><fmt:message key="lastfmSettings.artistsummary.title"/> (only new)</a></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><fmt:message key="lastfmSettings.artistsummaryinfo1"/></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><fmt:message key="lastfmSettings.artistsummaryinfo2"/></p>
<br>
<p class="forward"><a href="lastfmSettings.view?CleanupArtist">LastFM Artist Cleanup</a></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;">Cleanup unknown/incomplete artist entries</p>
<br>
<p class="forward"><a href="lastfmSettings.view?CleanupArtistTopTracks">LastFM Artist TopTracks Cleanup</a></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;">Cleanup cached artist TopTracks entries</p>
<br>

<table>
<tr>
<td>Select LastFM TopTrack result size</td>
<td>
<form method="post" action="lastfmSettings.view">
<select name="LastFMResultSize">
<c:if test="${model.LastFMResultSize eq '10'}"><option selected="selected">10</option></c:if>
<c:if test="${model.LastFMResultSize ne '10'}"><option>10</option></c:if>
<c:if test="${model.LastFMResultSize eq '20'}"><option selected="selected">20</option></c:if>
<c:if test="${model.LastFMResultSize ne '20'}"><option>20</option></c:if>
<c:if test="${model.LastFMResultSize eq '30'}"><option selected="selected">30</option></c:if>
<c:if test="${model.LastFMResultSize ne '30'}"><option>30</option></c:if>
<c:if test="${model.LastFMResultSize eq '50'}"><option selected="selected">50</option></c:if>
<c:if test="${model.LastFMResultSize ne '50'}"><option>50</option></c:if>
</select>
</td>
</tr>
<tr>
<td>
<input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
</td>
</tr>
</form>
</table>

<c:if test="${not empty model.error}">
    <p class="warning"><fmt:message key="${model.error}"/></p>
</c:if>

<c:if test="${model.reload}">
    <script language="javascript" type="text/javascript">parent.frames.upper.location.href="top.view?"</script>
    <script language="javascript" type="text/javascript">parent.frames.left.location.href="left.view?"</script>
</c:if>

<!-- CONTENT -->
</div>

</body></html>