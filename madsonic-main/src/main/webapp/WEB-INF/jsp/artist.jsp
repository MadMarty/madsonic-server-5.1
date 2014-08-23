<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html><head>
	<%@ include file="head.jsp" %>
	<%@ include file="jquery.jsp" %>

	<link href="<c:url value="/style/shadow.css"/>" rel="stylesheet">
	<link href="<c:url value="/style/artist/style-artist.css"/>" rel="stylesheet">
	
    <c:if test="${not model.customScrollbar}">
	<link href="<c:url value="/style/customScrollbar.css"/>" rel="stylesheet">
    <script type="text/javascript" src="<c:url value="/script/smooth-scroll.js"/>"></script>
    </c:if>	

    <c:if test="${model.customScrollbar}">
	<link href="<c:url value="/style/customScrollbar.css"/>" rel="stylesheet">
	<script type="text/javascript" src="<c:url value="/script/jquery.mousewheel.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/script/jquery.mCustomScrollbar.js"/>"></script>
    </c:if>	

	<script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script> 
	<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>

	<script type="text/javascript">
		function playTagRadio() {
		var genres = new Array();
		var e = document.getElementsByTagName("span");
		for (var i = 0; i < e.length; i++) {
			if (e[i].className == "on2") {
				genres.push(e[i].firstChild.data);
			}
		}
		// var num = document.getElementsByName("GenreRadioPlayCount")[0].selectedIndex;
		// var playcount = document.getElementsByName("GenreRadioPlayCount")[0].options[num].text;

		parent.playQueue.onPlayGenreRadio(genres, 10);
	}
	</script>
	
	
	<script type="text/javascript">
		function albumlink(ob) {
			window.location.href = $(ob).attr("alt");
		}	
	</script>
	
	<script type="text/javascript" language="javascript">
        function init() {
            dwr.engine.setErrorHandler(null);
        }
		
		function changeClass(elem, className1,className2) {
		elem.className = (elem.className == className1)?className2:className1;
		}

		function showSearch(albumId) {
			var SearchStack = "#showSearch_" + albumId;
			var ShowSearchToogle = "#showSearchToogle_" + albumId;
			var HideSearchToogle = "#hideSearchToogle_" + albumId;
			
			$( SearchStack ).show('blind');
			$( ShowSearchToogle ).hide();
			$( HideSearchToogle ).show();
		}

		function hideSearch(albumId) {
			var SearchStack = "#showSearch_" + albumId;
			var ShowSearchToogle = "#showSearchToogle_" + albumId;
			var HideSearchToogle = "#hideSearchToogle_" + albumId;
			
			$( SearchStack ).hide('blind');
			$( ShowSearchToogle ).show();
			$( HideSearchToogle ).hide();
		}
		
		function showAlbums(albumId) {
            var AlbumStack = "#album_stack_" + albumId;
			var ShowAlbumButton = "#showAlbums_" + albumId;
			var HideAlbumButton = "#hideAlbums_" + albumId;

            $( AlbumStack ).show('blind');
            $( ShowAlbumButton ).hide();
            $( HideAlbumButton ).show();
		}

		function hideAlbums(albumId) {
            var AlbumStack = "#album_stack_" + albumId;
			var ShowAlbumButton = "#showAlbums_" + albumId;
			var HideAlbumButton = "#hideAlbums_" + albumId;

			$( AlbumStack ).hide('blind');
            $( HideAlbumButton ).hide();
            $( ShowAlbumButton ).show();
		}		
    </script>

	<script type="text/javascript">

		function albumlink(ob) {
			window.location.href = $(ob).attr("alt");
		}	
	
	$(document).ready(function() { 
	$(".image_stack").delegate('img', 'mouseenter', function() {//when user hover mouse on image with div id=stackphotos 
			if ($(this).hasClass('stackphotos')) {//
			// the class stackphotos is not really defined in css , it is only assigned to each images in the photo stack to trigger the mouseover effect on  these photos only 
				
				var $parent = $(this).parent();
				$parent.find('img#photo1').addClass('rotate4');
				$parent.find('img#photo2').addClass('rotate3');//add class rotate1,rotate2,rotate3 to each image so that 
				$parent.find('img#photo3').addClass('rotate1');//it rotates to the correct degree in the correct direction ( 15 degrees one to the left , one to the right ! )
				$parent.find('img#photo2').css("left","-30px"); // reposition the first and last image 
				$parent.find('img#photo3').css("left","65px");
			}
		})
		.delegate('img', 'mouseleave', function() {// when user removes cursor from the image stack
				$('img#photo1').removeClass('rotate4');// remove the css class that was previously added to make it to its original position
				$('img#photo2').removeClass('rotate3');
				$('img#photo3').removeClass('rotate1');
				$('img#photo2').css("left","");// remove the css property 'left' value from the dom
				$('img#photo3').css("left","");
		});;
	});
	
		$(document).ready(function() { 
	$(".image_stack2").delegate('img', 'mouseenter', function() {//when user hover mouse on image with div id=stackphotos 
			if ($(this).hasClass('stackphotos')) {//
			// the class stackphotos is not really defined in css , it is only assigned to each images in the photo stack to trigger the mouseover effect on  these photos only 
				
				var $parent = $(this).parent();
				$parent.find('img#photo1').addClass('rotate4');
				$parent.find('img#photo2').addClass('rotate3');//add class rotate1,rotate2,rotate3 to each image so that 
				$parent.find('img#photo3').addClass('rotate1');//it rotates to the correct degree in the correct direction ( 15 degrees one to the left , one to the right ! )
				$parent.find('img#photo2').css("left","-30px"); // reposition the first and last image 
				$parent.find('img#photo3').css("left","65px");
			}
		})
		.delegate('img', 'mouseleave', function() {// when user removes cursor from the image stack
				$('img#photo1').removeClass('rotate4');// remove the css class that was previously added to make it to its original position
				$('img#photo2').removeClass('rotate3');
				$('img#photo3').removeClass('rotate1');
				$('img#photo2').css("left","");// remove the css property 'left' value from the dom
				$('img#photo3').css("left","");
		});;
	});
	</script>

	<link href="<c:url value="/style/carousel2.css"/>" rel="stylesheet">
	<script type="text/javascript" src="<c:url value="/script/jquery-migrate-1.2.1.js"/>"></script> 
    <script type="text/javascript" src="<c:url value="/script/jquery.event.drag-2.2.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/script/cloud-carousel.1.0.7.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/script/cloud-carousel-artist.js"/>"></script>
	
