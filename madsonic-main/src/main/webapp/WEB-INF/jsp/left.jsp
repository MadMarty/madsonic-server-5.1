<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html><head>
	<%@ include file="head.jsp" %>
	<%@ include file="jquery.jsp" %> 

    <c:if test="${model.customScrollbar}">
	<style type="text/css">
		.content{position:absolute; left:0px; top:0px; margin:1; width:92%; height:99%; padding:0 10px; border-bottom:1px solid #333;overflow:auto;}
	</style>
	<script type="text/javascript" src="<c:url value="/script/jquery.mousewheel.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/script/jquery.mCustomScrollbar.js"/>"></script>
    </c:if>	

    <c:if test="${not model.customScrollbar}">
	<style type="text/css">
		.content{position:absolute; left:0px; top:0px; margin:1; margin-top:155px; width:90%; height:80%; padding:0 10px;}
	</style>
    <script type="text/javascript" src="<c:url value="/script/smooth-scroll.js"/>"></script>
    </c:if>	
	
    <c:if test="${model.customAccordion}">
	<script>
        $(window).ready(function() {
        var icons = {
                header: "ui-icon-triangle-1-e", 
                headerSelected: "ui-icon-triangle-1-s"
            };
        $("#accordion").accordion( {active: true, autoHeight: false, navigation: false, collapsible: true, icons: icons} );	
		$('.ui-accordion').bind('accordionchange', function(event, ui) {
			if(!ui.newHeader.length) { return; }
				ui.newHeader // jQuery object, activated header
				ui.oldHeader // jQuery object, previous header
				ui.newContent // jQuery object, activated content
				ui.oldContent // jQuery object, previous content
//				alert($(ui.newHeader).offset().top);
//				alert($(ui.oldHeader).offset().top);
				$('html, body, content_1').animate({scrollTop: $(ui.newHeader).offset().top -120}, 300);
				$('ui.newHeader').click();
				$('#ui.newHeader').click();
				$('newHeader').click();
		 }) ;

      });
    </script>
    </c:if>	

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
            $("#playlists").show();
        }
        function showStatistic() {
			$('#statistics').show('blind');
			$('#hideStatistics').show();
		 	$('#showStatistics').hide();
		}

        function hideStatistic() {
			$('#statistics').hide('blind');
			$('#hideStatistics').hide();
			$('#showStatistics').show();
		}

        function showShortcut() {
			$('#shortcuts').show('blind');
			$('#showShortcuts').hide();
			$('#hideShortcuts').show();
		}

        function hideShortcut() {
            $('#shortcuts').hide('blind');
			$('#hideShortcuts').hide();
			$('#showShortcuts').show();
			}

        function hideAllPlaylists() {
            $('#playlistOverflow').hide('blind');
            $("#playlists").hide('blind');
            $('#hideAllPlaylists').hide('blind');
            $('#showAllPlaylists').hide('blind');
            $('#showsomePlaylists').show('blind');
		}
		
        function showAllPlaylists() {
            $("#playlists").show();
            $('#playlistOverflow').show('blind');
            $('#hideAllPlaylists').show('blind');			
            $('#showAllPlaylists').hide('blind');
            $('#showsomePlaylists').hide('blind');
        }

        function showsomePlaylists() {
            $('#playlistOverflow').hide('blind');
            $("#playlists").show('blind');
            $('#hideAllPlaylists').show('blind');			
            $('#showAllPlaylists').show('blind');
            $('#showsomePlaylists').hide('blind');
        }
		
        function playlistCallback(playlists) {
            this.playlists = playlists;
			
            $("#playlists").empty();
            $("#playlistOverflow").empty();
			
            for (var i = 0; i < playlists.length; i++) {
                var playlist = playlists[i];
                var overflow = i > 9;
                $("<p class='dense'><a target='main' href='playlist.view?id=" +
                playlist.id + "'>" + playlist.name + "&nbsp;(" + playlist.fileCount + ")</a></p>").appendTo(overflow ? "#playlistOverflow" : "#playlists");
            }

            if (playlists.length > 9 && !$('#playlistOverflow').is(":visible")) {
			    $("#playlists").hide();
				$("#playlistOverflow").hide();			
                $('#showAllPlaylists').show();
				$('#showsomePlaylists').show();
            }
			
            if (playlists.length < 9 ) {
			    $("#playlists").hide();
				$("#playlistOverflow").hide();
				$('#showAllPlaylists').hide();
				$('#showsomePlaylists').show('blind');
            }			
        }
    </script>

    <script type="text/javascript">
        var previousQuery = "";
        var instantSearchTimeout;

        function triggerInstantSearch() {
            if (instantSearchTimeout) {
                window.clearTimeout(instantSearchTimeout);
            }
            instantSearchTimeout = window.setTimeout(executeInstantSearch, 300);
        }

        function executeInstantSearch() {
            var query = $("#query").val().trim();
            if (query.length > 1 && query != previousQuery) {
                previousQuery = query;
                document.searchForm.submit();
            }
        }
    </script>	
