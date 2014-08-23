<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%--@elvariable id="model" type="java.util.Map"--%>

<html><head>
<%@ include file="head.jsp" %>
<%@ include file="jquery.jsp" %>

<link href="<c:url value="/style/shadow.css"/>" rel="stylesheet">

<c:if test="${not model.updateNowPlaying}">
	<meta http-equiv="refresh" content="180;URL=nowPlaying.view?">
</c:if>

<c:if test="${model.customScrollbar}">
	<link href="<c:url value="/style/customScrollbar.css"/>" rel="stylesheet">
	<script type="text/javascript" src="<c:url value="/script/jquery.mousewheel.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/script/jquery.mCustomScrollbar.js"/>"></script>
</c:if>	

<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
<script type="text/javascript" src="<c:url value="/dwr/interface/starService.js"/>"></script>
<script type="text/javascript" src="<c:url value="/dwr/interface/playlistService.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/fancyzoom/FancyZoom.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/fancyzoom/FancyZoomHTML.js"/>"></script>

</head><body class="mainframe bgcolor1" onload="init();">

<!-- content block -->

<div id="content_2" class="content_main">
<!-- CONTENT -->

<sub:url value="createShare.view" var="shareUrl">
    <sub:param name="id" value="${model.dir.id}"/>
</sub:url>
<sub:url value="download.view" var="downloadUrl">
    <sub:param name="id" value="${model.dir.id}"/>
</sub:url>
<sub:url value="appendPlaylist.view" var="appendPlaylistUrl">
    <sub:param name="id" value="${model.dir.id}"/>
</sub:url>

<script type="text/javascript" language="javascript">
    function init() {
        setupZoom('<c:url value="/"/>');

        $("#dialog-select-playlist").dialog({resizable: true, height: 350, position: [400,'center'], modal: true, autoOpen: false,
            buttons: {
                "<fmt:message key="common.cancel"/>": function() {
                    $(this).dialog("close");
                }
            }});
			
		//TODO: RefreshMedia	
		RefreshMediaType();
    }

    <!-- actionSelected() is invoked when the users selects from the "More actions..." combo box. -->
    function actionSelected(id) {
        var selectedIndexes = getSelectedIndexes();

        if (id == "top") {
            return;
        } else if (id == "selectAll") {
            selectAll(true);
        } else if (id == "selectNone") {
            selectAll(false);
        } else if (id == "share" && selectedIndexes != "") {
            parent.frames.main.location.href = "${shareUrl}&" + selectedIndexes;
        } else if (id == "download" && selectedIndexes != "") {
            location.href = "${downloadUrl}&" + getSelectedIndexes();
        } else if (id == "addNext" && selectedIndexes != "") {
            onAddSelectedNext();
        } else if (id == "addLast" && selectedIndexes != "") {
            onAddSelectedLast();
        } else if (id == "appendPlaylist" && selectedIndexes != "") {
            onAppendPlaylist();
        } else if (id == "savePlaylist") {
            onSavePlaylist();
	} else if (id == "savePlaylistNamed") {
            onSavePlaylistNamed();
        }
        $("#moreActions").prop("selectedIndex", 0);
    }

    function getSelectedIndexes() {
        var result = "";
        for (var i = 0; i < ${fn:length(model.children)}; i++) {
            var checkbox = $("#songIndex" + i);
            if (checkbox != null  && checkbox.is(":checked")) {
                result += "i=" + i + "&";
            }
        }
        return result;
    }

    function selectAll(b) {
        for (var i = 0; i < ${fn:length(model.children)}; i++) {
            var checkbox = $("#songIndex" + i);
            if (checkbox != null) {
                if (b) {
                    checkbox.attr("checked", "checked");
                } else {
                    checkbox.removeAttr("checked");
                }
            }
        }
    }

    function toggleStar(mediaFileId, imageId) {
        if ($(imageId).attr("src").indexOf("<spring:theme code="ratingOnImage"/>") != -1) {
            $(imageId).attr("src", "<spring:theme code="ratingOffImage"/>");
            starService.unstar(mediaFileId);
        }
        else if ($(imageId).attr("src").indexOf("<spring:theme code="ratingOffImage"/>") != -1) {
            $(imageId).attr("src", "<spring:theme code="ratingOnImage"/>");
            starService.star(mediaFileId);
        }
    }

    function onAppendPlaylist() {
        playlistService.getWritablePlaylists(playlistCallback);
    }
    function playlistCallback(playlists) {
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
        for (var i = 0; i < ${fn:length(model.children)}; i++) {
            var checkbox = $("#songIndex" + i);
            if (checkbox && checkbox.is(":checked")) {
                mediaFileIds.push($("#songId" + i).html());
            }
        }
        playlistService.appendToPlaylist(playlistId, mediaFileIds, function (){parent.left.updatePlaylists();});
    }

	// --------------------------------------------
	function onSavePlaylist() {
		selectAll(true);
		var mediaFileIds = new Array();
		for (var i = 0; i < ${fn:length(model.children)}; i++) {
			var checkbox = $("#songIndex" + i);
			if (checkbox && checkbox.is(":checked")) {
				mediaFileIds.push($("#songId" + i).html());
			}
		}
		playlistService.savePlaylist(mediaFileIds, function (){
		parent.left.updatePlaylists();
		$().toastmessage("showSuccessToast", "<fmt:message key="playlist.toast.saveasplaylist"/>");
		});
	}

	// --------------------------------------------
	function onSavePlaylistNamed() {
	
		selectAll(true);
		var mediaFileIds = new Array();
		var PlaylistName = "${model.artist} - ${model.album}";
		for (var i = 0; i < ${fn:length(model.children)}; i++) {
			var checkbox = $("#songIndex" + i);
			if (checkbox && checkbox.is(":checked")) {
				mediaFileIds.push($("#songId" + i).html());
			}
		}
		playlistService.savePlaylist(mediaFileIds, PlaylistName ,function (){
		parent.left.updatePlaylists();
		$().toastmessage("showSuccessToast", "<fmt:message key="playlist.toast.saveasplaylist"/>");
		});
	}	

	// --------------------------------------------
	function onAddSelectedNext() {
		var mediaFileIds = new Array();
		for (var i = 0; i < ${fn:length(model.children)}; i++) {
			var checkbox = $("#songIndex" + i);
			if (checkbox && checkbox.is(":checked")) {
				mediaFileIds.push($("#songId" + i).html());
			}
		}
		parent.playQueue.onAddSelectedNext(mediaFileIds);
		$().toastmessage("showSuccessToast", "added next to PlayQueue");
	}	

	// --------------------------------------------
	function onAddSelectedLast() {
		var mediaFileIds = new Array();
		for (var i = 0; i < ${fn:length(model.children)}; i++) {
			var checkbox = $("#songIndex" + i);
			if (checkbox && checkbox.is(":checked")) {
				mediaFileIds.push($("#songId" + i).html());
			}
		}
		parent.playQueue.onAddSelectedLast(mediaFileIds);
		$().toastmessage("showSuccessToast", "added last to PlayQueue");
	}		
	