</head>

<body class="bgcolor1 mainframe" onload="init()">

<c:choose>
  <c:when test="${model.showAlbum}">

<c:if test="${not model.customScrollbar}">
<a name="top"></a>
</c:if>	

<!-- content block -->
<div id="content_4" class="content_artist1" style="opacity:1.0;z-index: 100;">

	<!-- CONTENT -->
	<div class="bgcolor2 artistindex" style="opacity: 1.0; clear: both; position: fixed; top: 0; right: 0; left: 0px;
	padding: 0.15em 0.15em 0.15em 0.15em; height: 45px; z-index: 1000">
	
		<c:if test="${model.customScrollbar}">
		<a id="top"></a>
		</c:if>	
		<div style="padding-bottom:1.0em; padding-left: 10px; font-size: 14pt;">
		<c:if test="${model.customScrollbar}">
			<div id="anchor_list"><h2>
			<c:forEach items="${model.indexes}" var="index">
				<a href="?name=${index.index}" lnk="${index.index}">${index.index}</a>
			</c:forEach>
			</h2>
			</div>
		</c:if>	
		<c:if test="${not model.customScrollbar}">
			<div id="anchor_list"><h2>		
			<c:forEach items="${model.indexes}" var="index">
				<a href="?name=${index.index}" accesskey="${index.index}">${index.index}</a>
			</c:forEach>
			</h2>
			</div>			
		</c:if>	
		</div>
	</div>
	
	<div style="margin-top: 60px; padding: 0.15em 0.15em 0.15em 0.15em; z-index: 100">
	
	<form method="get" action="artist.view" target="main" name="artistForm">
		<table><tr>
			<td><input type="text" name="name" id="name" size="24" value="${search}" onclick="select();"></td>
			<td><a href="javascript:document.artistForm.submit()"><img src="<spring:theme code="searchImage"/>" alt="${search}" title="${search}"></a></td>
		</tr></table>
	</form>

	<c:if test="${model.indexedArtistHub == null}">
	 <i>Select Artist Index.</i>
	</c:if>	

	<c:if test="${model.lastFMArtist == null}">
	 <h2>
		<img src="<spring:theme code="warningImage"/>" alt=""/>
		No Last.FM-Data found! 
	</h2>
	<br>
	No Data exist or the Server should be re-synchronized with Last.FM Update Function.
	</c:if>	

	<c:if test="${model.lastFMArtist ne null}">
	
	<c:forEach items="${model.indexedArtistHub}" var="entry">

    <table width="740px" border="0" cellspacing="0" cellpadding="1" style="border-collapse:collapse;margin-top:15px;">
		<c:forEach items="${entry.value}" var="artist" varStatus="status">
		
        <tr><td colspan="2">
		<!-- 01 -->
		<sub:url value="main.view" var="mainUrl">
			<c:forEach items="${artist.mediaFiles}" var="mediaFile">
			<sub:param name="id" value="${mediaFile.id}"/>
			</c:forEach>
		</sub:url>
		<h1 style="margin: 0 0 5px;">
		<a target="main" href="${mainUrl}"><str:truncateNicely upper="${model.captionCutoff}">${artist.name}</str:truncateNicely></a> 
		</td></tr>

		<tr><td colspan="2">
		<h2>Artist TopTags</h2>
		</td></tr>			
		
		<tr><td colspan="2">
		<c:forEach items="${model.lastFMArtistTopTags}" var="TopTags">
		<span class="off2" onclick='changeClass(this,"on2","off2");'>${TopTags}</span>
		</c:forEach>
		<form><input type="button" value="Play Tag Radio!" onClick="playTagRadio();"></form>
		</td></tr>

		<tr><td colspan="2">
		<span class="image_stack2" style="margin-left:550px;margin-top:-60px;"> 
				<c:forEach items="${artist.albums}" var="album" varStatus="loopStatus">
				<c:choose>
				  <c:when test="${loopStatus.count <= 3}">
					<c:import url="artistAlbums.jsp">
						<c:param name="albumId" value="${album.mediaFileId}"/>
						<c:param name="albumName" value="${album.name}"/>
						<c:param name="coverArtSize" value="50"/>
						<c:param name="showLink" value="true"/>
						<c:param name="showZoom" value="false"/>
						<c:param name="showChange" value="false"/>
						<c:param name="showCaption" value="false"/>
						<c:param name="appearAfter" value="0"/>
						<c:param name="count" value="${loopStatus.count}"/>
					</c:import>
				  </c:when>
				  <c:otherwise>
				  </c:otherwise>
				</c:choose>
				</c:forEach>
		</span>		
		</td></tr>			
		
		
		<tr><td>
		
		<c:if test="${model.lastFMArtist.coverart1 ne null and model.lastFMArtist.coverart2 ne null}">
		<script type="text/javascript" src="<c:url value="/script/jquery.mousewheel.js"/>"></script>
			
		<div id="preloader">
			<div id="preloaderText">
				<span id="currentProcess"></span> 
				<span id="persent"></span>
				<div id="stopLoading">cancel</div>
				<div id="startLoading">resume</div>
			</div>
		</div>
		<div id="resizable">
			<div id = "carousel1" > 
	
				<img class = "cloudcarousel" src="${model.lastFMArtist.coverart1}" width="110">
				<img class = "cloudcarousel" src="${model.lastFMArtist.coverart2}" width="110">
				<img class = "cloudcarousel" src="${model.lastFMArtist.coverart3}" width="110">
				<img class = "cloudcarousel" src="${model.lastFMArtist.coverart4}" width="110">
				<img class = "cloudcarousel" src="${model.lastFMArtist.coverart5}" width="110">
			</div>
		</div>		
		</c:if>	

		</td>
		<td class="detailFont" style="padding:15px 0 15px;">
		${model.lastFMArtist.summary}
		</td></tr>		
		<tr><td colspan="2">
		<h2>Artist Similar</h2>
		</td></tr>	
		<tr><td colspan="2">		
		<c:forEach items="${model.lastFMArtistSimilar}" var="similar">
		<span class="off2" onclick='changeClass(this,"on2","off2");'><a href="s.jsp?&q=${similar}" target="main" lnk="${similar}">${similar}</a></span>
		</c:forEach>
		<!--
		<c:forEach items="${model.lastFMArtistSimilar}" var="similar">
		<a href="s.jsp?&q=${similar}" target="main" lnk="${similar}">${similar}</a><br>
		</c:forEach>
		-->
		</td></tr>	
		</table>
		
		<table>		

	<tr>			
		<td colspan="2">
		<h2>Artist TopAlbum</h2>
		</td>
	</tr>	
	<!--
		<tr>	
		<td colspan="2">
		<c:forEach items="${model.lastFMArtistTopAlbums}" var="TopAlbum">
		<span class="off2" onclick='changeClass(this,"on2","off2");'>${TopAlbum}</span>
		</c:forEach>		
		</td>
		</tr>	
	-->	
		<tr><td colspan="2">
		<c:forEach items="${model.lastFMArtistTopAlbumX}" var="TopAlbums">
		<span class="off2" onclick='changeClass(this,"on2","off2");'>
		
		<c:choose>
		  <c:when test="${TopAlbums.mediaFileId eq 0}">
			${TopAlbums.albumName}
		  </c:when>
		  <c:otherwise>
			<a href="main.view?&id=${TopAlbums.mediaFileId}" target="main" lnk="${TopAlbums.mediaFileId}">${TopAlbums.albumName}
            <img src="icons/default/Folder.png" width="12" style="margin-top:-2px;margin-right:-2px" alt="" title="<fmt:message key="artist.open"/>">
			</a>

		  </c:otherwise>
		</c:choose>
		</span>

		</c:forEach>
		</td></tr>  
		
		<tr><td width="500px">
		<h2>Artist Album</h2>
		</td>
		</tr>			
		<tr><td style="vertical-align: text-top;">
		<c:forEach items="${artist.albums}" var="album" varStatus="loopStatus">
		
				<c:import url="playAddDownload.jsp">
				<c:param name="id" value="${album.mediaFileId}"/>
				<c:param name="video" value="false"/>
				<c:param name="playEnabled" value="true"/>
				<c:param name="playAddEnabled" value="false"/>
				<c:param name="addEnabled" value="true"/>
				<c:param name="downloadEnabled" value="false"/>
				<c:param name="artist" value="${album.name}"/>
				<c:param name="title" value="${album.name}"/>
				<c:param name="starEnabled" value="false"/>
				<c:param name="starred" value="${not empty child.starredDate}"/>
				<c:param name="asTable" value="false"/>
				<c:param name="YoutubeEnabled" value="false"/>
				</c:import>

				<!-- <span class="detailmini"><c:if test="${album.playCount < 100}">0</c:if><c:if test="${album.playCount < 10}">0</c:if>${album.playCount}</span> |  -->
				<span class="detailmini"><c:if test="${album.songCount < 100}">0</c:if><c:if test="${album.songCount < 10}">0</c:if>${album.songCount}</span>
				<img src="icons/default/note.png" width="10" height="10" title="Tracks" style="margin-right: 5px;"/>

				<div style="display: inline-table;">
				
				<c:choose>
				  <c:when test="${album.playCount < 100}">
					<img src="icons/default/counter.png" width="${(album.playCount)/2}" height="8" title="Played ${album.playCount} times"/>
				  </c:when>
				  <c:otherwise>
					<img src="icons/default/counter.png" width="50" height="8" title="Played ${album.playCount} times"/>
					<img src="icons/default/plus.png" width="8" height="8" style="position:relative; left:-10px;"/>
				  </c:otherwise>
				</c:choose>
				
				<c:choose>
				  <c:when test="${album.playCount < 100}">
					<img src="icons/default/counter2.png" width="${(100-album.playCount)/2}" height="8" title="Played ${album.playCount} times"/>
					<img src="icons/default/trans.png" width="8" height="8" style="position:relative; left:-10px;"/>
				  </c:when>
				  <c:otherwise>
					<img src="icons/default/counter2.png" width="0" height="8" title="Played ${album.playCount} times"/>
				  </c:otherwise>
				</c:choose>
				</div>

			<c:if test="${album.year == 0}"><span class="detail">[????]</span></c:if><c:if test="${album.year > 1}"><span class="detailcolor">[${album.year}]</span></c:if>
				
			<sub:url value="main.view" var="mediaFileIdUrl">
				<sub:param name="id" value="${album.mediaFileId}"/>
			</sub:url>
			<a target="main" href="${mediaFileIdUrl}"><str:truncateNicely upper="${model.captionCutoff}">${album.name}</str:truncateNicely></a> 
			<c:if test="${album.genre ne null}"> - 
			<span class="detailcolor">(${album.genre})</span>
			</c:if>
			<br>
			</c:forEach>
		</td>
	</tr>		
		</c:forEach>
