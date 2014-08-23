<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/interface/tagService.js"/>"></script>
</head>
<body class="mainframe bgcolor1">

<script type="text/javascript" language="javascript">
    var index = 0;
    var fileCount = ${fn:length(model.songs)};
    function setArtist() {
        var artist = dwr.util.getValue("artistAll");
        for (i = 0; i < fileCount; i++) {
            dwr.util.setValue("artist" + i, artist);
        }
    }
    function setAlbumArtist() {
        var albumartist = dwr.util.getValue("albumartistAll");
        for (i = 0; i < fileCount; i++) {
            dwr.util.setValue("albumArtist" + i, albumartist);
        }
    }
    function setAlbum() {
        var album = dwr.util.getValue("albumAll");
        for (i = 0; i < fileCount; i++) {
            dwr.util.setValue("album" + i, album);
        }
    }
    function setYear() {
        var year = dwr.util.getValue("yearAll");
        for (i = 0; i < fileCount; i++) {
            dwr.util.setValue("year" + i, year);
        }
    }
    function setGenre() {
        var genre = dwr.util.getValue("genreAll");
        for (i = 0; i < fileCount; i++) {
            dwr.util.setValue("genre" + i, genre);
        }
    }
    function setMood() {
        var mood = dwr.util.getValue("moodAll");
        for (i = 0; i < fileCount; i++) {
            dwr.util.setValue("mood" + i, mood);
        }
    }	
    function suggestTitle() {
        for (i = 0; i < fileCount; i++) {
            var title = dwr.util.getValue("suggestedTitle" + i);
            dwr.util.setValue("title" + i, title);
        }
    }
    function resetTitle() {
        for (i = 0; i < fileCount; i++) {
            var title = dwr.util.getValue("originalTitle" + i);
            dwr.util.setValue("title" + i, title);
        }
    }
    function suggestTrack() {
        for (i = 0; i < fileCount; i++) {
            var track = dwr.util.getValue("suggestedTrack" + i);
            dwr.util.setValue("track" + i, track);
        }
    }
    function resetTrack() {
        for (i = 0; i < fileCount; i++) {
            var track = dwr.util.getValue("originalTrack" + i);
            dwr.util.setValue("track" + i, track);
        }
    }
    function updateTags() {
        document.getElementById("save").disabled = true;
        index = 0;
        dwr.util.setValue("errors", "");
        for (i = 0; i < fileCount; i++) {
            dwr.util.setValue("status" + i, "");
        }
        updateNextTag();
    }
    function updateNextTag() {
        var id = dwr.util.getValue("id" + index);
        var artist = dwr.util.getValue("artist" + index);
        var albumartist = dwr.util.getValue("albumArtist" + index); 
        var track = dwr.util.getValue("track" + index);
        var album = dwr.util.getValue("album" + index);
        var title = dwr.util.getValue("title" + index);
        var year = dwr.util.getValue("year" + index);
        var mood = dwr.util.getValue("mood" + index);
        var genre = dwr.util.getValue("genre" + index);
        var rank = dwr.util.getValue("rank" + index);

        dwr.util.setValue("status" + index, "<fmt:message key="edittags.working"/>");
        tagService.setTags(id, track, artist, albumartist, album, title, year, mood, genre, rank, setTagsCallback);
    }
    function setTagsCallback(result) {
        var message;
        if (result == "SKIPPED") {
            message = "<fmt:message key="edittags.skipped"/>";
        } else if (result == "UPDATED") {
            message = "<b><fmt:message key="edittags.updated"/></b>";
        } else {
            message = "<div class='warning'><fmt:message key="edittags.error"/></div>"
            var errors = dwr.util.getValue("errors");
            errors += "<br>" + result + "<br>";
            dwr.util.setValue("errors", errors, { escapeHtml:false });
        }
        dwr.util.setValue("status" + index, message, { escapeHtml:false });
        index++;
        if (index < fileCount) {
            updateNextTag();
        } else {
            document.getElementById("save").disabled = false;
        }
    }
</script>

<h1><fmt:message key="edittags.title"/></h1>
<sub:url value="main.view" var="backUrl"><sub:param name="id" value="${model.id}"/></sub:url>
<div class="back"><a href="${backUrl}"><fmt:message key="common.back"/></a></div>

