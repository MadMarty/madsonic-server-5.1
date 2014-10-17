<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%--@elvariable id="command" type="org.madsonic.command.IconSettingsCommand"--%>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
	
	<link href="<c:url value="/style/customScrollbar.css"/>" rel="stylesheet">
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
</head>

<body class="mainframe bgcolor1">

<div id="content_2" class="content_main">
<!-- CONTENT -->

<script type="text/javascript" src="<c:url value="/script/wz_tooltip.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/tip_balloon.js"/>"></script>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="icon"/>
    <c:param name="toast" value="${command.toast}"/>
</c:import>
<br>
<form:form method="post" action="iconSettings.view" commandName="command">

  <table style="white-space:nowrap" width="30%" class="indent">
  <tr>
	<td><form:checkbox path="showIconHome" id="showIconHome"/><label for="showIconHome">Home</label></td>
	<td><form:checkbox path="showIconSettings" id="showIconSettings" disabled="true"/><label for="showIconSettings">Settings</label></td>
</tr>
<tr>
	<td><form:checkbox path="showIconArtist" id="showIconArtist"/><label for="showIconArtist">Artist</label></td>
	<td><form:checkbox path="showIconStatus" id="showIconStatus" /><label for="showIconStatus">Status</label></td>
</tr>
<tr>

	<td><form:checkbox path="showIconPlaying" id="showIconPlaying"/><label for="showIconPlaying">Playing</label></td>
	<td><form:checkbox path="showIconSocial" id="showIconSocial"/><label for="showIconSocial">Social</label></td>
</tr>
<tr>

	<td><form:checkbox path="showIconCover" id="showIconCover"/><label for="showIconCover">Discover</label></td>
	<td><form:checkbox path="showIconHistory" id="showIconHistory"/><label for="showIconHistory">History</label></td>
</tr>
<tr>

	<td><form:checkbox path="showIconStarred" id="showIconStarred"/><label for="showIconStarred">Starred</label></td>
	<td><form:checkbox path="showIconStatistics" id="showIconStatistics"/><label for="showIconStatistics">Statistics</label></td>
</tr>
<tr>

	<td><form:checkbox path="showIconGenre" id="showIconGenre" disabled="false"/><label for="showIconGenre">Genre</label></td>
	<td><form:checkbox path="showIconPlaylists" id="showIconPlaylists"/><label for="showIconPlaylists">Playlists</label></td>
</tr>
<tr>
	<td><form:checkbox path="showIconMoods" id="showIconMoods"/><label for="showIconMoods">Moods</label></td>
	<td><form:checkbox path="showIconPlaylistEditor" id="showIconPlaylistEditor"/><label for="showIconPlaylistEditor">Playlist Editor</label></td>
</tr>
<tr>
	<td><form:checkbox path="showIconRadio" id="showIconRadio"/><label for="showIconRadio">Radio</label></td>
	<td><form:checkbox path="showIconMore" id="showIconMore"/><label for="showIconMore">More</label></td>
</tr>
<tr>
	<td><form:checkbox path="showIconPodcast" id="showIconPodcast"/><label for="showIconPodcast">Podcast</label></td>
	<td><form:checkbox path="showIconAbout" id="showIconAbout"/><label for="showIconAbout">About</label></td>
</tr>
<tr>
	<td><form:checkbox path="showIconRandom" id="showIconRandom"/><label for="showIconRandom">Random</label></td>
</tr>
<tr>
  </tr>
  <tr>
  </tr>
  <tr>
    <td><!--18--></td>
  </tr>
  <tr>
    <td><!--19--></td>
  </tr>
  <tr>
    <td><!--20--></td>
  </tr>
</table>

	<table>
        <tr><td colspan="2">&nbsp;</td></tr>	
		
		<tr>
            <td> </td>
            <td><form:checkbox path="showIconAdmins" id="showIconAdmins"/><label for="showIconAdmins">use Settings also for Admin Accounts</label></td>
        </tr>		
        <tr><td colspan="2">&nbsp;</td></tr>	
		
        <tr>
            <td colspan="2" style="padding-top:1.5em">
                <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
                <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">
            </td>
        </tr>

    </table>
</form:form>

<c:if test="${command.reloadNeeded}">
    <script language="javascript" type="text/javascript">
        parent.frames.upper.location.href="top.view?";
    </script>
</c:if>

<!-- CONTENT -->
</div>
</body></html>