</head>
<body class="bgcolor2 leftframe" onload="init()">

<fmt:message key="top.search" var="search"/>

<c:if test="${not model.customScrollbar}">
<a name="top"></a>
</c:if>	
<!-- content block 
<c:if test="${fn:length(model.musicFolders) == 1}">
<div id="content_1" class="content">
</c:if>	-->
<c:if test="${fn:length(model.musicFolders) >= 0}">
<div id="content_1_2" class="content">
</c:if>	

	<!-- CONTENT -->
	<div class="bgcolor2" style="opacity: 1.0; clear: both; position: fixed; top: 0; right: 0; left: 0;
	padding: 0.25em 0.15em 0.15em 0.15em; max-width: 252px; border-bottom: 1px solid #A4A4A4;z-index:111000">
		<Table>
		<tr>
			<td style="padding-left:0.2em; padding-bottom:0em;">
				<form method="post" action="search.view" target="main" name="searchForm">
					<table><tr>
						<td><input type="text" name="query" id="query" size="28" placeholder="${search} ..." style="padding-left:8px;margin-right:4px;"  onclick="select();" onkeyup="triggerInstantSearch();"></td>
						<td><a href="javascript:document.searchForm.submit()"><img src="<spring:theme code="searchImage"/>" alt="${search}" title="${search}"></a></td>
					</tr></table>
				</form>
			</td>
		</tr>
		</table>


	<c:if test="${fn:length(model.musicFolders) >= 0}">
		<div style="padding-top:0,5em; width:182px;margin-left:6px;">
			<select name="musicFolderId" style="width:100%" onchange="refreshFrames( options[selectedIndex].value );">
				<option value="-1"><fmt:message key="left.allfolders"/></option>
				
				<c:if test="${model.MusicFolderEnabled eq true}">
				<option ${model.selectedMusicFolderId == -2 ? "selected" : ""} value="-2">All Music</option>			
				</c:if>
				<c:if test="${model.VideoFolderEnabled eq true}">
				<option ${model.selectedMusicFolderId == -3 ? "selected" : ""} value="-3">All Videos</option>
				</c:if>
				<c:if test="${model.MoviesFolderEnabled eq true}">
				<option ${model.selectedMusicFolderId == -4 ? "selected" : ""} value="-4">All Movies</option>
				</c:if>
				<c:if test="${model.SeriesFolderEnabled eq true}">
				<option ${model.selectedMusicFolderId == -5 ? "selected" : ""} value="-5">All Series</option>
				</c:if>
				<c:if test="${fn:length(model.musicFolders) > 0}">
				<option value="-1"></option>
				</c:if>
				<c:forEach items="${model.musicFolders}" var="musicFolder">
					<option ${model.selectedMusicFolder.id == musicFolder.id ? "selected" : ""} value="${musicFolder.id}">${musicFolder.name}</option>
				</c:forEach>
			</select>
		</div>
	</c:if>

    <script language="javascript" type="text/javascript">
	 function refreshFrames(sUrl) {
       location = "left.view?musicFolderId=" + sUrl;
       parent.frames.main.location.href="home.view?listType=${model.listType}";
	}
    </script>
	
	<div style="padding-top:0,5em; width:182px;margin-left:6px;margin-top:5px;">
		<select name="genreAll" style="width:100%" onchange="location='left.view?genre=' + escape(options[selectedIndex].value);" >>
			<option value=""><fmt:message key="left.allgenres"/></option>
			<!--<option ${"unknown genre" eq model.selectedGenre ? "selected" : ""} value="unknown genre">unknown genre</option>-->
			<c:forEach items="${model.allGenres}" var="genre">
				<option ${genre eq model.selectedGenre ? "selected" : ""} value="${genre}">${genre}</option>
			</c:forEach>
		</select>	
	</div>
		
	<c:choose>
		<c:when test="${model.scanning}">
	<!--<div style="padding-bottom:1.0em">
		<div class="warning"><fmt:message key="left.scanning"/></div>
		<div class="forward"><a href="left.view"><fmt:message key="common.refresh"/></a></div>
	</div>-->
		<div style="padding-bottom:0.35em; padding-left:0.7em;margin-top:10px;">
			<div class="forward"><a href="left.view?refresh=true"><fmt:message key="common.refresh"/></a></div>
		</div>
		</c:when>
		<c:otherwise>
			<div style="padding-bottom:0.35em; padding-left:0.7em;margin-top:10px;">
				<div class="forward"><a href="left.view?refresh=true"><fmt:message key="common.refresh"/></a></div>
			</div>
		</c:otherwise>
	</c:choose>		
	</div>
	
    <c:if test="${model.customScrollbar}">
	<a id="top"></a>
    </c:if>		
	
	<span id="showStatistics" style="display:inline"><div class="forward"><a href="javascript:noop()"onclick="showStatistic()">Show Statistics</a></div></span>
	<span id="hideStatistics" style="display:none"><div class="forward"><a href="javascript:noop()"onclick="hideStatistic()">Hide Statistics</a></div></span>

    <c:if test="${model.user.adminRole}">
	<span id="advancedStatistics" style="display:inline"><div class="forward"><a href="serverStatus.view?" target="main">Advanced Statistics</a></div></span>
	</c:if>                

	<c:if test="${model.statistics.songCount gt 0}">
	<div id="statistics" style="display:none;margin-top:10px;">
		<div class="detail" style="margin-left:5px;">
			<fmt:message key="left.statistics">
				<fmt:param value="${model.statistics.artistCount}"/>
				<fmt:param value="${model.statistics.albumArtistCount}"/>
				<fmt:param value="${model.statistics.albumCount}"/>
				<fmt:param value="${model.statistics.genreCount}"/>
				<fmt:param value="${model.statistics.songCount}"/>
				<fmt:param value="${model.statistics.videoCount}"/>
				<fmt:param value="${model.statistics.podcastCount}"/>
				<fmt:param value="${model.statistics.audiobookCount}"/>
				<fmt:param value="${model.bytes}"/>
				<fmt:param value="${model.hours}"/>
			</fmt:message>
		</div>
	</div>
	</c:if>


	<!--
	<c:if test="${fn:length(model.musicFolders) > 1}">
		<div style="padding-top:1em">
			<select name="musicFolderId" style="width:100%" onchange="location='left.view?musicFolderId=' + options[selectedIndex].value;" >
				<option value="-1"><fmt:message key="left.allfolders"/></option>
				<c:forEach items="${model.musicFolders}" var="musicFolder">
					<option ${model.selectedMusicFolder.id == musicFolder.id ? "selected" : ""} value="${musicFolder.id}">${musicFolder.name}</option>
				</c:forEach>
			</select>
		</div>
	</c:if>
	-->
	<c:if test="${not model.ShowShortcuts}">	
		<c:if test="${not empty model.shortcuts}">	
		<span id="showShortcuts" style="display:inline;">
		<div class="forward" style="margin-top:10px;" ><a href="javascript:noop()"onclick="showShortcut()">Show Shortcuts</a></div></span>

		<span id="hideShortcuts" style="display:none;">
		<div class="forward" style="margin-top:10px;" ><a href="javascript:noop()"onclick="hideShortcut()">Hide Shortcuts</a></div></span>
		</c:if>
		<div id="shortcuts" style="display:none;">

	</c:if>
	<c:if test="${model.ShowShortcuts}">	
	<div id="shortcuts">
	</c:if>
	
	<c:if test="${not empty model.shortcuts}">
		<h2 class="bgcolor1"><fmt:message key="left.shortcut"/></h2>
		<c:forEach items="${model.shortcuts}" var="shortcut">
			<p class="dense" style="padding-left:0.5em">
				<sub:url value="main.view" var="mainUrl">
					<sub:param name="id" value="${shortcut.id}"/>
				</sub:url>
				<a target="main" href="${mainUrl}">${shortcut.name}</a>
			</p>
		</c:forEach>
	</c:if>

	</div>

	<c:if test="${model.playlistEnabled}">	
		<h2 class="bgcolor1"><fmt:message key="left.playlists"/></h2> 
		<div id="playlistWrapper" style='padding-left:0.5em'>
			<div id="playlists"></div>
			<div id="playlistOverflow" style="display:none"></div>
	         	<div style="padding-top: 0.3em"/>
			<div id="showsomePlaylists" style="display: none"><a href="javascript:noop()" onclick="showsomePlaylists()"><fmt:message key="left.showsomeplaylists"/></a></div>
			<div id="showAllPlaylists" style="display: none"><a href="javascript:noop()" onclick="showAllPlaylists()"><fmt:message key="left.showallplaylists"/></a></div>
			<div id="hideAllPlaylists" style="display: none"><a href="javascript:noop()" onclick="hideAllPlaylists()"><fmt:message key="left.hideallplaylists"/></a></div>
			<div><a href="javascript:noop()" onclick="createEmptyPlaylist()"><fmt:message key="left.createplaylist"/></a></div>
			<div><a href="importPlaylist.view" target="main"><fmt:message key="left.importplaylist"/></a></div>
		</div>
	</c:if>

	<c:if test="${not empty model.radios}">
		<h2 class="bgcolor1"><fmt:message key="left.radio"/></h2>
		<c:forEach items="${model.radios}" var="radio">
			<p class="dense">
				<a target="hidden" href="${radio.streamUrl}">
					<img src="<spring:theme code="playImage"/>" alt="<fmt:message key="common.play"/>" title="<fmt:message key="common.play"/>"></a>
				<c:choose>
					<c:when test="${empty radio.homepageUrl}">
						${radio.name}
					</c:when>
					<c:otherwise>
						<a target="main" href="${radio.homepageUrl}">${radio.name}</a>
					</c:otherwise>
				</c:choose>
			</p>
		</c:forEach>
	</c:if>
	<br>