function RefreshMediaType() {

//	console.log("--RefreshGUI--");
	var mediatype = document.getElementsByName("mediatype");
	for (var i=0; i < mediatype.length; i++) {
		if (mediatype[i].checked == true) {
	//		console.log("The " + (i + 1) + ". radio button is checked");
		}
	}	
	if ("${model.dir.mediaType}" == "MULTIARTIST") { mediatype[0].checked=true; }
	if ("${model.dir.mediaType}" == "ARTIST") 	   { mediatype[1].checked=true; }
	if ("${model.dir.mediaType}" == "DIRECTORY")   { mediatype[2].checked=true; }
	if ("${model.dir.mediaType}" == "VIDEOSET")    { mediatype[3].checked=true; }
	if ("${model.dir.mediaType}" == "ALBUMSET")    { mediatype[4].checked=true; }
	if ("${model.dir.mediaType}" == "ALBUM")       { mediatype[5].checked=true; }
	if ("${model.dir.mediaTypeOverride}"=="false") { mediatype[6].checked=true; }
	}
</script>

<c:if test="${model.updateNowPlaying}">

    <script type="text/javascript" language="javascript">
        // Variable used by javascript in playlist.jsp
        var updateNowPlaying = true;
    </script>
</c:if>

<h1>
    <a href="#" style="text-decoration:none;" onclick="toggleStar(${model.dir.id}, '#starImage'); return false;">
        <c:choose>
            <c:when test="${not empty model.dir.starredDate}">
                <img id="starImage" src="<spring:theme code="ratingOnImage"/>" alt="">
			</c:when>
            <c:otherwise>
                <img id="starImage" src="<spring:theme code="ratingOffImage"/>" alt="">
			</c:otherwise>
        </c:choose></a>

    <c:forEach items="${model.ancestors}" var="ancestor">
        <sub:url value="main.view" var="ancestorUrl">
            <sub:param name="id" value="${ancestor.id}"/>
		</sub:url>
		
	<c:choose>
		<c:when test="${fn:startsWith(ancestor.name,'[')}">
			<a href="${ancestorUrl}">${fn:split(ancestor.name,']')[1]}</a> &raquo;
		</c:when>
		<c:otherwise>
			<a href="${ancestorUrl}">${ancestor.name}</a> &raquo;
		</c:otherwise>
	</c:choose>		
    </c:forEach>

	<c:choose>
		<c:when test="${not empty model.dir.albumSetName}">
			${model.dir.albumSetName}
		</c:when>
		<c:otherwise>
			${model.dir.name}
		</c:otherwise>
	</c:choose>		

    <c:if test="${model.dir.album and model.averageRating gt 0}">
        &nbsp;&nbsp;
        <c:import url="rating.jsp">
            <c:param name="path" value="${model.dir.path}"/>
            <c:param name="readonly" value="true"/>
            <c:param name="rating" value="${model.averageRating}"/>
        </c:import>
    </c:if>
</h1>

<c:if test="${not model.partyMode}">
<h2>
<div class="maincontrol">
    <c:if test="${model.navigateUpAllowed}">
        <sub:url value="main.view" var="upUrl">
            <sub:param name="id" value="${model.parent.id}"/>
		</sub:url>
        <a href="${upUrl}"> <img src="icons/default/main-up.png"><fmt:message key="main.up"/></a>
        <c:set var="needSep" value="true"/>
    </c:if>

    <c:if test="${model.user.streamRole}">
        <c:if test="${needSep}"> <img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;"></c:if> 
        <a href="#" onclick="parent.playQueue.onPlay(${model.dir.id});"> <img src="icons/default/main-play.png"> <fmt:message key="main.playall"/></a> 
		<img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;">
        <a href="#" onclick="parent.playQueue.onPlayRandom(${model.dir.id}, 9);"> <img src="icons/default/main-random.png"> <fmt:message key="main.playrandom"/> </a>
		<c:if test="${model.user.lastFMRole}">
			<c:if test="${not model.dir.album}">
				<c:if test="${model.dir.mediaType ne 'VIDEOSET'}">
				<img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;">
				<a href="#" onclick="parent.playQueue.onPlayTopTrack(&quot;${model.artist}&quot;);"> <img src="icons/default/main-play.png"> <fmt:message key="main.playtoptrack"/> <span style="font-size:75% !important;"> (${model.topPlayedfound}|${model.toplastFMfound})</span> </a> <a href="#" onclick="parent.playQueue.onUpdateTopTrack(&quot;${model.artist}&quot; , ${model.dir.id});"> <img src="<spring:theme code="spinnerImage"/>" style="width:14px;"></a>
				</c:if>
			</c:if>
		</c:if>
		<img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;">
        <a href="#" onclick="parent.playQueue.onAdd(${model.dir.id});"> <img src="icons/default/main-addall.png"> <fmt:message key="main.addall"/></a>
		<c:if test="${model.user.lastFMRole}">
			<c:if test="${not model.dir.album}">
				<c:if test="${model.dir.mediaType ne 'VIDEOSET'}">
				<img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;">
				<a href="#" onclick="parent.playQueue.onAddTopTrack(&quot;${model.artist}&quot;);"> <img src="icons/default/main-addall.png"> <fmt:message key="main.addtoptrack"/></a>
				</c:if>
			</c:if>
		</c:if>
        <c:set var="needSep" value="true"/>
    </c:if>

    <c:if test="${model.dir.album}">

        <c:if test="${model.user.downloadRole}">
            <sub:url value="download.view" var="downloadUrl">
                <sub:param name="id" value="${model.dir.id}"/>
            </sub:url>
            <c:if test="${needSep}"> <img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;"></c:if>
            <a href="${downloadUrl}"> <img src="icons/default/main-save.png"> <fmt:message key="common.download"/> </a>
            <c:set var="needSep" value="true"/>
        </c:if>

        <c:if test="${model.user.coverArtRole}">
            <sub:url value="editTags.view" var="editTagsUrl">
                <sub:param name="id" value="${model.dir.id}"/>
            </sub:url>
            <c:if test="${needSep}"><img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;"></c:if>
            <a href="${editTagsUrl}"> <img src="icons/default/main-tag.png"> <fmt:message key="main.tags"/> </a>
            <c:set var="needSep" value="true"/>
        </c:if>

    </c:if>
	
    <c:if test="${model.user.commentRole}">
        <c:if test="${needSep}"><img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;"></c:if>
        <a href="javascript:toggleComment()"> <img src="icons/default/main-edit.png"> <fmt:message key="main.comment"/> </a>
	<c:set var="needSep" value="true"/>
    </c:if>

	<c:if test="${model.user.adminRole}">
		<c:if test="${needSep}"><img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;"></c:if>
        <a href="javascript:toggleCreateURL()"> <img src="icons/default/main-mediatype.png"> Create Link</a>
    </c:if>
	
    <c:if test="${model.user.coverArtRole}">
		<c:if test="${needSep}"><img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;"></c:if>
        <a href="javascript:toggleMediaType()"> <img src="icons/default/main-mediatype.png"> MediaType </a>
    </c:if>

