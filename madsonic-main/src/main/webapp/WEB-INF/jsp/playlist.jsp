<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
    <script type="text/javascript" src="<c:url value='/script/scripts.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/dwr/util.js'/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/interface/playlistService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/interface/starService.js"/>"></script>
    <script type="text/javascript" language="javascript">

        var playlist;
        var songs;

        function init() {
            dwr.engine.setErrorHandler(null);
			
            $("#dialog-edit").dialog({resizable: true, width:450, position:  [100, 120], modal: false, autoOpen: false,
                buttons: {
                    "<fmt:message key="common.save"/>": function() {
                        $(this).dialog("close");
                        var name = $("#newName").val();
                        var comment = $("#newComment").val();
                        var isPublic = $("#newPublic").is(":checked");
                        $("#name").html(name);
                        $("#comment").html(comment);
                        playlistService.updatePlaylist(playlist.id, name, comment, isPublic, function (playlistInfo){playlistCallback(playlistInfo); top.left.updatePlaylists()});
                    },
                    "<fmt:message key="common.cancel"/>": function() {
                        $(this).dialog("close");
                    }
                }});

			$("#dialog-select-playlist").dialog({resizable: true, width:450, height: 400, position: [100, 95], modal: true, autoOpen: false,
				buttons: {
					"<fmt:message key="common.cancel"/>": function() {
						$(this).dialog("close");
					}
				}});				
			
            $("#dialog-delete").dialog({resizable: false, width:450, height: 170, position: [100, 120], modal: true, autoOpen: false,
                buttons: {
                    "<fmt:message key="common.delete"/>": function() {
                        $(this).dialog("close");
                        playlistService.deletePlaylist(playlist.id, function (){top.left.updatePlaylists(); location = "home.view";});
                    },
                    "<fmt:message key="common.cancel"/>": function() {
                        $(this).dialog("close");
                    }
                }});
				
            getPlaylist();
        }

        function getPlaylist() {
            playlistService.getPlaylist(${model.playlist.id}, playlistCallback);
        }

        function playlistCallback(playlistInfo) {
            this.playlist = playlistInfo.playlist;
            this.songs = playlistInfo.entries;

            if (songs.length == 0) {
                $("#empty").show();
            } else {
                $("#empty").hide();
            }


            $("#songCount").html(playlist.fileCount);
            $("#duration").html(playlist.durationAsString);

            if (playlist.public) {
                $("#shared").html("<fmt:message key="playlist2.shared"/>");
            } else {
                $("#shared").html("<fmt:message key="playlist2.notshared"/>");
            }

            // Delete all the rows except for the "pattern" row
            dwr.util.removeAllRows("playlistBody", { filter:function(tr) {
                return (tr.id != "pattern");
            }});

            // Create a new set cloned from the pattern row
            for (var i = 0; i < songs.length; i++) {
                var song  = songs[i];
                var id = i + 1;
                dwr.util.cloneNode("pattern", { idSuffix:id });
                if (song.starred) {
                    $("#starSong" + id).attr("src", "<spring:theme code='ratingOnImage'/>");
                } else {
                    $("#starSong" + id).attr("src", "<spring:theme code='ratingOffImage'/>");
                }
				if ($("#rank" + id)) {
					if (song.rank >= 10) {
						$("#rank" + id).html(truncate(song.rank));
						$("#rank" + id).attr("rank", song.rank);
						$("#rank" + id).attr("class", "rank");
					} else if (song.rank == 0) {
						$("#rank" + id).html(truncate(""));
						$("#rank" + id).attr("rank", "");
						$("#rank" + id).attr("class", "");
					} else {
						$("#rank" + id).html(truncate("0" + song.rank));
						$("#rank" + id).attr("rank", "0" + song.rank);
						$("#rank" + id).attr("class", "rank");
					}
                }
				if ($("#songId" + id)) {
                    $("#songId" + id).html(truncate(song.id));
                    $("#songId" + id).attr("songid", song.id);
                }
                if ($("#title" + id)) {
                    $("#title" + id).html(truncate(song.title));
                    $("#title" + id).attr("title", song.title);
                }
                if ($("#album" + id)) {
                    $("#album" + id).html(truncate(song.album));
                    $("#album" + id).attr("title", song.album);
                    $("#albumUrl" + id).attr("href", "main.view?id=" + song.id);
                }
                if ($("#artist" + id)) {
                    $("#artist" + id).html(truncate(song.artist));
                    $("#artist" + id).attr("title", song.artist);
                }
                if ($("#songDuration" + id)) {
                    $("#songDuration" + id).html(song.durationAsString);
                }

                $("#pattern" + id).addClass((i % 2 == 0) ? "bgcolor2" : "bgcolor1");

                // Note: show() method causes page to scroll to top.
                $("#pattern" + id).css("display", "table-row");
            }
        }

        function truncate(s) {
            if (s == null) {
                return s;
            }
            var cutoff = 50;

            if (s.length > cutoff) {
                return s.substring(0, cutoff) + "...";
            }
            return s;
        }

        function onPlay(index) {
            top.playQueue.onPlay(songs[index].id);
        }
        function onPlayAll() {
            top.playQueue.onPlayPlaylist(playlist.id, false);
        }
		
        function onPlayAllRandom() {
            top.playQueue.onPlayPlaylist(playlist.id, true);
        }		
        function onAdd(index) {
            top.playQueue.onAdd(songs[index].id);
        }
        function onAddAll() {
            top.playQueue.onAddPlaylist(playlist.id);
        }
        function onStar(index, forced) {
            playlistService.toggleStar(playlist.id, index, forced, playlistCallback);
        }
        function onStarAll(forced) {
            playlistService.toggleAllStar(playlist.id, forced, playlistCallback);
        }
		
        function onStarSelected(forced) {
         	var mediaFileIds = new Array();
			var count = 0;
			
			for (var i = 0; i < songs.length+1; i++) {
				var checkbox = $("#songIndex" + i);
				if (checkbox && checkbox.is(":checked")) {
					onStar(i - 1, forced);
					count++;
				}
			}
			if (count > 0){
				$().toastmessage("showSuccessToast", "Toogle Stars");
			}

			parent.left.updatePlaylists();
			getPlaylist();
		}
		
        function onRemove(index) {
            playlistService.remove(playlist.id, index, function (playlistInfo){playlistCallback(playlistInfo); top.left.updatePlaylists()});
        }
        function onUp(index) {
            playlistService.up(playlist.id, index, playlistCallback);
        }
        function onDown(index) {
            playlistService.down(playlist.id, index, playlistCallback);
        }
        function onEditPlaylist() {
            $("#dialog-edit").dialog("open");
        }
        function onDeletePlaylist() {
            $("#dialog-delete").dialog("open");
        }

	    function onAppendPlaylist() {
        playlistService.getWritablePlaylists(playlistAppendCallback);
		}
		function playlistAppendCallback(playlists) {
			$("#dialog-select-playlist-list").empty();
			for (var i = 0; i < playlists.length; i++) {
				var playlist = playlists[i];
				$("<p class='dense'><b><a href='#' onclick='appendPlaylist(" + playlist.id + ")'>" + playlist.name + "</a></b></p>").appendTo("#dialog-select-playlist-list");
			}
			$("#dialog-select-playlist").dialog("open");
		}
		function appendPlaylist(playlistId) {
			$("#dialog-select-playlist").dialog("close");

			var mediaFileIds = new Array();
			for (var i = 0; i < songs.length; i++) {
				if ($("#songIndex" + (i + 1)).is(":checked")) {
					mediaFileIds.push(songs[i].id);
				}
			}
			playlistService.appendToPlaylist(playlistId, mediaFileIds, function (){
				parent.left.updatePlaylists();
				$().toastmessage("showSuccessToast", "<fmt:message key="playlist.toast.appendtoplaylist"/>");
			});
		}
		
		function selectAll(b) {
			for (var i = 0; i < songs.length; i++) {
				if (b) {
					$("#songIndex" + (i + 1)).attr("checked", "checked");
				} else {
					$("#songIndex" + (i + 1)).removeAttr("checked");
				}
			}
		}

		// --------------------------------------------
		function onAddSelectedNext() {
			var mediaFileIds = new Array();
			var count = 0;
			for (var i = 0; i < songs.length; i++) {
				var checkbox = $("#songIndex" + i);
				if (checkbox && checkbox.is(":checked")) {
					mediaFileIds.push($("#songId" + i).html());
					count++;
				}
			}
			parent.playQueue.onAddSelectedNext(mediaFileIds);
			if (count > 0) {
				$().toastmessage("showSuccessToast", "added next to PlayQueue");
			}
		}	

		// --------------------------------------------
		function onAddSelectedLast() {
			var mediaFileIds = new Array();
			var count = 0;
			for (var i = 0; i < songs.length; i++) {
				var checkbox = $("#songIndex" + i);
				if (checkbox && checkbox.is(":checked")) {
					mediaFileIds.push($("#songId" + i).html());
					count++;
				}
			}
			parent.playQueue.onAddSelectedLast(mediaFileIds);
			if (count > 0) {
				$().toastmessage("showSuccessToast", "added last to PlayQueue");
			}
		}	
	
    </script>