<c:if test="${model.customAccordion}">
 <div id="accordion"> 
</c:if>

	<c:forEach items="${model.indexedArtists}" var="entry">

<c:if test="${model.customAccordion}">
		<h3>
		<table width="100%" style="margin: 0 0 0 0;">
</c:if>
<c:if test="${not model.customAccordion}">
		<table class="bgcolor1" style="width:100%;padding:0;margin:1em 0 0 0;border:0">
</c:if>

			<tr style="padding:0;margin:0;border:0">
				<c:if test="${model.customScrollbar}">
					<th style="text-align:left;padding:0;margin:0;border:0">
					<c:choose>
						<c:when test="${entry.key.index eq '#'}">
							<a id="0"></a>
						</c:when>
						<c:when test="${entry.key.index eq '!'}">
							<a id="1"></a>
						</c:when>
						<c:otherwise>
							<a id="${entry.key.index}"></a> 
						</c:otherwise>
					</c:choose> 
					<h2 style="padding:0;margin:0;border:0">${entry.key.index}</h2>
					</th>
				</c:if>
		
				<c:if test="${not model.customScrollbar}">
					<th style="text-align:left;padding:0;margin:0;border:0"><a name="${entry.key.index}"></a>
					<h2 style="padding:0;margin:0;border:0">${entry.key.index}</h2>
					</th>
				</c:if>

				<th style="text-align:right;">
					<c:if test="${not model.customScrollbar}">
						<a href="#top"><img src="<spring:theme code="upImage"/>" alt=""></a>
					</c:if>	
					<c:if test="${model.customScrollbar}">
						<a href="#" class="back_to_top"><img src="<spring:theme code="upImage"/>" alt=""></a>
					</c:if>	
				</th>	
				</tr>
		</table>
		</h3>