</div>
</h2>

</c:if>
<c:if test="${not model.dir.album}">
<c:if test="${model.user.searchRole}">

		<div class="detail">
		<div class="maincontrol">
            <sub:url value="http://www.google.com/search" var="googleUrl" encoding="UTF-8">
                <sub:param name="q" value="\"${model.dir.name}\""/>
            </sub:url>
            <sub:url value="http://en.wikipedia.org/wiki/Special:Search" var="wikipediaUrl" encoding="UTF-8">
                <sub:param name="search" value="\"${model.dir.name}\""/>
                <sub:param name="go" value="Go"/>
            </sub:url>
            <sub:url value="http://www.discogs.com/search" var="discogsUrl" encoding="UTF-8">
                <sub:param name="q" value="${model.dir.name}"/>
                <sub:param name="type" value="artist"/>				
            </sub:url>
			<sub:url value="http://www.musik-sammler.de/index.php" var="MusiksammlerUrl" encoding="UTF-8">
                <sub:param name="do" value="search"/>
				<sub:param name="artist" value="\"${model.dir.name}\""/>
            </sub:url>
			<sub:url value="http://www.laut.de/Suche" var="lautUrl" encoding="UTF-8">
                <sub:param name="suchbegriff" value="${model.dir.name}"/>
            </sub:url>
            <sub:url value="http://www.last.fm/search" var="lastFmUrl" encoding="UTF-8">
                <sub:param name="q" value="\"${model.dir.name}\""/>
                <sub:param name="type" value="artist"/>
            </sub:url>
			<sub:url value="http://musicbrainz.org/search" var="musicbrainzUrl" encoding="UTF-8">
                <sub:param name="query" value="${model.dir.name}"/>
                <sub:param name="type" value="artist"/>
            </sub:url>			
            <sub:url value="http://www.youtube.com/results" var="YoutubeUrl" encoding="UTF-8">
                <sub:param name="search_query" value="${model.dir.name}"/>
            </sub:url>
			
			<span display="inline" class="detailcolor" style="margin-left: 10px;"><fmt:message key="top.search"/></span> 

            <a target="_blank" href="${googleUrl}"> <img src="<spring:theme code="googleImage"/>" alt="" title="Search with Google"> Google</a> 
			<img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;">
			<!--
            <a target="_blank" href="${wikipediaUrl}"> <img src="<spring:theme code="wikipediaImage"/>" alt="" title="Search with Wikipedia"> Wikipedia </a>
			<img src="<spring:theme code="sepImage"/>" alt="">
	
            <a target="_blank" href="${MusiksammlerUrl}">MusikSammler</a> <img src="<spring:theme code="sepImage"/>" alt="">
            <a target="_blank" href="${lautUrl}">Laut</a> <img src="<spring:theme code="sepImage"/>" alt=""> -->
			
            <a target="_blank" href="${discogsUrl}"><img src="<spring:theme code="discosgsImage"/>" alt="" title="Search with Discogs"> Discogs</a> 
			<img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;">
			
			<a target="_blank" href="${musicbrainzUrl}"><img src="<spring:theme code="musicbrainzImage"/>" alt="" title="Search with MusicBrainz"> MusicBrainz</a> 
			<img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;">
			
            <a target="_blank" href="${lastFmUrl}"><img src="<spring:theme code="lastfmImage"/>" alt="" title="Search with Last.FM"> Last.FM</a>
			<img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;">
			
            <a target="_blank" href="${YoutubeUrl}"><img src="<spring:theme code="youtubeImage"/>" alt="" title="Search with Youtube"> Youtube</a>
	</div>
</div>
</c:if>
</c:if>