</head>
<body class="mainframe bgcolor1" onload="init()">

<h1 id="name">${model.playlist.name}</h1>
<h2>
	    <a href="javascript:void(0)" onclick="self.location.href='loadPlaylist.view?'">Playlists</a>

	    <img src="<spring:theme code="sepImage"/>" alt="">&nbsp;<a href="javascript:void(0)" onclick="onPlayAll();"><fmt:message key="common.play"/></a>

	    <img src="<spring:theme code="sepImage"/>" alt="">&nbsp;<a href="javascript:void(0)" onclick="onPlayAllRandom();">Play Random</a>
		
        <img src="<spring:theme code="sepImage"/>" alt="">&nbsp;<a href="javascript:void(0)" onclick="onAddAll();"><fmt:message key="common.add"/></a>	

		<img src="<spring:theme code="sepImage"/>" alt="">&nbsp;<a href="javascript:void(0)" onclick="selectAll(true);">Select</a>
		
	    <img src="<spring:theme code="sepImage"/>" alt="">&nbsp;<a href="javascript:void(0)" onclick="onAppendPlaylist();">Append</a>

	 <!--   <img src="<spring:theme code="sepImage"/>" alt="">&nbsp;<a href="javascript:void(0)" onclick="onStarAll();">Toogle Star</a> -->
		
    <c:if test="${model.user.downloadRole}">
        <c:url value="download.view" var="downloadUrl"><c:param name="playlist" value="${model.playlist.id}"/></c:url>
        <img src="<spring:theme code="sepImage"/>" alt="">&nbsp;<a href="${downloadUrl}"><fmt:message key="common.download"/></a>
    </c:if>
    <c:if test="${model.editAllowed}">
        <img src="<spring:theme code="sepImage"/>" alt="">&nbsp;<a href="javascript:void(0)" onclick="onEditPlaylist();"><fmt:message key="common.edit"/></a>
        <img src="<spring:theme code="sepImage"/>" alt="">&nbsp;<a href="javascript:void(0)" onclick="onDeletePlaylist();"><fmt:message key="common.delete"/></a>
    </c:if>
    <c:url value="exportPlaylist.view" var="exportUrl"><c:param name="id" value="${model.playlist.id}"/></c:url>
    <img src="<spring:theme code="sepImage"/>" alt="">&nbsp;<a href="${exportUrl}"><fmt:message key="playlist2.export"/></a>
	
    <c:if test="${model.user.shareRole}">	
    <c:url value="createPlaylistShare.view" var="shareUrl"><c:param name="id" value="${model.playlist.id}"/></c:url>
    <img src="<spring:theme code="sepImage"/>" alt="">&nbsp;<a href="${shareUrl}">Share</a>
    </c:if>
