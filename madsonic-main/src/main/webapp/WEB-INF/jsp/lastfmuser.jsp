<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>

    <c:if test="${model.customScrollbar}">
	<style type="text/css">
		.content_main{position:absolute; left:0px; top:0px; margin-left:10px; margin-top:5px; width:99%; height:95%; padding:0 0;overflow:auto;}
	</style>
	<script type="text/javascript" src="<c:url value="/script/jquery.mousewheel.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/script/jquery.mCustomScrollbar.js"/>"></script>
    </c:if>	
</head>
<body class="mainframe bgcolor1">
<!-- content block -->

<div id="content_2" class="content_main">
<!-- CONTENT -->

<h1>
	<img src="<spring:theme code="lastfmLargeImage"/>" alt="">
	LastFM Match
</h1>

<c:if test="${noLastFMUser}">
 no LastFM User config found
</c:if>	

<table width="80%" border="0" cellspacing="5" cellpadding="5">
  <tr>
    <th scope="col" align="left" style="vertical-align: top;">
		<h1>Loved Tracks</h1><br>
		<c:forEach items="${model.lovedTracks}" var="lovedTrack">

			<p class="dense">

			<sub:url value="main.view" var="mediaFileIdUrl">
				<sub:param name="id" value="${lovedTrack.id}"/>
			</sub:url>

			<c:if test="${lovedTrack.id == '0'}">
			<str:truncateNicely upper="30">${lovedTrack.artist}</str:truncateNicely>
			</c:if>	

			<c:if test="${lovedTrack.id != '0'}">
			<a target="main" href="${mediaFileIdUrl}"><str:truncateNicely upper="30">${lovedTrack.artist}</str:truncateNicely></a> 
			</c:if>	
			-
			<sub:url value="main.view" var="albumIdUrl">
				<sub:param name="id" value="${lovedTrack.trackNumber}"/>
			</sub:url>
			
 			<c:if test="${lovedTrack.trackNumber == '0'}">
			<str:truncateNicely upper="30">${lovedTrack.title}</str:truncateNicely>
			</c:if>	

			<c:if test="${lovedTrack.trackNumber != '0'}">

			<a target="main" href="${albumIdUrl}"><str:truncateNicely upper="30">${lovedTrack.title}</str:truncateNicely></a> 
			</c:if>	
			</p>

		</c:forEach>
		<!--
		<br><br>		
		<h1>Chart Tracks</h1><br>
			<c:forEach items="${model.chartTracks}" var="chartTrack">
			<p class="dense">
			<str:truncateNicely upper="35">${chartTrack}</str:truncateNicely><br>
			</p>
			</c:forEach>
		-->	
	</th>
    <th scope="col" align="left" style="vertical-align: top;">
		<h1>Top-Albums</h1><br>
		
			<c:forEach items="${model.topAlbums}" var="topAlbum">
			<p class="dense">

			<sub:url value="main.view" var="mediaFileIdUrl">
				<sub:param name="id" value="${topAlbum.id}"/>
			</sub:url>

			<c:if test="${topAlbum.id == '0'}">
			<str:truncateNicely upper="30">${topAlbum.artist}</str:truncateNicely>
			</c:if>	

			<c:if test="${topAlbum.id != '0'}">
			<a target="main" href="${mediaFileIdUrl}"><str:truncateNicely upper="30">${topAlbum.artist}</str:truncateNicely></a> 
			</c:if>	
			-
			<sub:url value="main.view" var="albumIdUrl">
				<sub:param name="id" value="${topAlbum.mediaFileId}"/>
			</sub:url>
			
 			<c:if test="${topAlbum.mediaFileId == '0'}">
			<str:truncateNicely upper="30">${topAlbum.name}</str:truncateNicely>
			</c:if>	

			<c:if test="${topAlbum.mediaFileId != '0'}">

			<a target="main" href="${albumIdUrl}"><str:truncateNicely upper="30">${topAlbum.name}</str:truncateNicely></a> 
			</c:if>	
			</p>
			</c:forEach>		
		
			<!--  		
			<c:forEach items="${model.topAlbums}" var="topAlbum">
			<str:truncateNicely upper="40">${topAlbum}</str:truncateNicely><br>
			</c:forEach> -->
	</th>
    <th scope="col" align="left" style="vertical-align: top;">
		<h1>Top-Artist</h1><br>
			<c:forEach items="${model.topArtists}" var="topArtist">
			<p class="dense">
		
			<sub:url value="main.view" var="mediaFileIdUrl">
				<sub:param name="id" value="${topArtist.id}"/>
			</sub:url>

			<c:if test="${topArtist.id == '0'}">
			<str:truncateNicely upper="30">${topArtist.name}</str:truncateNicely>
			</c:if>	

			<c:if test="${topArtist.id != '0'}">
			<a target="main" href="${mediaFileIdUrl}"><str:truncateNicely upper="30">${topArtist.name}</str:truncateNicely></a> 
			</c:if>	
			</p>
			
			</c:forEach>
		<br><br>	
		<h1>Top-Tags</h1><br>
			<c:forEach items="${model.topTags}" var="topTag">
			<p class="dense">
			<str:truncateNicely upper="30">${topTag}</str:truncateNicely><br>
			</p>
			</c:forEach>
	</th>
  </tr>
</table>

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
			scrollInertia:200, /*scrolling inertia: integer (milliseconds)*/
			scrollEasing:"easeOutCubic", /*scrolling easing: string*/
			mouseWheel:"auto", /*mousewheel support and velocity: boolean, "auto", integer*/
			autoDraggerLength:true, /*auto-adjust scrollbar dragger length: boolean*/
			scrollButtons:{ /*scroll buttons*/
				enable:true, /*scroll buttons support: boolean*/
				scrollType:"pixels", /*scroll buttons scrolling type: "continuous", "pixels"*/
				scrollSpeed:55, /*scroll buttons continuous scrolling speed: integer*/
				scrollAmount:250 /*scroll buttons pixels scroll amount: integer (pixels)*/
			},
			advanced:{
				updateOnBrowserResize:true, /*update scrollbars on browser resize (for layouts based on percentages): boolean*/
				updateOnContentResize:true, /*auto-update scrollbars on content resize (for dynamic content): boolean*/
				autoExpandHorizontalScroll:false /*auto expand width for horizontal scrolling: boolean*/
			},
			callbacks:{
				onScroll:function(){}, /*user custom callback function on scroll event*/
				onTotalScroll:function(){}, /*user custom callback function on bottom reached event*/
				onTotalScrollOffset:0 /*bottom reached offset: integer (pixels)*/
			}
		});
	});
})(jQuery);

$(".content_main").resize(function(e){
	$(".content_main").mCustomScrollbar("update");
});
</script>
</c:if>	
</html>