<c:if test="${model.dir.album}">

    <c:if test="${model.user.searchRole}">

    <div class="detail">
	    <div class="maincontrol" style="display:inline;padding-left:2px">
		
        <c:if test="${not empty model.artist or not empty model.album}">
            <sub:url value="http://www.google.com/search" var="googleUrl" encoding="UTF-8">
                <sub:param name="q" value="\"${model.artist}\" \"${model.album}\""/>
            </sub:url>
            <sub:url value="http://en.wikipedia.org/wiki/Special:Search" var="wikipediaUrl" encoding="UTF-8">
                <sub:param name="search" value="\"${model.album}\""/>
                <sub:param name="go" value="Go"/>
            </sub:url>
            <sub:url value="http://www.discogs.com/search" var="discogsUrl" encoding="UTF-8">
                <sub:param name="q" value="${model.artist}+${model.album}"/>
                <sub:param name="type" value="all"/>				
            </sub:url>
			<sub:url value="http://www.musik-sammler.de/index.php" var="MusiksammlerUrl" encoding="UTF-8">
                <sub:param name="do" value="search"/>
				<sub:param name="artist" value="\"${model.artist}\""/>
            </sub:url>
			<sub:url value="http://www.laut.de/Suche" var="lautUrl" encoding="UTF-8">
                <sub:param name="suchbegriff" value="${model.artist} ${model.album}"/>
            </sub:url>
            <sub:url value="http://www.last.fm/search" var="lastFmUrl" encoding="UTF-8">
                <sub:param name="q" value="\"${model.artist}\" \"${model.album}\""/>
                <sub:param name="type" value="album"/>
            </sub:url>
			<sub:url value="http://musicbrainz.org/search" var="musicbrainzUrl" encoding="UTF-8">
                <sub:param name="query" value="${model.album}"/>
                <sub:param name="type" value="release"/>
            </sub:url>			
            <sub:url value="http://www.youtube.com/results" var="YoutubeUrl" encoding="UTF-8">
                <sub:param name="search_query" value="${model.artist} ${model.album}"/>
            </sub:url>
			<span display="inline" class="detailcolor"><fmt:message key="top.search"/></span> 
            <a target="_blank" href="${googleUrl}"> <img src="<spring:theme code="googleImage"/>" alt="" title="Search with Google"> Google</a> 
			<img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;">
			
            <a target="_blank" href="${wikipediaUrl}"> <img src="<spring:theme code="wikipediaImage"/>" alt="" title="Search with Wikipedia"> Wikipedia </a>
			<img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;">
			<!--
            <a target="_blank" href="${MusiksammlerUrl}">MusikSammler</a> <img src="<spring:theme code="sepImage"/>" alt="">
            <a target="_blank" href="${lautUrl}">Laut</a> <img src="<spring:theme code="sepImage"/>" alt=""> -->
			
            <a target="_blank" href="${discogsUrl}"><img src="<spring:theme code="discosgsImage"/>" alt="" title="Search with Discogs"> Discogs</a>
			<img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;">
			
			<a target="_blank" href="${musicbrainzUrl}"><img src="<spring:theme code="musicbrainzImage"/>" alt="" title="Search with MusicBrainz"> MusicBrainz</a> 
			<img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;">
			
            <a target="_blank" href="${lastFmUrl}"><img src="<spring:theme code="lastfmImage"/>" alt="" title="Search with Last.FM"> Last.FM</a>
			<img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;">
			
            <a target="_blank" href="${YoutubeUrl}"><img src="<spring:theme code="youtubeImage"/>" alt="" title="Search with Youtube"> Youtube</a>
        </c:if>
	</div>
	<c:if test="${model.user.shareRole and model.dir.mediaType ne 'ALBUMSET'}">
		<img src="<spring:theme code="sepImage"/>" style="margin-left: 5px;margin-right: 5px;">
		<a href="${shareUrl}"><span class="detailcolor" style="margin-right: 5px;"><fmt:message key="main.sharealbum"/></span></a> 
		<a href="${shareUrl}"><img src="<spring:theme code="shareFacebookImage"/>" alt=""></a>
		<a href="${shareUrl}"><img src="<spring:theme code="shareTwitterImage"/>" alt=""></a>
		<a href="${shareUrl}"><img src="<spring:theme code="shareGooglePlusImage"/>" alt=""></a>
	</c:if>
	</div>
	</c:if>
	
<div style="padding-top:0.8em">
	<select id="moreActions" onchange="actionSelected(this.options[selectedIndex].id);" style="margin-bottom:1.0em">
    <option id="top" selected="selected"><fmt:message key="main.more"/></option>
    <option style="color:blue;">all songs</option>
    <option id="savePlaylist">&nbsp;&nbsp;&nbsp;&nbsp;Save as Playlist (Date)</option>
    <option id="savePlaylistNamed">&nbsp;&nbsp;&nbsp;&nbsp;Save as Playlist (Named)</option>	
	<option style="color:blue;"><fmt:message key="main.more.selection"/></option>
    <option id="selectAll">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.more.selectall"/></option>
    <option id="selectNone">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.more.selectnone"/></option>
    <c:if test="${model.user.shareRole}">
        <option id="share">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="main.more.share"/></option>
    </c:if>
    <c:if test="${model.user.downloadRole}">
        <option id="download">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="common.download"/></option>
    </c:if>
    <option id="appendPlaylist">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="playlist.append"/></option>
    <option id="addNext">&nbsp;&nbsp;&nbsp;&nbsp;Add Next to PlayQueue</option>
    <option id="addLast">&nbsp;&nbsp;&nbsp;&nbsp;Add Last to PlayQueue</option>
    </select>
	
	<c:if test="${model.user.commentRole}">
	<span display="inline" class="detail" style="padding-left:1.5em">
		<c:import url="rating.jsp">
			<c:param name="id" value="${model.dir.id}"/>
			<c:param name="path" value="${model.dir.path}"/>
			<c:param name="readonly" value="false"/>
			<c:param name="rating" value="${model.userRating}"/>
		</c:import>
		</span>
	</c:if>
	
	<c:if test="${model.user.commentRole}">
	<span display="inline" class="detail" style="padding-left:1.5em">	
		<c:import url="hot.jsp">
			<c:param name="id" value="${model.dir.id}"/>
			<c:param name="flag" value="${model.hotRating}"/>
		</c:import>
		</span>
	</c:if>		
</div>

<span display="inline" class="detailcolor" style="padding-left:2px;padding-top:0.8em">

<fmt:message key="main.playcount"><fmt:param value="${model.dir.playCount}"/></fmt:message>
<c:if test="${not empty model.dir.lastPlayed}">
	<fmt:message key="main.lastplayed">
		<fmt:param><fmt:formatDate type="date" dateStyle="long" value="${model.dir.lastPlayed}"/></fmt:param>
	</fmt:message>
</c:if>
</span>
	
