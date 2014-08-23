<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html><head>
    <%@ include file="head.jsp" %>
	<%@ include file="jquery.jsp" %>
	
    <link href="<c:url value="/style/shadow.css"/>" rel="stylesheet">
	
	<c:if test="${model.customScrollbar}">
		<link href="<c:url value="/style/customScrollbar.css"/>" rel="stylesheet">
		<script type="text/javascript" src="<c:url value="/script/jquery.mousewheel.min.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/script/jquery.mCustomScrollbar.js"/>"></script>
	</c:if>	
	
    <c:if test="${model.listType eq 'random'}">
        <meta http-equiv="refresh" content="15000">
    </c:if>
	
    <c:if test="${model.listType eq 'hot'}">
	<link href="<c:url value="/style/carousel.css"/>" rel="stylesheet">
	<script type="text/javascript" src="<c:url value="/script/jquery-migrate-1.2.1.js"/>"></script> 
    <script type="text/javascript" src="<c:url value="/script/jquery.event.drag-2.2.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/script/cloud-carousel.1.0.7.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/script/cloud-carousel-home.js"/>"></script>
    </c:if>

</head>
<body class="mainframe bgcolor1">
<!-- content block -->

	<div id="content_2" class="content_home">
	<!-- CONTENT -->

	<c:if test="${not empty model.welcomeTitle}">
		<h1>
			<img src="<spring:theme code="homeImage"/>" alt="">
			${model.welcomeTitle}
		</h1>
	</c:if>

	<c:if test="${not empty model.welcomeSubtitle}">
		<h2>${model.welcomeSubtitle}</h2>
	</c:if>

	<h2>
	<!-- random newest hot allArtist starredArtist starred tip highest frequent recent decade genre alphabetical top new -->
		<c:forTokens items="${model.homeHeader}" delims=" " var="cat" varStatus="loopStatus">

		<c:choose>
			<c:when test="${loopStatus.count > 1 and  (loopStatus.count - 1) % 8 != 0}">&nbsp;<img src="<spring:theme code="sepImage"/>" alt="">&nbsp;</c:when>
			<c:otherwise></h2><h2></c:otherwise>
		</c:choose>
	
			<sub:url var="url" value="home.view">
				<sub:param name="listType" value="${cat}"/>
				<sub:param name="listRows" value="${model.listRows}"/>
				<sub:param name="listColumns" value="${model.listColumns}"/>
			</sub:url>
			
			<c:choose>
				<c:when test="${model.listType eq cat}">

				<span class="headerSelected">
					<c:if test="${cat eq 'starred' || cat eq 'starredArtist'}">
						<img width="16" height="16" style="padding-bottom: 3px;" src="<spring:theme code="ratingOnImage"/>" >
					</c:if>
				<fmt:message key="home.${cat}.title"/></span>
				</c:when>
				<c:otherwise>
					<c:if test="${cat eq 'starred' || cat eq 'starredArtist'}">
						<img width="16" height="16" style="padding-bottom: 3px;" src="<spring:theme code="ratingOnImage"/>" >
					</c:if>
					<a href="${url}"><fmt:message key="home.${cat}.title"/></a>
				</c:otherwise>
			</c:choose>
		</c:forTokens>
	</h2>
	<div style="margin-top: 20px;">
	<c:if test="${model.isIndexBeingCreated}">
		<p class="warning" style="margin-top: 10px;"><fmt:message key="home.scan"/></p>
	</c:if>
	<h2><fmt:message key="home.${model.listType}.text"/><c:if test="${model.listType eq 'hot'}">: <p id="carousel1-title">Title</p></c:if></h2>

	<table width="100%">
		<tr>
			<td style="vertical-align:top;">
	<c:choose>
	<c:when test="${model.listType eq 'tip'}">
		<table>
			<c:forEach items="${model.albums}" var="album" varStatus="loopStatus">
				<c:if test='${loopStatus.count % model.listColumns == 1}'>
					<tr>
				</c:if>
				<td style="vertical-align:top">
					<table>
						<tr><td>
								<c:import url="coverArt.jsp">
									<c:param name="albumId" value="${album.id}"/>
									<c:param name="albumName" value="${album.albumSetName}"/>
									<c:param name="coverArtSize" value="130"/>
									<c:param name="coverArtPath" value="${album.coverArtPath}"/>
									<c:param name="showLink" value="true"/>
									<c:param name="showZoom" value="false"/>
									<c:param name="showChange" value="false"/>
									<c:param name="appearAfter" value="${loopStatus.count * 20}"/>
									<c:param name="showPlayAlbum" value="false"/>
									<c:param name="showAddAlbum" value="false"/>
								</c:import>

								<div class="detailmini">
								<c:if test="${not empty album.playCount}">
								<div class="detailcolordark">
									<fmt:message key="home.playcount"><fmt:param value="${album.playCount}"/></fmt:message>
								</div>
								</c:if>
								<c:if test="${not empty album.lastPlayed}">
								<div class="detailcolordark">
									<fmt:formatDate value="${album.lastPlayed}" dateStyle="short" var="lastPlayedDate"/>
									<fmt:message key="home.lastplayed"><fmt:param value="${lastPlayedDate}"/></fmt:message>
								</div>
								</c:if>
								<c:if test="${not empty album.created}">
								<div class="detailcolordark">
									<fmt:formatDate value="${album.created}" dateStyle="short" var="creationDate"/>
									<fmt:message key="home.created"><fmt:param value="${creationDate}"/></fmt:message>
								</div>
								</c:if>
								<c:if test="${not empty album.rating}">
									<c:import url="rating.jsp">
										<c:param name="readonly" value="true"/>
										<c:param name="rating" value="${album.rating}"/>
									</c:import>
								</c:if>
								</div>

								<sub:url value="main.view" var="parentId">
									<sub:param name="id" value="${album.parentId}"/>
								</sub:url>
									
							<c:choose>
								<c:when test="${empty album.artist and empty album.albumTitle}">
								<div class="detail"><fmt:message key="common.unknown"/></div>
								</c:when>
								<c:otherwise>
								<a href="${parentId}">Show Artist</a>
									<div class="detailcolor"><em><str:truncateNicely upper="19"><a href="${parentId}">${album.artist}</a></str:truncateNicely></em></div>
									<div class="detail"><str:truncateNicely upper="21">${album.albumTitle}</str:truncateNicely></div>
								</c:otherwise>
							</c:choose>

						</td></tr>
					</table>
				</td>
			<c:if test="${loopStatus.count % model.listColumns == 0}">
				</tr>
			</c:if>
			</c:forEach>
			<tr>
			<c:if test="${not empty model.albums}">
			<td><div class="forward"><a href="home.view?listType=tip&amp;listRows=${model.listRows}&amp;listColumns=${model.listColumns}"><fmt:message key="common.more"/></a></div></td>
			</c:if>
			</tr>
		</table>

	</c:when>
	</c:choose>	
	
	<c:choose>
	<c:when test="${model.listType eq 'hot'}">

		<script type="text/javascript" src="<c:url value="/script/jquery.mousewheel.js"/>"></script>
			
		<img id="left-but" src="<spring:theme code="buttonLeftImage"/>" title=""/>
		<img id="right-but" src="<spring:theme code="buttonRightImage"/>" title=""/>
		
		<div id="preloader">
			<div id="preloaderText">
				<span id="currentProcess"></span> 
				<span id="persent"></span>
				<div id="stopLoading">cancel</div>
				<div id="startLoading">resume Downloads</div>
			</div>
		</div>
		<div id="resizable">
			<div id = "carousel1" > 
			<c:forEach items="${model.albums}" var="album" varStatus="loopStatus">
				<c:import url="carousel.jsp">
					<c:param name="albumId" value="${album.id}"/>			
					<c:param name="albumPath" value="${album.path}"/>
					<c:param name="albumName" value="${album.albumSetName}"/>
					<c:param name="albumArtist" value="${album.artist}"/>
					<c:param name="albumYear" value="${album.albumYear}"/>				
					<c:param name="coverArtSize" value="140"/>
					<c:param name="coverArtSize" value="${model.coverArtSize}"/>
					<c:param name="showLink" value="true"/>
					<c:param name="showPlayAlbum" value="false"/>					
					<c:param name="showAddAlbum" value="false"/>
				</c:import>				
			</c:forEach>
			</div>
		</div>
		<div id = "nextcontrols" > 
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<c:choose>
						<c:when test="${model.listType eq 'random'}">
						</c:when>
						<c:otherwise>
							<sub:url value="home.view" var="previousUrl">
								<sub:param name="listType" value="${model.listType}"/>
								<sub:param name="listRows" value="${model.listRows}"/>
								<sub:param name="listColumns" value="${model.listColumns}"/>
								<sub:param name="listOffset" value="${model.listOffset - model.listSize}"/>
								<sub:param name="genre" value="${model.genre}"/>
								<sub:param name="decade" value="${model.decade}"/>
							</sub:url>
							<sub:url value="home.view" var="nextUrl">
								<sub:param name="listType" value="${model.listType}"/>
								<sub:param name="listRows" value="${model.listRows}"/>
								<sub:param name="listColumns" value="${model.listColumns}"/>
								<sub:param name="listOffset" value="${model.listOffset + model.listSize}"/>
								<sub:param name="genre" value="${model.genre}"/>
								<sub:param name="decade" value="${model.decade}"/>								
							</sub:url>
								<td width="33%"></td>
								<td width="80"><fmt:message key="home.albums"><fmt:param value="${model.listOffset + 1}"/><fmt:param value="${model.listOffset + model.listSize}"/></fmt:message></td>
								<td width="33%"></td>
							</c:otherwise> 
						</c:choose>
					</tr>
			</table>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="33%"><div class="back"><a href="${previousUrl}"><fmt:message key="common.previous"/></a></div></td>
					<td width="100" style="padding-up:1.5em">
						<select name="listSize" onchange="location='home.view?listType=${model.listType}&amp;listOffset=${model.listOffset}&amp;listSize=' + options[selectedIndex].value;">
							<c:forTokens items="5 10 15 20 25 30 35" delims=" " var="size">
								<option ${size eq model.listSize ? "selected" : ""} value="${size}"><fmt:message key="home.listsize"><fmt:param value="${size}"/></fmt:message></option>
							</c:forTokens>
						</select>
					</td>
					<td width="33%"><div class="forwardright"><a href="${nextUrl}"><fmt:message key="common.next"/></a></div></td>
				</tr>
			</table>
		</div>

	</c:when>
	<c:when test="${model.listType eq 'users'}">
		<table>
			<tr>
				<th><fmt:message key="home.chart.total"/></th>
				<th><fmt:message key="home.chart.stream"/></th>
			</tr>
			<tr>
				<td><img src="<c:url value="/userChart.view"><c:param name="type" value="total"/></c:url>" alt=""></td>
				<td><img src="<c:url value="/userChart.view"><c:param name="type" value="stream"/></c:url>" alt=""></td>
			</tr>
			<tr>
				<th><fmt:message key="home.chart.download"/></th>
				<th><fmt:message key="home.chart.upload"/></th>
			</tr>
			<tr>
				<td><img src="<c:url value="/userChart.view"><c:param name="type" value="download"/></c:url>" alt=""></td>
				<td><img src="<c:url value="/userChart.view"><c:param name="type" value="upload"/></c:url>" alt=""></td>
			</tr>
	</table>
	</c:when>
	<c:otherwise>
		<c:if test="${model.listType ne 'tip'}">
		<table>
			<c:forEach items="${model.albums}" var="album" varStatus="loopStatus">
				<c:if test='${loopStatus.count % model.listColumns == 1}'>
					<tr>
				</c:if>
				<td style="vertical-align:top">
					<table>
						<tr><td>
						
								<c:import url="coverArt.jsp">
									<c:param name="albumId" value="${album.id}"/>
									<c:param name="albumName" value="${album.albumSetName}"/>
									<c:param name="artistName" value="${album.artist}"/>
									<c:param name="coverArtSize" value="118"/>
									<c:param name="coverArtPath" value="${album.coverArtPath}"/>
									<c:param name="showLink" value="true"/>
									<c:param name="showZoom" value="false"/>
									<c:param name="showChange" value="false"/>
									<c:param name="appearAfter" value="${loopStatus.count * 20}"/>
									<c:param name="showPlayAlbum" value="true"/>									
									<c:param name="showAddAlbum" value="true"/>
									<c:param name="showTopTrack" value="${model.showTopTrack}"/>
								</c:import>

								<div class="detailmini">
								<c:if test="${not empty album.playCount}">
								<div class="detailcolordark">
									<fmt:message key="home.playcount"><fmt:param value="${album.playCount}"/></fmt:message>
								</div>
								</c:if>
								<c:if test="${not empty album.lastPlayed}">
								<div class="detailcolordark">
									<fmt:formatDate value="${album.lastPlayed}" dateStyle="short" var="lastPlayedDate"/>
									<fmt:message key="home.lastplayed"><fmt:param value="${lastPlayedDate}"/></fmt:message>
								</div>
								</c:if>
								<c:if test="${not empty album.created}">
								<div class="detailcolordark">
									<fmt:formatDate value="${album.created}" dateStyle="short" var="creationDate"/>
									<fmt:message key="home.created"><fmt:param value="${creationDate}"/></fmt:message>
								</div>
				                                    <c:if test="${not empty album.year}">
				                                        ${album.year}
				                                    </c:if>
								</c:if>
								<c:if test="${not empty album.rating}">
									<c:import url="rating.jsp">
										<c:param name="readonly" value="true"/>
										<c:param name="rating" value="${album.rating}"/>
									</c:import>
								</c:if>
								</div>

							<c:choose>
								<c:when test="${empty album.artist and empty album.albumTitle}">
								<div class="detail"><fmt:message key="common.unknown"/></div>
								</c:when>
								<c:otherwise>

								<sub:url value="main.view" var="parent">
								<sub:param name="id" value="${album.parentId}"/>
								</sub:url>

									<div class="detailcolor"><a href="${parent}"><str:truncateNicely lower="18" upper="18">${album.artist}</str:truncateNicely></a></div>
									
										<c:choose>
											<c:when test="${fn:startsWith(album.albumTitle,'[')}">
												<div class="detail"><str:truncateNicely upper="15">${fn:split(album.albumTitle,']')[1]}</str:truncateNicely></div>
											</c:when>
											<c:otherwise>
												<div class="detail"><str:truncateNicely upper="15">${album.albumTitle}</str:truncateNicely></div>
											</c:otherwise>
										</c:choose>
									
								</c:otherwise>
							</c:choose>

						</td></tr>
					</table>
				</td>
				<c:if test="${loopStatus.count % model.listColumns == 0}">
					</tr>
				</c:if>
			</c:forEach>
		</table>

	<table>
		<tr>
		   <c:if test="${model.listType eq 'decade'}">
				<td style="padding-left: 0em">
					<fmt:message key="home.decade.text"/>
				</td>
				<td>
					<select name="decade" onchange="location='home.view?listType=${model.listType}&amp;listRows=${model.listRows}&amp;listColumns=${model.listColumns}&amp;decade=' + options[selectedIndex].value">
						<c:forEach items="${model.decades}" var="decade">
							<option
								${decade eq model.decade ? "selected" : ""} value="${decade}">${decade}</option>
						</c:forEach>
					</select>
				</td>
			</c:if>
			<c:if test="${model.listType eq 'genre'}">
				<td style="padding-left: 0em">
					<fmt:message key="home.genre.text"/>
				</td>
				<td>
					<select name="genre" onchange="location='home.view?listType=${model.listType}&amp;listRows=${model.listRows}&amp;listColumns=${model.listColumns}&amp;genre=' + options[selectedIndex].value">
						<c:forEach items="${model.genres}" var="genre">
							<option ${genre eq model.genre ? "selected" : ""} value="${genre}">${genre}</option>
						</c:forEach>
					</select>
				</td>
			</c:if>
			<td high=20>
			</td>
			
			<td>
			<select name="listRows" id="listRows" class="inputWithIcon vcenter" onchange="location='home.view?listType=${model.listType}&amp;listColumns=${model.listColumns}&amp;listRows=' + options[selectedIndex].value;">
				<c:forTokens items="1 2 3 4 5 6 7 8 9 10" delims=" " var="listrows">
					<option ${listrows eq model.listRows ? "selected" : ""} value="${listrows}"><fmt:message key="home.listrows"><fmt:param value="${listrows}"/></fmt:message>${listrows gt 1 ? pluralizer : ""}</option>
				</c:forTokens>
			</select>
					
			<select name="listColumns" id="listColumns" class="inputWithIcon vcenter" onChange="location='home.view?listType=${model.listType}&amp;listRows=${model.listRows}&amp;listColumns=' + options[selectedIndex].value;">
				<c:forEach begin="1" end="10" var="listcolumns">
					<c:if test="${listcolumns gt 10}">
						<c:set var="listcolumns" value="${listcolumns}"/>
					</c:if>
					<option ${listcolumns eq model.listColumns ? "selected" : ""} value="${listcolumns}"><fmt:message key="home.listcolumns"><fmt:param value="${listcolumns}"/></fmt:message>${listcolumns gt 1 ? pluralizer : ""}</option>
				</c:forEach>
			</select> 
			<td>
			<c:choose>
			<c:when test="${model.listType eq 'random'}">
				<td><div class="forwardright"><a href="home.view?listType=random&amp;listRows=${model.listRows}&amp;listColumns=${model.listColumns}"><fmt:message key="common.more"/></a></div></td>
			</c:when>
			
			<c:otherwise>
				<sub:url value="home.view" var="previousUrl">
					<sub:param name="listType" value="${model.listType}"/>
					<sub:param name="listRows" value="${model.listRows}"/>
					<sub:param name="listColumns" value="${model.listColumns}"/>
					<sub:param name="listOffset" value="${model.listOffset - model.listSize}"/>
					<sub:param name="genre" value="${model.genre}"/>
					<sub:param name="decade" value="${model.decade}"/>
				</sub:url>
				<sub:url value="home.view" var="nextUrl">
					<sub:param name="listType" value="${model.listType}"/>
					<sub:param name="listRows" value="${model.listRows}"/>
					<sub:param name="listColumns" value="${model.listColumns}"/>
					<sub:param name="listOffset" value="${model.listOffset + model.listSize}"/>
					<sub:param name="genre" value="${model.genre}"/>
					<sub:param name="decade" value="${model.decade}"/>					
				</sub:url>

				<c:if test="${model.listOffset gt 0}">
					<td style="padding-right:1.5em"><div class="back"><a href="${previousUrl}"><fmt:message key="common.previous"/></a></div></td>
                </c:if>

				<c:if test="${model.listType eq 'allArtist' || model.listType eq 'starredArtist' }"> 
					<td style="padding-right:1.5em"><fmt:message key="home.artists"><fmt:param value="${model.listOffset + 1}"/><fmt:param value="${model.listOffset + model.listSize}"/></fmt:message></td>
				</c:if>
	
				<c:if test="${model.listType ne 'allArtist' && model.listType ne 'starredArtist' }">
					<td style="padding-right:1.5em"><fmt:message key="home.albums"><fmt:param value="${model.listOffset + 1}"/><fmt:param value="${model.listOffset + model.listSize}"/></fmt:message></td>
				</c:if>
				
                <c:if test="${fn:length(model.albums) eq model.listSize}">
					<td><div class="forwardright"><a href="${nextUrl}"><fmt:message key="common.next"/></a></div></td>
                </c:if>
			</c:otherwise>
		</c:choose>

			</tr>
		</table>

	</c:if>
		
	</c:otherwise>
	</c:choose>
			</td>
				<c:if test="${not empty model.welcomeMessage}">
					<td style="vertical-align:top;width:20em">
						<div style="padding:0 1em 0 1em;border-left:1px solid #<spring:theme code="detailColor"/>">
							<sub:wiki text="${model.welcomeMessage}"/>
						</div>
					</td>
				</c:if>
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
					scrollInertia:650, /*scrolling inertia: integer (milliseconds)*/
					mouseWheel:true, /*mousewheel support: boolean*/
					mouseWheelPixels:350, /*mousewheel pixels amount: integer, "auto"*/
					autoDraggerLength:true, /*auto-adjust scrollbar dragger length: boolean*/
					autoHideScrollbar:false, /*auto-hide scrollbar when idle*/
					scrollButtons:{ /*scroll buttons*/
						enable:true, /*scroll buttons support: boolean*/
						scrollType:"continuous", /*scroll buttons scrolling type: "continuous", "pixels"*/
						scrollSpeed:"auto", /*scroll buttons continuous scrolling speed: integer, "auto"*/
						scrollAmount:350 /*scroll buttons pixels scroll amount: integer (pixels)*/
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

$(".content_home").resize(function(e){
	$(".content_home").mCustomScrollbar("update");
});
</script>
</c:if>	

<script type="text/javascript">        
function updateListOption(opt, val) {
	var newURI = 'home.view?listType='
	switch (opt) {
		case ("type"):    newURI += val + '&listRows=${model.listRows}&listColumns=${model.listColumns}&listOffset=${model.listOffset}'; break;
		case ("rows"):    newURI += '${model.listType}&listRows=' + val + '&listColumns=${model.listColumns}&listOffset=${model.listOffset}'; break;
		case ("columns"): newURI += '${model.listType}&listRows=${model.listRows}&listColumns=' + val + '&listOffset=${model.listOffset}'; break;
		case ("offset"):  newURI += '${model.listType}&listRows=${model.listRows}&listColumns=${model.listColumns}&listOffset=' + val; break;
	}
	persistentTopLinks(newURI);
}
function refreshPage() {
	window.location.href = window.location.href;
}

function persistentTopLinks(newURI, follow) {
    var id;
    var follow = (typeof(follow)=="undefined") ? true : follow;
    var url = this.location;
    var m = url.toString().match(/.*\/(.+?)\./);
    if (m[1].match(/^.*Settings$/)) {
        m[1] = "settings";
    }
    switch (m[1]) {
        case "home": id = "homeLink"; break
        case "podcastReceiver": id = "podcastLink"; break
        case "status": id = "statusLink"; break
        case "more": case "db": id = "moreLink"; break
        case "logfile": id = "statusLink"; break
        case "settings": id = "settingsLink"; break
    }
    parent.upper.document.getElementById(id).href = newURI;
    parent.upper.document.getElementById(id + "Desc").href = newURI;
    if (follow) {
        parent.main.src = newURI;
        location = newURI;
    }
}
</script>

</html>