</h2>

<div id="comment" class="detail" style="padding-top:0.2em">${model.playlist.comment}</div>

	<select id="moreActions" onchange="actionSelected(this.options[selectedIndex].id);" style="margin-bottom:1.0em">
    <option id="top" selected="selected"><fmt:message key="main.more"/></option>
    <option style="color:blue;">all songs</option>
    <option id="selectAll">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.more.selectall"/></option>
    <option id="selectNone">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.more.selectnone"/></option>
    <option id="toogleAll">&nbsp;&nbsp;&nbsp;&nbsp;Toogle Starred</option>
    <option id="toogleAllForced">&nbsp;&nbsp;&nbsp;&nbsp;Forced All Starred</option>
	    <c:if test="${model.user.shareRole}">
        <option id="share">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="main.more.share"/></option>
    </c:if>
    <c:if test="${model.user.downloadRole}">
        <option id="download">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="common.download"/></option>
    </c:if>
	<option style="color:blue;"><fmt:message key="main.more.selection"/></option>
    <option id="appendPlaylist">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.append"/></option>
	<option id="toogleSelected">&nbsp;&nbsp;&nbsp;&nbsp;Toogle Starred</option>
    <option id="toogleSelectedForced">&nbsp;&nbsp;&nbsp;&nbsp;Forced Selection Starred</option>
    <option id="addNext">&nbsp;&nbsp;&nbsp;&nbsp;Add Next to PlayQueue</option>
    <option id="addLast">&nbsp;&nbsp;&nbsp;&nbsp;Add Last to PlayQueue</option>
    </select>