</c:if>
<br>
<div id="createURLForm" display="inline" class="detailcolor" style="display:none">
    <form method="post" action="createURL.view">
        <input type="hidden" name="action" value="create">
        <input type="hidden" name="id" value="${model.dir.id}">	
		https://www.youtube.com/watch?v=
        <input name="URL" size="15" value="FJuu6LNRDCA">
		<input name="title" size="35" value="YouTube Video">
        <input type="submit" value="Create Link">
    </form>
</div>


<div id="MediaTypeSwitch" style="display:none">
	<br>
	Here you can override your current MediaFile-Type.<br>
	<br>
		<form method="post" action="setMediaFile.view">
		<input type="hidden" name="id" value="${model.dir.id}">
        <input type="hidden" name="action" value="setmediatype">
		<input type="radio" name="mediatype" value="MULTIARTIST"> MULTIARTIST (Sorting on Title)<br>
		<input type="radio" name="mediatype" value="ARTIST"> ARTIST<br>
		<br>
		<input type="radio" name="mediatype" value="DIRECTORY"> DIRECTORY<br>
		<input type="radio" name="mediatype" value="VIDEOSET"> VIDEOSET <br>		
		<input type="radio" name="mediatype" value="ALBUMSET"> ALBUMSET <br>
		<input type="radio" name="mediatype" value="ALBUM"> ALBUM <br>
		<br>
		<input type="radio" name="mediatype" value="AUTO"> AUTO<br>
		<br>
		<input type="submit" value="Update">
	<br><br>
	<span class="detailcolor">-- DEBUGINFO --</span><br><br>
	<span class="detailcolordark">Media Id </span>
	<span class="detailcolor">${model.dir.id} </span><br>
	<span class="detailcolordark">Override </span>
	<span class="detailcolor">${model.dir.mediaTypeOverride} </span><br>
	<span class="detailcolordark">MediaType </span>
	<span class="detailcolor">${model.dir.mediaType}</span>	<br>
	<br>
	</form>	
</div>

<div id="comment" class="albumComment"><sub:wiki text="${model.dir.comment}"/></div>

<div id="commentForm" style="display:none">
    <form method="post" action="setMusicFileInfo.view">
        <input type="hidden" name="action" value="comment">
        <input type="hidden" name="id" value="${model.dir.id}">	
        <textarea name="comment" rows="6" cols="70">${model.dir.comment}</textarea>
        <input type="submit" value="<fmt:message key="common.save"/>">
    </form>
    <fmt:message key="main.wiki"/>
</div>

<script type='text/javascript'>
    function toggleComment() {
        $("#commentForm").toggle();
        $("#comment").toggle();
    }
	
    function toggleMediaType() {
        $("#MediaTypeSwitch").toggle();
    }	
	
    function toggleCreateURL() {
        $("#createURLForm").toggle();
    }	
	
</script>

<c:if test="${model.user.adminRole}">
	<c:if test="${model.showQuickEdit}">
		<c:if test="${model.dir.mediaType eq 'ARTIST'} ">
			<form method="post" action="setMediaFile.view">
			<input type="hidden" name="id" value="${model.dir.id}">
			<input type="hidden" name="action" value="setmediatype">
			<input type="hidden" name="mediatype" checked value="ALBUMSET">
			<input type="submit" value="Update to ALBUMSET">
		</c:if>		
		<c:if test="${model.dir.mediaType eq 'ARTIST'}">
			<form method="post" action="setMediaFile.view">
			<input type="hidden" name="id" value="${model.dir.id}">
			<input type="hidden" name="action" value="setmediatype">
			<input type="hidden" name="mediatype" checked value="MULTIARTIST">
			<input type="submit" value="Update to MULTIARTIST">
			</c:if>	
		<c:if test="${model.dir.mediaType eq 'VIDEOSET'}">
			<form method="post" action="setMediaFile.view">
			<input type="hidden" name="id" value="${model.dir.id}">
			<input type="hidden" name="action" value="setmediatype">
			<input type="hidden" name="mediatype" checked value="ALBUMSET">
			<input type="submit" value="Update to ALBUMSET">
		</c:if>	
		<c:if test="${model.dir.mediaType ne 'VIDEOSET'}">
			<form method="post" action="setMediaFile.view">
			<input type="hidden" name="id" value="${model.dir.id}">
			<input type="hidden" name="action" value="setmediatype">
			<input type="hidden" name="mediatype" checked value="VIDEOSET">
			<input type="submit" value="Update to VIDEOSET">
		</c:if>			
		<c:if test="${model.dir.mediaType eq 'ALBUMSET' || model.dir.mediaType eq 'ALBUM' }">
			<form method="post" action="setMediaFile.view">
			<input type="hidden" name="id" value="${model.dir.id}">
			<input type="hidden" name="action" value="setmediatype">
			<input type="hidden" name="mediatype" checked value="ARTIST">
			<input type="submit" value="Update to ARTIST">
		</c:if>		
		<c:if test="${model.dir.mediaType ne 'ALBUM'}">
			<form method="post" action="setMediaFile.view">
			<input type="hidden" name="id" value="${model.dir.id}">
			<input type="hidden" name="action" value="setmediatype">
			<input type="hidden" name="mediatype" checked value="ALBUM">
			<input type="submit" value="Update to ALBUM">
		</c:if>			
		<c:if test="${model.dir.mediaType eq 'ARTIST' || model.dir.mediaType eq 'MULTIARTIST' }">
			<form method="post" action="setMediaFile.view">
			<input type="hidden" name="id" value="${model.dir.id}">
			<input type="hidden" name="action" value="setmediatype">
			<input type="hidden" name="mediatype" checked value="DIRETORY">
			<input type="submit" value="Update to DIRETORY">
		</c:if>	
		<span class="detailcolor">now: ${model.dir.mediaType}</span>	
	</c:if>	
