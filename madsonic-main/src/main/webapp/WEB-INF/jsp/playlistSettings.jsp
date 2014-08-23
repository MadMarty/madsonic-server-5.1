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
    <c:param name="cat" value="playlist"/>
    <c:param name="toast" value="${model.toast}"/>
    <c:param name="done" value="${model.done}"/>
    <c:param name="warn" value="${model.warn}"/>
    <c:param name="warnInfo" value="${model.warnInfo}"/>
    <c:param name="bug" value="${model.bug}"/>	
    <c:param name="bugInfo" value="${model.bugInfo}"/>		
</c:import>
<br>
<p class="forward"><a href="playlistEditor.view?">Create Playlist</a></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;">Create Playlists</p>
<br>
<p class="forward"><a href="importPlaylist.view?">Import Playlist</a></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;">Import Playlists into Madsonic Database</p>
<br>
<p class="forward"><a href="playlistSettings.view?exportPlaylists"><fmt:message key="cleanupsettings.exportplaylist.title"/></a></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><fmt:message key="cleanupsettings.exportplaylist"/> <b>${model.exportfolder}</b><br></p>
<br>
<p class="forward"><a href="playlistSettings.view?backupPlaylists">Backup Playlists</a></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><fmt:message key="cleanupsettings.backupplaylist"/> <b>${model.backupfolder}</b><br></p>
<br>
<p class="forward"><a href="playlistSettings.view?recoveryPlaylists">Recover Playlists</a></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;">Restore all Playlists from Backup</p>
<br>
<p class="forward"><a href="playlistSettings.view?deletePlaylists">Delete all Playlists</a></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;">Delete only Playlists in Database</p>
<br><!--
<p class="forward"><a href="playlistSettings.view?resetPlaylists"><fmt:message key="cleanupsettings.resetplaylist.title"/></a></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><fmt:message key="cleanupsettings.resetplaylist"/></p>
<br>-->

<c:if test="${not empty model.error}">
    <p class="warning"><fmt:message key="${model.error}"/></p>
</c:if>

<c:if test="${model.reload}">
    <script language="javascript" type="text/javascript">parent.frames.left.location.href="left.view?"</script>
</c:if>
<!-- CONTENT -->

</div>
</body></html>