<div class="detail" style="padding-top:0.2em">
    <fmt:message key="playlist2.created">
        <fmt:param>${model.playlist.username}</fmt:param>
        <fmt:param><fmt:formatDate type="date" dateStyle="long" value="${model.playlist.created}"/></fmt:param>
    </fmt:message>.
    <span id="shared"></span>.
    <span id="songCount"></span> <fmt:message key="playlist2.songs"/> (<span id="duration"></span>)
</div>

<div style="height:0.7em"></div>

<p id="empty" style="display: none;"><em><fmt:message key="playlist2.empty"/></em></p>

<table style="border-collapse:collapse;white-space:nowrap">
    <tbody id="playlistBody">
    <tr id="pattern" style="display:none;margin:0;padding:0;border:0">
		<td><span id="rank"></span></td>	
        <td style="padding-right:0.25em"><a href="javascript:void(0)">
            <img id="starSong" onclick="onStar(this.id.substring(8) - 1,false)" src="<spring:theme code="ratingOffImage"/>" alt="" title=""></a></td>
        <td><a href="javascript:void(0)">
            <img id="play" src="<spring:theme code="playImage"/>" alt="<fmt:message key="common.play"/>" title="<fmt:message key="common.play"/>"
                 onclick="onPlay(this.id.substring(4) - 1)"></a></td>
        <td><a href="javascript:void(0)">
            <img id="add" src="<spring:theme code="addImage"/>" alt="<fmt:message key="common.add"/>" title="<fmt:message key="common.add"/>"
                 onclick="onAdd(this.id.substring(3) - 1)"></a></td>
        <c:if test="${model.editAllowed}">
            <td><a href="javascript:void(0)">
                <img id="removeSong" onclick="onRemove(this.id.substring(10) - 1)" src="<spring:theme code="removeImage"/>"
                     alt="<fmt:message key="playlist.remove"/>" title="<fmt:message key="playlist.remove"/>"></a></td>
        </c:if>
        <td style="padding-right:0.25em"></td>
		<td class="bgcolor2" style="padding-left: 0.1em"><input type="checkbox" class="checkbox" id="songIndex"></td>
        <td><span id="songId" style="display: none">id</span></td>	
        <td style="padding-right:1.25em"><span id="title">Title</span></td>
        <td style="padding-right:1.25em"><a id="albumUrl" target="main"><span id="album" class="detail">Album</span></a></td>
        <td style="padding-right:1.25em"><span id="artist" class="detail">Artist</span></td>
        <td style="padding-right:1.25em;text-align:right;"><span id="songDuration" class="detail">Duration</span></td>
		
        <c:if test="${model.editAllowed}">
            <td><a href="javascript:void(0)">
                <img id="up" onclick="onUp(this.id.substring(2) - 1)" src="<spring:theme code="upImage"/>"
                     alt="<fmt:message key="playlist.up"/>" title="<fmt:message key="playlist.up"/>"></a></td>
            <td><a href="javascript:void(0)">
                <img id="down" onclick="onDown(this.id.substring(4) - 1)" src="<spring:theme code="downImage"/>"
                     alt="<fmt:message key="playlist.down"/>" title="<fmt:message key="playlist.down"/>"></a></td>
        </c:if>

    </tr>
    </tbody>