</c:if>	

	
<table cellpadding="10" style="width:100%">
<tr style="vertical-align:top;">
    <td style="vertical-align:top;min-width:340px;">
        <table style="border-collapse:collapse;white-space:nowrap">
            <c:set var="cutoff" value="${model.visibility.captionCutoff}"/>
            <c:forEach items="${model.children}" var="child" varStatus="loopStatus">
                <%--@elvariable id="child" type="org.madsonic.domain.MediaFile"--%>
                <c:choose>
                    <c:when test="${loopStatus.count % 2 == 1}">
                        <c:set var="htmlClass" value="class='bgcolor2'"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="htmlClass" value=""/>
                    </c:otherwise>
				</c:choose>
				<c:choose>
				<c:when test="${model.dir.mediaType eq 'MULTIARTIST' or model.dir.mediaType eq 'VIDEOSET'}">
					<tr style="margin:0;padding:0;border:0">
                    <c:import url="playAddDownload.jsp">
                        <c:param name="id" value="${child.id}"/>
                        <c:param name="video" value="true"/>
						<c:param name="playEnabled" value="${ (model.dir.mediaType eq 'VIDEOSET') and not child.directory}"/>
                        <c:param name="playAddEnabled" value="false"/>
                        <c:param name="addEnabled" value="false"/>
                        <c:param name="downloadEnabled" value="false"/>
                        <c:param name="artist" value="${fn:escapeXml(child.artist)}"/>
                        <c:param name="title" value="${child.title}"/>
                        <c:param name="starEnabled" value="${model.buttonVisibility.starredVisible}"/>
                        <c:param name="starred" value="${not empty child.starredDate}"/>
                        <c:param name="asTable" value="true"/>
                        <c:param name="YoutubeEnabled" value="false"/>
                    </c:import>
                    </c:when>
                    <c:otherwise>
					<tr style="margin:0;padding:0;border:0">

					<c:if test="${child.rank > 0}">
						<td style="padding-right:0.25em;text-align:left;">
							<c:if test="${model.buttonVisibility.rankVisible}">
								<span class="rank">
								<c:if test="${child.rank < 10}">0</c:if>${child.rank}</span>
							</c:if>
						</td>							
					</c:if>
				
				<c:if test="${child.rank == 0}">
					<td style="padding-right:0.25em;"></td>							
				</c:if>
				
                    <c:import url="playAddDownload.jsp">
                        <c:param name="id" value="${child.id}"/>
                        <c:param name="video" value="${child.video and model.player.web}"/>
                        <c:param name="playEnabled" value="${model.user.streamRole and not model.partyMode and model.buttonVisibility.playVisible}"/>
                        <c:param name="playAddEnabled" value="${model.user.streamRole and not model.partyMode and model.buttonVisibility.playAddVisible}"/>
                        <c:param name="playMoreEnabled" value="${model.user.streamRole and not model.partyMode and model.buttonVisibility.playMoreVisible}"/>
                        <c:param name="addEnabled" value="${model.user.streamRole and (not model.partyMode or not child.directory) and model.buttonVisibility.addContextVisible}"/>
                        <c:param name="addNextEnabled" value="${model.user.streamRole and (not model.partyMode or not child.directory) and model.buttonVisibility.addNextVisible}"/>
                        <c:param name="addLastEnabled" value="${model.user.streamRole and (not model.partyMode or not child.directory) and model.buttonVisibility.addLastVisible}"/>						
                        <c:param name="downloadEnabled" value="${model.user.downloadRole and not model.partyMode and model.buttonVisibility.downloadVisible}"/>
                        <c:param name="artist" value="${fn:escapeXml(child.artist)}"/>
                        <c:param name="title" value="${child.title}"/>
                        <c:param name="starEnabled" value="${model.buttonVisibility.starredVisible}"/>
                        <c:param name="starred" value="${not empty child.starredDate}"/>
                        <c:param name="asTable" value="true"/>
                        <c:param name="YoutubeEnabled" value="${model.buttonVisibility.youtubeVisible}"/>
                        </c:import>
                    </c:otherwise>
                </c:choose>

                    <c:choose>
                        <c:when test="${child.directory}">
                            <sub:url value="main.view" var="childUrl">
                                <sub:param name="id" value="${child.id}"/>
                            </sub:url>
 
							<td style="padding-left:0.5em" colspan="1">
								<c:if test="${child.mediaType eq 'ALBUMSET' and model.dir.mediaType ne 'VIDEOSET'}">
									<img id="cdImage" src="<spring:theme code="CDImage"/>" alt="Albumset">
								</c:if>									
							</td>
							
							<c:choose>
								<c:when test="${model.showAlbumYear}">
									<td style="padding-left:0.50em">
									<span class="detail"><c:if test="${not empty child.year}">[${child.year}]</c:if></span></td>
								</c:when>
								<c:otherwise>
								</c:otherwise>
							</c:choose>

							<c:if test="${not empty child.year}">
								<c:if test="${not empty child.albumSetName}">
									<td style="padding-left:0.5em" colspan="5">
										<a href="${childUrl}" title="${child.albumSetName}"><span class="album" style="white-space:nowrap;vertical-align:bottom;"><str:truncateNicely upper="${cutoff}">${child.albumSetName}</str:truncateNicely></span></a>

									<c:if test="${child.newAdded}">
										<img id="newaddedImage" src="<spring:theme code="newaddedImage"/>" width="14" hight="14" title="new added">
									</c:if>
									</td>
									
								</c:if>
								<c:if test="${not empty child.albumName and empty child.albumSetName}">
									<td style="padding-left:0.5em" colspan="5">
										<a href="${childUrl}" title="${child.albumName}"><span style="white-space:nowrap;vertical-align: bottom;"><str:truncateNicely upper="${cutoff}">${child.albumSetName}</str:truncateNicely></span></a>
										<img id="cdImage" src="<spring:theme code="CDImage"/>" alt="Albumset">
									</td>
								</c:if>
							</c:if>

							<c:if test="${empty child.year}">
								<c:if test="${not empty child.name}">
									<td style="padding-left:0.5em" colspan="5">
										<a href="${childUrl}" title="${child.albumSetName}"><span style="white-space:nowrap;"><str:truncateNicely upper="${cutoff}">${child.name}</str:truncateNicely></span></a>
									</td>
								</c:if>
							</c:if>
						</c:when>

                        <c:otherwise>
						
							<c:if test="${model.dir.mediaType ne 'VIDEOSET'}">
                            <td ${htmlClass} style="padding-left:0.25em;padding-right:0.5em;"><input type="checkbox" class="checkbox" id="songIndex${loopStatus.count - 1}">
                                <span id="songId${loopStatus.count - 1}" style="display: none">${child.id}</span></td>
							</c:if>	

                            <c:if test="${model.visibility.discNumberVisible}">
                                <td ${htmlClass} style="padding-right:0.75em;text-align:right">
                                    <span class="detail">${child.discNumber}</span>
                                </td>
                            </c:if>								
								
                            <c:if test="${model.visibility.trackNumberVisible}">
                                <td ${htmlClass} style="padding-right:1.0em;text-align:right">
                                    <span class="detail">${child.trackNumber}</span>
                                </td>
                            </c:if>

                            <td ${htmlClass} style="padding-right:2.0em;white-space:nowrap">
                                    <span class="songTitle" title="${child.title}"><str:truncateNicely upper="${cutoff}">${fn:escapeXml(child.title)}</str:truncateNicely></span>
                            </td>

                            <c:if test="${model.visibility.albumVisible}">
                                <td ${htmlClass} style="padding-right:1.75em;white-space:nowrap">
                                    <span class="detail" title="${child.albumName}"><str:truncateNicely upper="${cutoff}">${fn:escapeXml(child.albumName)}</str:truncateNicely></span>
                                </td>
                            </c:if>

                            <c:if test="${model.visibility.artistVisible and model.multipleArtists}">
                                <td ${htmlClass} style="padding-right:1.75em;white-space:nowrap">
                                    <span class="detail" title="${child.artist}"><str:truncateNicely upper="${cutoff}">${fn:escapeXml(child.artist)}</str:truncateNicely></span>
                                </td>
                            </c:if>

                            <c:if test="${model.visibility.moodVisible}">
                                <td ${htmlClass} style="padding-right:2.25em;white-space:nowrap">
                                    <span class="detail">${child.mood}</span>
                                </td>
                            </c:if>

                            <c:if test="${model.visibility.genreVisible}">
                                <td ${htmlClass} style="padding-right:2.0em;white-space:nowrap">
                                    <span class="detail">${child.genre}</span>
                                </td>
                            </c:if>

                            <c:if test="${model.visibility.yearVisible}">
                                <td ${htmlClass} style="padding-right:1.50em">
                                    <span class="detail">${child.year}</span>
                                </td>
                            </c:if>

                            <c:if test="${model.visibility.formatVisible}">
                                <td ${htmlClass} style="padding-right:1.25em">
                                    <span class="detail">${fn:toLowerCase(child.format)}</span>
                                </td>
                            </c:if>

                            <c:if test="${model.visibility.fileSizeVisible}">
                                <td ${htmlClass} style="padding-right:1.25em;text-align:right">
                                    <span class="detail"><sub:formatBytes bytes="${child.fileSize}"/></span>
                                </td>
                            </c:if>

                            <c:if test="${model.visibility.durationVisible}">
                                <td ${htmlClass} style="padding-right:1.25em;text-align:right">
                                    <span class="detail">${child.durationString}</span>
                                </td>
                            </c:if>

                            <c:if test="${model.visibility.bitRateVisible}">
                                <td ${htmlClass} style="padding-right:0.25em">
                                    <span class="detail">
                                        <c:if test="${not empty child.bitRate}">
                                            ${child.bitRate} Kbps ${child.variableBitRate ? "vbr" : ""}
                                        </c:if>
                                        <c:if test="${child.video and not empty child.width and not empty child.height}">
                                            (${child.width}x${child.height})
                                        </c:if>
                                    </span>
                                </td>
                            </c:if>


                        </c:otherwise>
                    </c:choose>
                </tr>
            </c:forEach>
        </table>
    </td>

	
    <td style="vertical-align:top;width:100%">

	<c:set var="coverArtSize" value="${model.player.coverArtScheme.size}"/>
	<c:set var="captionLength" value="${model.player.coverArtScheme.captionLength}"/>
	
	<c:if test="${model.showGenericArtistArt} and ${model.artist} eq null">
	</c:if>

	<c:if test="${model.showGenericArtistArt}">