</c:forEach>
  </tr>
  
</table>
</c:if>	  

</div>	  
	
  </c:when>
  <c:otherwise>
  <!-- ---------------------------- -->

  
  <c:if test="${not model.customScrollbar}">
<a name="top"></a>
</c:if>	

<!-- content block -->
<div id="content_4" class="content_artist2">

	<!-- CONTENT -->

	<div class="bgcolor2 artistindex" style="opacity: 1.0; clear: both; position: fixed; top: 0; right: 0; left: 0px;
	padding: 0.15em 0.15em 0.15em 0.15em; height: 45px; z-index: 1000">

		<c:if test="${model.customScrollbar}">
		<a id="top"></a>
		</c:if>	
		<div style="padding-bottom:1.0em; padding-left: 10px; font-size: 14pt;">
		<c:if test="${model.customScrollbar}">
			<h2>
			<div id="anchor_list">
			<c:forEach items="${model.indexes}" var="index">
				<a href="?name=${index.index}" lnk="${index.index}">${index.index}</a>
			</c:forEach>
			</div>
			</h2>
		</c:if>	
		<c:if test="${not model.customScrollbar}">
			<h2>
			<div id="anchor_list">
			<c:forEach items="${model.indexes}" var="index">
				<a href="?name=${index.index}" accesskey="${index.index}">${index.index}</a>
			</c:forEach>
			</div>
			</h2>
		</c:if>	
		</div>
	</div>

	<div style="margin-top: 60px; padding: 0.20em 0.15em 0.15em 0.15em; z-index: 100">
	
	<form method="get" action="artist.view" target="main" name="artistForm">
		<table><tr>
			<td><input type="text" name="name" id="name" size="24" value="${search}" onclick="select();"></td>
			<td><a href="javascript:document.artistForm.submit()"><img src="<spring:theme code="searchImage"/>" alt="${search}" title="${search}"></a></td>
		</tr></table>
	</form>

	<c:if test="${model.indexedArtistHub == null }">
	 <i>Select Artist Index.</i>
	</c:if>	

	<c:forEach items="${model.indexedArtistHub}" var="entry">

    <table width="60%" border="0" cellspacing="0" cellpadding="4" style="border-collapse:collapse;white-space:nowrap;margin-top:10px;">
      <tr>
		<c:forEach items="${entry.value}" var="artist" varStatus="status">
        <td style="vertical-align: top;">

        <table width="450px" class="artisthub" border="0" cellspacing="0" cellpadding="2" style="white-space:nowrap; padding-left: 10px;padding-top: 10px;">
        <tr><td colspan="4">
		<!-- 02 -->
		<sub:url value="main.view" var="mainUrl">
			<c:forEach items="${artist.mediaFiles}" var="mediaFile">
			<sub:param name="id" value="${mediaFile.id}"/>
			</c:forEach>
		</sub:url>
		
		<h1 style="margin: 0 0 5px;">
		<a target="main" href="${mainUrl}"><str:truncateNicely upper="${model.captionCutoff}">${artist.name}</str:truncateNicely></a> 
		</h1>
		<!-- 02 -->
		</td>
        </tr>
        <tr style="width:100%;">

		<!-- 01 -->
		<td style="vertical-align:top;width:90px;">

		<c:import url="coverArt.jsp">
		<c:param name="albumId" value="${artist.mediaFiles[0].id}"/>
		<c:param name="albumName" value="${mediaFile.name}"/>
		<c:param name="coverArtSize" value="90"/>
		<c:param name="typArtist" value="true"/>
		<c:param name="showLink" value="true"/>
		<c:param name="showZoom" value="false"/>
		<c:param name="showChange" value="false"/>
		<c:param name="showCaption" value="true"/>
		<c:param name="appearAfter" value="0"/>
		</c:import>
		<sub:url value="/changeCoverArt.view" var="changeCoverArtUrl">
			<sub:param name="id" value="${artist.mediaFiles[0].id}"/>
			<sub:param name="isArtist" value="true"/>
		</sub:url>
		<div style="opacity: 0.70; clear: both; position: relative; top: -35px; left: 68px; 
					padding: 0.25em 0.15em 0.15em 0.15em; max-width: 16px;">
			<a class="detailmini" href="${changeCoverArtUrl}" title="Change Artist Cover" alt="Change"><img src="<spring:theme code="editArtistImage"/>"</a>
		</div>
		<!-- 01 -->
		  </td>
          <td width="190" style="vertical-align:top;">

		<!-- 03-07 -->
		<table width="192px" border="0" cellspacing="0" cellpadding="1">
		
	<!--	<td class="detailcolor" style="width: 60px;">albums</td> -->
		
		<tr><td class="detailcolor" style="width: 55px;">albums</td> <td class="detailcolordark">${artist.albumCount}</td></tr>
		<tr><td class="detailcolor">songs</td> <td class="detailcolordark">${artist.songCount}</td></tr>
		<tr><td class="detailcolor">played</td> <td class="detailcolordark">${artist.playCount}</td></tr>
		<tr><td class="detailcolor">year</td> <td class="detailcolordark">${artist.albums[0].year}</td></tr>
		<tr><td class="detailcolor">genre</td> <td class="detailmini">${artist.albums[0].genre}</td></tr>
		<tr><td class="detailcolor" style="padding-top:6px">
		
		
		<div style="opacity: 0.70; clear: both; position: relative; top: 0px; left: 0px; padding: 0.25em 0.15em 0.15em 0.15em; max-width: 16px;">
		<c:choose>
		  <c:when test="${model.showAlbum}">
			<span id="showAlbums_${artist.albums[0].mediaFileId}" style="display:none">
			<a href="javascript:noop()"onclick="showAlbums(${artist.albums[0].mediaFileId})">
			<img src="<spring:theme code="albumsImage"/>" title="Show other albums from Artist"></a></span>
			<span id="hideAlbums_${artist.albums[0].mediaFileId}" style="display:inline">
			<a href="javascript:noop()" onclick="hideAlbums(${artist.albums[0].mediaFileId})">
			<img src="<spring:theme code="albumsImage"/>" title="Hide albums"></a></span>
		  </c:when>
		  <c:otherwise>
			<span id="showAlbums_${artist.albums[0].mediaFileId}" style="display:inline">
			<a href="javascript:noop()"onclick="showAlbums(${artist.albums[0].mediaFileId})">
			<img src="<spring:theme code="albumsImage"/>" title="Show other albums from Artist"></a></span>
			<span id="hideAlbums_${artist.albums[0].mediaFileId}" style="display:none">
			<a href="javascript:noop()" onclick="hideAlbums(${artist.albums[0].mediaFileId})">
			<img src="<spring:theme code="albumsImage"/>" title="Hide albums"></a></span>
		  </c:otherwise>
		</c:choose>
		</div>
		
		<div style="opacity: 0.70; clear: both; position: relative; top: -27px; left: 20px; padding: 0.25em 0.15em 0.15em 0.15em; max-width: 16px;">

			<span id="showSearchToogle_${artist.albums[0].mediaFileId}" style="display:inline">		
			<a href="javascript:noop()"onclick="showSearch(${artist.albums[0].mediaFileId})">
			<img src="icons/default/search2.png" title="Show Search Tools"></a>
			</span>
			
			<span id="hideSearchToogle_${artist.albums[0].mediaFileId}" style="display:none">		
			<a href="javascript:noop()"onclick="hideSearch(${artist.albums[0].mediaFileId})">
			<img src="icons/default/search2.png" title="Hide Search Tools"></a>
			</span>
		</div>
		</td>
		
		<td class="detailcolor" style="padding-top:10px;vertical-align:top;">
			
		<span id="showSearch_${artist.albums[0].mediaFileId}" style="display:none">		
		
			<sub:url value="http://www.google.com/search" var="googleUrl" encoding="UTF-8">
                <sub:param name="q" value="\"${artist.albums[0].artist}\""/>
            </sub:url>
            <sub:url value="http://en.wikipedia.org/wiki/Special:Search" var="wikipediaUrl" encoding="UTF-8">
                <sub:param name="search" value="\"${artist.albums[0].artist}\""/>
                <sub:param name="go" value="Go"/>
            </sub:url>
            <sub:url value="http://www.discogs.com/search" var="discogsUrl" encoding="UTF-8">
                <sub:param name="q" value="${artist.albums[0].artist}"/>
            </sub:url>
            <sub:url value="http://www.last.fm/search" var="lastFmUrl" encoding="UTF-8">
                <sub:param name="q" value="\"${artist.albums[0].artist}\""/>
                <sub:param name="type" value="artist"/>
            </sub:url>
			<sub:url value="http://musicbrainz.org/search" var="musicbrainzUrl" encoding="UTF-8">
                <sub:param name="query" value="${artist.albums[0].artist}"/>
                <sub:param name="type" value="artist"/>
            </sub:url>
            <sub:url value="http://www.youtube.com/results" var="YoutubeUrl" encoding="UTF-8">
                <sub:param name="search_query" value="${artist.albums[0].artist}"/>
            </sub:url>
            <a target="_blank" href="${googleUrl}"><img src="<spring:theme code="googleImage"/>" title="Search with Google"></a> 
            <a target="_blank" href="${wikipediaUrl}"><img src="<spring:theme code="wikipediaImage"/>" title="Search with Wikipedia"></a> 
            <a target="_blank" href="${discogsUrl}"><img src="<spring:theme code="discosgsImage"/>" title="Search with discosgs"></a>
			<a target="_blank" href="${YoutubeUrl}"><img src="<spring:theme code="youtubeImage"/>" title="Search with youtube"></a>
			<a target="_blank" href="${musicbrainzUrl}"><img src="<spring:theme code="musicbrainzImage"/>" title="Search with MusicBrainz"></a> 
            <a target="_blank" href="${lastFmUrl}"><img src="<spring:theme code="lastfmImage"/>" title="Search with LastFM"></a> 

			</span>

		</td>
		</table>
		<!-- 03-07 -->

		</td>
		<td>

		<!-- 08 -->
		<td width="180" style="vertical-align:top;padding-top:20px;">
		<span class="image_stack" style="margin-top:-150px;"> 
				<c:forEach items="${artist.albums}" var="album" varStatus="loopStatus">
				<c:choose>
				  <c:when test="${loopStatus.count <= 3}">
					<c:import url="artistAlbums.jsp">
						<c:param name="albumId" value="${album.mediaFileId}"/>
						<c:param name="albumName" value="${album.name}"/>
						<c:param name="coverArtSize" value="50"/>
						<c:param name="showLink" value="true"/>
						<c:param name="showZoom" value="false"/>
						<c:param name="showChange" value="false"/>
						<c:param name="showCaption" value="false"/>
						<c:param name="appearAfter" value="0"/>
						<c:param name="count" value="${loopStatus.count}"/>
					</c:import>
				  </c:when>
				  <c:otherwise>
				  </c:otherwise>
				</c:choose>
				</c:forEach>
		</span>
		<!-- 08 -->
		</td>
        </tr>
        <tr>
          <td colspan="4">
			<!-- 10 -->

				<c:choose>
				  <c:when test="${model.showAlbum}">
					<div id="album_stack_${artist.albums[0].mediaFileId}" style="margin-top:0px;margin-bottom:15px;display:block">
				  </c:when>
				  <c:otherwise>
					<div id="album_stack_${artist.albums[0].mediaFileId}" style="margin-top:0px;margin-bottom:15px;display:none">
				  </c:otherwise>
				</c:choose>

			<c:forEach items="${artist.albums}" var="album" varStatus="loopStatus">

				<span class="controls">
				<c:import url="playAddDownload.jsp">
				<c:param name="id" value="${album.mediaFileId}"/>
				<c:param name="video" value="false"/>
				<c:param name="playEnabled" value="true"/>
				<c:param name="playAddEnabled" value="false"/>
				<c:param name="addEnabled" value="true"/>
				<c:param name="downloadEnabled" value="false"/>
				<c:param name="artist" value="${album.name}"/>
				<c:param name="title" value="${album.name}"/>
				<c:param name="starEnabled" value="false"/>
				<c:param name="starred" value=""/>
				<c:param name="asTable" value="false"/>
				<c:param name="YoutubeEnabled" value="false"/>
				</c:import>				
				<span>
				
				<!-- <span class="detailmini"><c:if test="${album.playCount < 100}">0</c:if><c:if test="${album.playCount < 10}">0</c:if>${album.playCount}</span> |  -->
				<span class="detailmini"><c:if test="${album.songCount < 100}">0</c:if><c:if test="${album.songCount < 10}">0</c:if>${album.songCount}</span>
				<img src="icons/default/note.png" width="10" height="10" title="Tracks" style="margin-right: 5px;"/>

				<div style="display: inline-table;">
				
				<c:choose>
				  <c:when test="${album.playCount < 100}">
					<img src="icons/default/counter.png" width="${(album.playCount)/2}" height="8" title="Played ${album.playCount} times"/>
				  </c:when>
				  <c:otherwise>
					<img src="icons/default/counter.png" width="50" height="8" title="Played ${album.playCount} times"/>
					<img src="icons/default/plus.png" width="8" height="8" style="position:relative; left:-10px;"/>
				  </c:otherwise>
				</c:choose>
				
				<c:choose>
				  <c:when test="${album.playCount < 100}">
					<img src="icons/default/counter2.png" width="${(100-album.playCount)/2}" height="8" title="Played ${album.playCount} times"/>
					<img src="icons/default/trans.png" width="8" height="8" style="position:relative; left:-10px;"/>
				  </c:when>
				  <c:otherwise>
					<img src="icons/default/counter2.png" width="0" height="8" title="Played ${album.playCount} times"/>
				  </c:otherwise>
				</c:choose>
				</div>

				<c:if test="${album.year == 0}"><span class="detail">[0000]</span></c:if><c:if test="${album.year > 1}"><span class="detailcolor">[${album.year}]</span></c:if>
				
				<sub:url value="main.view" var="mediaFileIdUrl">
					<sub:param name="id" value="${album.mediaFileId}"/>
				</sub:url>
				<a target="main" href="${mediaFileIdUrl}"><str:truncateNicely upper="${model.captionCutoff}">${album.name}</str:truncateNicely></a> 
				<c:if test="${album.genre ne null}"> - 
				<span class="detailcolor">(${album.genre})</span>
				</c:if>
				<br>
				</c:forEach>
			</div>
			<td>
			</td>
			<!-- 10 -->
		</td>
	
        </tr>
        </table>
    </td>
		<c:choose>
			<c:when test="${status.count % 2 == 1}">
			</c:when>
			<c:otherwise>
			</tr>
			</c:otherwise>
		</c:choose>
		</c:forEach>