<div> 
			<c:forEach items="${entry.value}" var="artist">
				<p class="dense" style="padding-left:0.5em">
					<span title="${artist.name}">
						<sub:url value="main.view" var="mainUrl">
							<c:choose>
								<c:when test="${model.organizeByFolderStructure}">
									<c:forEach items="${artist.mediaFiles}" var="mediaFile">
								<sub:param name="id" value="${mediaFile.id}"/>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<sub:param name="id" value="${artist.id}"/>
									</c:otherwise>
							</c:choose>
						</sub:url>
						<a target="main" href="${mainUrl}"><str:truncateNicely upper="${model.captionCutoff}">${artist.name}</str:truncateNicely></a>
					</span>
				</p>
			</c:forEach>
		</div>
	</c:forEach>

<c:if test="${model.customAccordion}">
</div>
</c:if>

	<div style="padding-top:1em"></div>

	<c:forEach items="${model.singleSongs}" var="song">
		<p class="dense" style="padding-left:0.5em">
			<span title="${song.title}">
				<c:import url="playAddDownload.jsp">
					<c:param name="id" value="${song.id}"/>
					<c:param name="playEnabled" value="${model.user.streamRole and not model.partyMode}"/>
					<c:param name="addEnabled" value="${model.user.streamRole}"/>
					<c:param name="downloadEnabled" value="${model.user.downloadRole and not model.partyMode}"/>
					<c:param name="video" value="${song.video and model.player.web}"/>
				</c:import>
				<str:truncateNicely upper="${model.captionCutoff}">${song.title}</str:truncateNicely>
			</span>
		</p>
	</c:forEach>

	<div style="height:5em"></div>

	<div class="bgcolor2" style="opacity: 1.0; clear: both; position: fixed; bottom: 0; right: 0; left: 0;
		padding: 0.25em 0.25em 0.25em 0.25em; border-top:1px solid #A4A4A4; max-width: 640px;">

		<c:if test="${not model.customScrollbar}">
		<a href="#top">TOP</a>
		</c:if>	

		<c:if test="${model.customScrollbar}">
		<a class="back_to_top" href="#">TOP</a>

		<div id="anchor_list" style="display: inline">
		<c:forEach items="${model.indexes}" var="index">
				<c:choose>
					<c:when test="${index.index eq '#'}">
						<a href="#" lnk="0">${index.index}</a>
					</c:when>
					<c:when test="${index.index eq '!'}">
						<a href="#" lnk="1">${index.index}</a>
					</c:when>
					<c:otherwise>
						<a href="#" lnk="${index.index}">${index.index}</a>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</div>	
		</c:if>	
			
		<c:if test="${not model.customScrollbar}">
			<c:forEach items="${model.indexes}" var="index">
				<a href="#${index.index}">${index.index}</a>
			</c:forEach>
		</c:if>	
			

	</div>
	<!-- CONTENT -->