<!--	<div style="float:top; display:block; padding:5px"> -->
        <div class="artistbanner" style="padding:6px;"> 
		<c:import url="coverArt.jsp">
			<c:param name="albumId" value="${model.dir.id}"/>
			<c:param name="artistName" value="${model.dir.name}"/>
			<c:param name="coverArtSize" value="${model.coverArtSize}"/>
			<c:param name="showLink" value="false"/>
			<c:param name="showZoom" value="false"/>
			<c:param name="showChange" value="false"/>
			<c:param name="showArtist" value="true"/>
			<c:param name="typArtist" value="true"/>
			<c:param name="appearAfter" value="0"/>
		</c:import>
	</div>
	</c:if>
		
		<c:forEach items="${model.coverArts}" var="coverArt" varStatus="loopStatus">
		
<!--	<div style="float:right; width:60%; hight:120px; font-size: 7pt; display:overflow">
		<p>${model.dir.comment}</p>
		</div>	-->

		<c:choose>
		<c:when test="${coverArt eq model.dir or coverArt eq model.album}">
<!--    <div style="float:top; display:block; padding:5px"> -->
        <div class="artistbanner" style="padding:6px;"> 
		</c:when>
		<c:otherwise>
<!--			<div style="float:top; display:block; padding:5px"> -->
<!--            <div style="float:left; padding:5px"> -->
                <div style="float:left; padding:6px"> 
		</c:otherwise>
		</c:choose>	
                <c:import url="coverArt.jsp">
                    <c:param name="albumId" value="${coverArt.id}"/>
                    <c:param name="albumName" value="${coverArt.albumSetName}"/>
                    <c:param name="artistName" value="${coverArt.artist}"/>       
                    <c:param name="typVideo" value="${model.dir.mediaType eq 'VIDEOSET'}"/>
					<c:param name="coverArtSize" value="${model.coverArtSize}"/>
                    <c:param name="showLink" value="${coverArt ne model.dir}"/>
                    <c:param name="showZoom" value="${coverArt eq model.dir}"/>
                    <c:param name="showArtist" value="${(coverArt eq model.dir) and model.user.lastFMRole}"/> 
                    <c:param name="showChange" value="${(coverArt eq model.dir) and (not model.isArtist) and model.user.coverArtRole}"/>
                    <c:param name="showCaption" value="true"/>
                    <c:param name="captionLength" value="${captionLength}"/>
                    <c:param name="appearAfter" value="${loopStatus.count * 30}"/>
                </c:import>
            </div>
        </c:forEach>

	<c:if test="${model.multipleArtists}">
	</c:if>

	<c:if test="${model.showGenericCoverArt}">
	<div class="coverart" style="float:left; padding:6px">
		<c:import url="coverArt.jsp">
			<c:param name="albumId" value="${model.dir.id}"/>
			<c:param name="coverArtSize" value="${model.coverArtSize}"/>
			<c:param name="showLink" value="false"/>
			<c:param name="showZoom" value="false"/>
			<c:param name="showChange" value="${model.user.coverArtRole}"/>
			<c:param name="appearAfter" value="0"/>
		</c:import>
	</div>
	</c:if>
    </td>

    <td style="vertical-align:top;">
	
	   <c:forEach items="${model.sieblingAlbums}" var="sieblingAlbum" varStatus="loopStatus">
	   <div class="coverart" style="padding:6px"> 
			<c:import url="coverArt.jsp">
				<c:param name="albumId" value="${sieblingAlbum.id}"/>
				<c:param name="albumName" value="${sieblingAlbum.name}"/>
				<c:param name="coverArtSize" value="${model.sieblingCoverArtScheme.size}"/>
				<c:param name="showLink" value="true"/>
				<c:param name="showZoom" value="false"/>
				<c:param name="showChange" value="false"/>
				<c:param name="showCaption" value="true"/>
				<c:param name="captionLength" value="${model.sieblingCoverArtScheme.captionLength}"/>
				<c:param name="appearAfter" value="0"/>
			</c:import>
		</div> 
		</c:forEach>
	</td>
	
	<!--
    <td style="vertical-align:top;">
        <div style="padding:0 1em 0 1em;">
            <c:if test="${not empty model.ad}">
                <div class="detail" style="text-align:center">
                        ${model.ad}
                    <br/>
                    <br/>
                    <sub:url value="donate.view" var="donateUrl">
                        <sub:param name="path" value="${model.dir.path}"/>
                    </sub:url>
                    <fmt:message key="main.donate"><fmt:param value="${donateUrl}"/><fmt:param value="${model.brand}"/></fmt:message>
                </div>
            </c:if>
        </div>
    </td> -->
	
	</tr>