</c:forEach>
  </tr>
</table>
</div>

  </c:otherwise>
</c:choose>

</body>
	<c:if test="${model.customScrollbar}">
	<script>
		(function($){
			$(window).load(function(){
				$("#content_4").mCustomScrollbar({
					set_width:false, /*optional element width: boolean, pixels, percentage*/
					set_height:false, /*optional element height: boolean, pixels, percentage*/
					horizontalScroll:false, /*scroll horizontally: boolean*/
					scrollInertia:450, /*scrolling inertia: integer (milliseconds)*/
					mouseWheel:true, /*mousewheel support: boolean*/
					mouseWheelPixels:"auto", /*mousewheel pixels amount: integer, "auto"*/
					autoDraggerLength:true, /*auto-adjust scrollbar dragger length: boolean*/
					autoHideScrollbar:false, /*auto-hide scrollbar when idle*/
					scrollButtons:{ /*scroll buttons*/
						enable:true, /*scroll buttons support: boolean*/
						scrollType:"continuous", /*scroll buttons scrolling type: "continuous", "pixels"*/
						scrollSpeed:"auto", /*scroll buttons continuous scrolling speed: integer, "auto"*/
						scrollAmount:250 /*scroll buttons pixels scroll amount: integer (pixels)*/
					},
					advanced:{
						updateOnBrowserResize:true, /*update scrollbars on browser resize (for layouts based on percentages): boolean*/
						updateOnContentResize:true, /*auto-update scrollbars on content resize (for dynamic content): boolean*/
						autoExpandHorizontalScroll:false, /*auto-expand width for horizontal scrolling: boolean*/
						autoScrollOnFocus:false, /*auto-scroll on focused elements: boolean*/
						normalizeMouseWheelDelta:true /*normalize mouse-wheel delta (-1/1)*/
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
	</c:if>
</html>