<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>

    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/interface/playlistService.js"/>"></script>    
    
    <script type="text/javascript" language="javascript">

        var playlists;
        
        function init() {
            dwr.engine.setErrorHandler(null);
            updatePlaylists();
        }

        function updatePlaylists() {
            playlistService.getReadablePlaylists(playlistCallback);
        }

         function createEmptyPlaylist() {
            playlistService.createEmptyPlaylist(playlistCallback);
        }
		
        function createNamedPlaylist(loadPlaylistUrl) {

			var isShared = false;
			if ($("#isShared").is(":checked")) {
				isShared = true;
			}
			var playlistName = $("#playlistName").val();
			var playlistComment = $("#playlistComment").val();

            playlistService.createNamedPlaylist(playlistName, playlistComment, isShared, playlistCallback);
		}

        function createEmptyPlaylistURL(loadPlaylistUrl) {
			var isShared = false;
		    if ($("#isShared").is(":checked")) {
                isShared = true;
            }
			var playlistComment = $("#playlistComment").val();
            playlistService.createEmptyPlaylist(playlistComment, isShared, playlistCallback);
			top.left.updatePlaylists();
			location.href = loadPlaylistUrl;
		}
		
		function CreateAndRefresh() {
			createEmptyPlaylist();
		}

//			setTimeout("alert ('createEmptyPlaylist');",4000);
//			location.href = "playlistHome.view?reload=true";

        function playlistCallback(playlists) {
            this.playlists = playlists;
			
            $("#playlists").empty();
			$("<table class='ruleTable indent'>").appendTo("#playlists");  

            for (var i = 0; i < playlists.length; i++) {

                var playlist = playlists[i];
				var playlisticon;
				var playlistcomment;
				
						if (playlist.shareLevel == '0') {
							playlisticon = "<img src='icons/default/playlist-private.png' title='Private Playlist' width='28' height='28' />"
						}
						if (playlist.shareLevel == '1') {
							playlisticon = "<img src='icons/default/playlist-group.png' title='Public Playlist' width='28' height='28' />"
						}
						if (playlist.shareLevel == '2') {
							playlisticon = "<img src='icons/default/playlist-add.png' title='Add Allowed Playlist' width='28' height='28' />"
						}
						if (playlist.shareLevel == '3') {
							playlisticon = "<img src='icons/default/playlist-utilities.png' title='Remove Allowed Playlist' width='28' height='28' />"
						}
						if (playlist.shareLevel == '4') {
							playlisticon = "<img src='icons/default/playlist-unlocked.png' title='Full Access Playlist' width='28' height='28' />"
						}
				
						if (playlist.comment == null) {
							playlistcomment = "";
						} else {
							playlistcomment = playlist.comment;
						}
				
                $("<tr><td class='ruleTableCellPL' align='center'>" + playlisticon + "</td><td class='ruleTableCellPL'><a target='main' href='playlist.view?id=" + playlist.id + "'>" + playlist.name + "</a><br></td><td class='ruleTableCellPL'>" + playlistcomment + "</td><td class='ruleTableCellPL'> [" + playlist.username + "] </td><td class='ruleTableCellPL' align='center'> [" + playlist.fileCount + "] </td><tr>").appendTo("#playlists");  
				
				// "<table><tr><td>" + playlist.name + 
				// "</td><td>" + playlist.comment + 
				// "</td><td>" + playlist.username + 
				// "</td><td>" + playlist.fileCount + 
				// "</td></tr></table>").appendTo("#playlists");
            }
			$("</table>").appendTo("#playlists");  
       }

    </script>
</head>

<body class="mainframe bgcolor1" onload="init()">
<h1>Edit playlist</h1>
    <table>
    <tr>
        <th align="left" style="padding-top:1em">Name</th>
        <th align="left" style="padding-top:1em">Comment</th>
        <th align="left" style="padding-top:1em">Public</th>
    </tr>
    <tr class="dense">
        <td><input type="text" id="playlistName" name="playlistName" value="Your Playlist" size="40"/></td>
        <td><input type="text" id="playlistComment" name="playlistComment" value="" size="40"/></td>
        <td align="center" style="padding-left:1em"><input id="isShared" name="isShared" type="checkbox" class="checkbox" checked/></td>
        <td/>
    </tr>
    </table>
    <p>
	<div class="forward"><a href="playlistEditor.view?"><fmt:message key="common.refresh"/></a></div>
	</p>
	
	<div id="playlistWrapper" style='padding-left:0.5em'>

		<sub:url value="playlistHome.view" var="loadPlaylistUrl"></sub:url>
		<div class="forward"><a href="javascript:noop()" onclick="createNamedPlaylist('${loadPlaylistUrl}')"><fmt:message key="playlist.load.createNamedplaylist"/></a></div>
		
		<sub:url value="playlistHome.view" var="loadPlaylistUrl"></sub:url>
		<div class="forward"><a href="javascript:noop()" onclick="createEmptyPlaylist('${loadPlaylistUrl}')"><fmt:message key="playlist.load.createEmptyplaylist"/></a></div>
		
		<div class="forward"><a href="importPlaylist.view" target="main"><fmt:message key="left.importplaylist"/></a></div>
		</div>	
	
		<h2 class="bgcolor2"><fmt:message key="left.playlists"/></h2> 
		
		<div id="playlists" style="display:block"></div>
		
		</div>
</body></html>