</table>

<div id="dialog-select-playlist" title="<fmt:message key="main.addtoplaylist.title"/>" style="display: none;">
	<p><fmt:message key="main.addtoplaylist.text"/></p>
	<div id="dialog-select-playlist-list"></div>
</div>

<div id="dialog-delete" title="<fmt:message key="common.confirm"/>" style="display: none;">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
        <fmt:message key="playlist2.confirmdelete"/></p>
</div>

<div id="dialog-edit" title="<fmt:message key="common.edit"/>" style="display: none;">
    <form>
        <label for="newName" style="display:block;"><fmt:message key="playlist2.name"/></label>
        <input type="text" name="newName" id="newName" value="${model.playlist.name}" class="ui-widget-content"
               style="display:block;width:95%;"/>
        <label for="newComment" style="display:block;margin-top:1em"><fmt:message key="playlist2.comment"/></label>
        <input type="text" name="newComment" id="newComment" value="${model.playlist.comment}" class="ui-widget-content"
               style="display:block;width:95%;"/>
        <input type="checkbox" name="newPublic" id="newPublic" ${model.playlist['public'] ? "checked='checked'" : ""} style="margin-top:1.5em" class="ui-widget-content"/>
        <label for="newPublic"><fmt:message key="playlist2.isPublic"/></label>
    </form>
</div>
    <script type="text/javascript" language="javascript">

		<!-- actionSelected() is invoked when the users selects from the "More actions..." combo box. -->
		function actionSelected(id) {

			if (id == "top") {
				return;
			} else if (id == "selectAll") {
				selectAll(true);
			} else if (id == "selectNone") {
				selectAll(false);
			} else if (id == "share") {
				parent.frames.main.location.href = "${shareUrl}";
			} else if (id == "download") {
				location.href = "${downloadUrl}";
			} else if (id == "toogleAll") {
				onStarAll(false);
			} else if (id == "toogleAllForced") {
				onStarAll(true);	
			} else if (id == "toogleSelected") {
				onStarSelected(false);	
			} else if (id == "toogleSelectedForced") {
				onStarSelected(true);					
			} else if (id == "addNext") {
				onAddSelectedNext();
			} else if (id == "addLast") {
				onAddSelectedLast();
			} else if (id == "appendPlaylist") {
				onAppendPlaylist();
			} else if (id == "savePlaylist") {
				onSavePlaylist();
		} else if (id == "savePlaylistNamed") {
				onSavePlaylistNamed();
			}
			$("#moreActions").prop("selectedIndex", 0);

    }
    </script>
</body>
</html>