</table>
<!--
<div style="padding-bottom: 1em">
    <c:if test="${not empty model.previousAlbum}">
        <sub:url value="main.view" var="previousUrl">
            <sub:param name="id" value="${model.previousAlbum.id}"/>
        </sub:url>
        <div class="back" style="float:left;padding-right:10pt"><a href="${previousUrl}" title="${model.previousAlbum.name}">
            <str:truncateNicely upper="55">${fn:escapeXml(model.previousAlbum.name)}</str:truncateNicely>
        </a></div>
    </c:if>
    <c:if test="${not empty model.nextAlbum}">
        <sub:url value="main.view" var="nextUrl">
            <sub:param name="id" value="${model.nextAlbum.id}"/>
		</sub:url>
        <div class="forwardright" style="float:left"><a href="${nextUrl}" title="${model.nextAlbum.name}">
            <str:truncateNicely upper="55">${fn:escapeXml(model.nextAlbum.name)}</str:truncateNicely>
        </a></div>
    </c:if>
</div>-->

<div id="dialog-select-playlist" title="<fmt:message key="main.addtoplaylist.title"/>" style="display: none;">
    <p><fmt:message key="main.addtoplaylist.text"/></p>
    <div id="dialog-select-playlist-list"></div>
</div>

<!-- CONTENT -->
</div>

</body>

<c:if test="${model.customScrollbar}">
<script type="text/javascript">    

		(function($){
			$(window).load(function(){
				$("#content_2").mCustomScrollbar({
					set_width:false, /*optional element width: boolean, pixels, percentage*/
					set_height:false, /*optional element height: boolean, pixels, percentage*/
					horizontalScroll:false, /*scroll horizontally: boolean*/
					scrollInertia:850, /*scrolling inertia: integer (milliseconds)*/
					mouseWheel:true, /*mousewheel support: boolean*/
					mouseWheelPixels:"auto", /*mousewheel pixels amount: integer, "auto"*/
					autoDraggerLength:true, /*auto-adjust scrollbar dragger length: boolean*/
					autoHideScrollbar:false, /*auto-hide scrollbar when idle*/
					scrollButtons:{ /*scroll buttons*/
						enable:true, /*scroll buttons support: boolean*/
						scrollType:"continuous", /*scroll buttons scrolling type: "continuous", "pixels"*/
						scrollSpeed:"auto", /*scroll buttons continuous scrolling speed: integer, "auto"*/
						scrollAmount:150 /*scroll buttons pixels scroll amount: integer (pixels)*/
					},
					advanced:{
						updateOnBrowserResize:true, /*update scrollbars on browser resize (for layouts based on percentages): boolean*/
						updateOnContentResize:false, /*auto-update scrollbars on content resize (for dynamic content): boolean*/
						autoExpandHorizontalScroll:false, /*auto-expand width for horizontal scrolling: boolean*/
						autoScrollOnFocus:true, /*auto-scroll on focused elements: boolean*/
						normalizeMouseWheelDelta:false /*normalize mouse-wheel delta (-1/1)*/
					},
					contentTouchScroll:true, /*scrolling by touch-swipe content: boolean*/
					callbacks:{
						onScrollStart:function(){}, /*user custom callback function on scroll start event*/
						onScroll:function(){}, /*user custom callback function on scroll event*/
						onTotalScroll:function(){}, /*user custom callback function on scroll end reached event*/
						onTotalScrollBack:function(){}, /*user custom callback function on scroll begin reached event*/
						onTotalScrollOffset:0, /*scroll end reached offset: integer (pixels)*/
						onTotalScrollBackOffset:0, /*scroll begin reached offset: integer (pixels)*/
						whileScrolling:function(){} /*user custom callback function on scrolling event*/
					},
					theme:"light" /*"light", "dark", "light-2", "dark-2", "light-thick", "dark-thick", "light-thin", "dark-thin"*/
				});
			});
		})(jQuery);

$(".content_main").resize(function(e){
	$(".content_main").mCustomScrollbar("update");
});
</script>
</c:if>	
</html>