<table class="ruleTable indent">
    <tr>
        <th class="ruleTableHeader"><fmt:message key="edittags.track"/></th>
        <th class="ruleTableHeader"><fmt:message key="edittags.songtitle"/></th>
        <th class="ruleTableHeader"><fmt:message key="edittags.artist"/>&nbsp;[<a href="javascript:setArtist()"><fmt:message key="edittags.set"/></a>]</th>
        <th class="ruleTableHeader"><fmt:message key="edittags.albumartist"/>&nbsp;[<a href="javascript:setAlbumArtist()"><fmt:message key="edittags.set"/></a>]</th>
        <th class="ruleTableHeader"><fmt:message key="edittags.album"/>&nbsp;[<a href="javascript:setAlbum()"><fmt:message key="edittags.set"/></a>]</th>
        <th class="ruleTableHeader"><fmt:message key="edittags.year"/>&nbsp;[<a href="javascript:setYear()"><fmt:message key="edittags.set"/></a>]</th>
        <th class="ruleTableHeader"><fmt:message key="edittags.mood"/>&nbsp;[<a href="javascript:setMood()"><fmt:message key="edittags.set"/></a>]</th>
        <th class="ruleTableHeader"><fmt:message key="edittags.genre"/>&nbsp;[<a href="javascript:setGenre()"><fmt:message key="edittags.set"/></a>]</th>
		<th class="ruleTableHeader" style="white-space: nowrap">Rank</th>
        <th class="ruleTableHeader" width="60"><fmt:message key="edittags.status"/></th>
    </tr>
    <tr>
        <th class="ruleTableHeader"><a href="javascript:suggestTrack()"><fmt:message key="edittags.suggest.short"/></a> |
            <a href="javascript:resetTrack()"><fmt:message key="edittags.reset.short"/></a></th>
        <th class="ruleTableHeader"><a href="javascript:suggestTitle()"><fmt:message key="edittags.suggest"/></a> |
            <a href="javascript:resetTitle()"><fmt:message key="edittags.reset"/></a></th>
        <th class="ruleTableHeader" style="white-space: nowrap"><input type="text" name="artistAll" size="25" onKeyPress="dwr.util.onReturn(event, setArtist)" value="${model.defaultArtist}"/></th>
        <th class="ruleTableHeader" style="white-space: nowrap"><input type="text" name="albumartistAll" size="25" onKeyPress="dwr.util.onReturn(event, setAlbumArtist)" value="${model.defaultAlbumArtist}"/></th>
        <th class="ruleTableHeader" style="white-space: nowrap"><input type="text" name="albumAll" size="30" onKeyPress="dwr.util.onReturn(event, setAlbum)" value="${model.defaultAlbum}"/></th>
        <th class="ruleTableHeader" style="white-space: nowrap"><input type="text" name="yearAll" size="5" onKeyPress="dwr.util.onReturn(event, setYear)" value="${model.defaultYear}"/></th>
        <th class="ruleTableHeader" style="white-space: nowrap">
            <select name="moodAll" style="width:8em">
                <option value=""/>
                <c:forEach items="${model.allMoods}" var="mood">
                    <option ${mood eq model.defaultMood ? "selected" : ""} value="${mood}">${mood}</option>
                </c:forEach>
            </select>
        </th>
        <th class="ruleTableHeader" style="white-space: nowrap">
            <select name="genreAll" style="width:8em">
                <option value=""/>
                <c:forEach items="${model.allGenres}" var="genre">
                    <option ${genre eq model.defaultGenre ? "selected" : ""} value="${genre}">${genre}</option>
                </c:forEach>
            </select>
        </th>
        <th class="ruleTableHeader"/>
        <th class="ruleTableHeader"/>
    </tr>

    <c:forEach items="${model.songs}" var="song" varStatus="loopStatus">
        <tr>
          <td colspan="10" align="left" valign="middle" class="ruleTableCell" title="${song.fileName}"><strong>${song.fileName}</strong></td>
        </tr>
        <tr>
<!--        <str:truncateNicely lower="45" upper="45" var="fileName">${song.fileName}</str:truncateNicely> -->

            <input type="hidden" name="id${loopStatus.count - 1}" value="${song.id}"/>
            <input type="hidden" name="suggestedTitle${loopStatus.count - 1}" value="${song.suggestedTitle}"/>
            <input type="hidden" name="originalTitle${loopStatus.count - 1}" value="${song.title}"/>
            <input type="hidden" name="suggestedTrack${loopStatus.count - 1}" value="${song.suggestedTrack}"/>
            <input type="hidden" name="originalTrack${loopStatus.count - 1}" value="${song.track}"/>
            <td nowrap="nowrap" class="ruleTableCell"><input type="text" size="1" name="track${loopStatus.count - 1}" value="${song.track}"/></td>
            <td class="ruleTableCell"><input type="text" size="45" name="title${loopStatus.count - 1}" value="${song.title}"/></td>
            <td class="ruleTableCell"><input type="text" size="25" name="artist${loopStatus.count - 1}" value="${song.artist}"/></td>
            <td class="ruleTableCell"><input type="text" size="25" name="albumArtist${loopStatus.count - 1}" value="${song.albumArtist}"/></td>
            <td class="ruleTableCell"><input type="text" size="30" name="album${loopStatus.count - 1}" value="${song.album}"/></td>
            <td class="ruleTableCell"><input type="text" size="5"  name="year${loopStatus.count - 1}" value="${song.year}"/></td>
            <td class="ruleTableCell"><input type="text" name="mood${loopStatus.count - 1}" value="${song.mood}" style="width:8em"/></td>
            <td class="ruleTableCell"><input type="text" name="genre${loopStatus.count - 1}" value="${song.genre}" style="width:8em"/></td>
            <td class="ruleTableCell"><input type="text" size="3" name="rank${loopStatus.count - 1}" value="${song.rank}" style="width:3em"/></td>
            <td class="ruleTableCell"><div id="status${loopStatus.count - 1}"/></td>
        </tr>
    </c:forEach>

</table>

<p><input type="submit" id="save" value="<fmt:message key="common.save"/>" onclick="javascript:updateTags()"/></p>
<div class="warning" id="errors"/>
</body></html>