</div>
 
<c:if test="${model.customScrollbar}">

	<script>
		(function($){
			$(window).load(function(){
				$("#content_1_2").mCustomScrollbar({
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
						scrollAmount:50 /*scroll buttons pixels scroll amount: integer (pixels)*/
					},
					advanced:{
						updateOnBrowserResize:true, /*update scrollbars on browser resize (for layouts based on percentages): boolean*/
						updateOnContentResize:true, /*auto-update scrollbars on content resize (for dynamic content): boolean*/
						autoExpandHorizontalScroll:false, /*auto-expand width for horizontal scrolling: boolean*/
						autoScrollOnFocus:false, /*auto-scroll on focused elements: boolean*/
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
			
				$("#content_1").mCustomScrollbar({
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
						scrollAmount:50 /*scroll buttons pixels scroll amount: integer (pixels)*/
					},
					advanced:{
						updateOnBrowserResize:true, /*update scrollbars on browser resize (for layouts based on percentages): boolean*/
						updateOnContentResize:true, /*auto-update scrollbars on content resize (for dynamic content): boolean*/
						autoExpandHorizontalScroll:false, /*auto-expand width for horizontal scrolling: boolean*/
						autoScrollOnFocus:false, /*auto-scroll on focused elements: boolean*/
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
	</script>

	<script>
	$('#anchor_list>a').click(function(){
		var thisPos = $("#"+ $(this).attr("lnk")).position(); /* get the position of the corresponding paragraph */
		$(".content").mCustomScrollbar("scrollTo", thisPos.top -140 );  /* scroll to */
	});
	</script>	
	
	<script>
	$(".back_to_top").click(function() {
		$(".content").mCustomScrollbar("scrollTo","top");  /* scroll all the content back to the top position */
	});
	</script>	
</c:if>	

<c:if test="${not model.customScrollbar}">
	<script>
	$(".back_to_top").click(function() {
		$(".content").scrollTop();
		});	
	</script>
</c:if>	

</body>
</html>