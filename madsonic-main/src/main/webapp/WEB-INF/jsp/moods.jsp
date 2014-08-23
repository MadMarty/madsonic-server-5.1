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
	
		
<style type="text/css">
span.off {
    cursor: pointer;
    float:left;
    padding: 2px 6px;
    margin: 2px;
    background: #FFF;
    color: #000;
    -webkit-border-radius: 7px;
    -moz-border-radius: 7px;
    border-radius: 7px;
    border: solid 1px #CCC;
    -webkit-transition-duration: 0.1s;
    -moz-transition-duration: 0.1s;
    transition-duration: 0.1s;
    -webkit-user-select:none;
    -moz-user-select:none;
    -ms-user-select:none;
    user-select:none;
    white-space: nowrap;
}

span.on {
    cursor: pointer;
    float:left;
    padding: 2px 6px;
    margin: 2px;
    background: #F5C70F;
    color: #000;
    -webkit-border-radius: 7px;
    -moz-border-radius: 7px;
    border-radius: 7px;
    border: solid 1px #999;
    -webkit-transition-duration: 0.1s;
    -moz-transition-duration: 0.1s;
    transition-duration: 0.1s;
    -webkit-user-select:none;
    -moz-user-select:none;
    -ms-user-select:none;
    user-select:none;
    white-space: nowrap;
}

span.off:hover {
    background: #F5C70F;opacity:0.7;
    border: solid 1px #999;
    text-decoration: none;
	}
	</style>
	
	<script type="text/javascript">

function changeClass(elem, className1,className2) {
    elem.className = (elem.className == className1)?className2:className1;
}
function playMoodRadio() {
	var moods = new Array();
	var e = document.getElementsByTagName("span");
	for (var i = 0; i < e.length; i++) {
		if (e[i].className == "on") {
			moods.push(e[i].firstChild.data);
		}
	}
	var num = document.getElementsByName("MoodRadioPlayCount")[0].selectedIndex;
	var playcount = document.getElementsByName("MoodRadioPlayCount")[0].options[num].text;

		var numyear = document.getElementsByName("MoodYear")[0].selectedIndex;
		var moodsyear = document.getElementsByName("MoodYear")[0].options[numyear].value;
		
	parent.playQueue.onPlayMoodRadio(moods, playcount, moodsyear);
}
	</script>
</head>
<body class="mainframe bgcolor1">

<!-- content block -->

<div id="content_2" class="content_main">
<!-- CONTENT -->

<h1>
	<img src="<spring:theme code="moodsImage"/>" alt="">
	Moods
</h1>
<c:choose>
	<c:when test="${empty model.moods}">
		<p>Please scan your library before</a>.
	</c:when>
	<c:otherwise>
		<p>Choose one or more moods.</p>
	<table>
		<tr>
		<td width="90%"> 
		<c:forEach items="${model.moods}" var="mood">
			<span class="off" onclick='changeClass(this,"on","off");'>${mood}</span>
		</c:forEach>
		<div style="clear:both"/>
		<br>

		<form>		
		<select name="MoodYear">
				<option value="any"><fmt:message key="more.random.anyyear"/></option>

				<c:forEach begin="0" end="${model.currentYear - 2005}" var="yearOffset">
					<c:set var="year" value="${model.currentYear - yearOffset}"/>
					<option value="${year} ${year}">${year}</option>
				</c:forEach>

				<option value="2010 2015">2010 &ndash; 2015</option>
				<option value="2005 2010">2005 &ndash; 2010</option>
				<option value="2000 2005">2000 &ndash; 2005</option>
				<option value="1990 2000">1990 &ndash; 2000</option>
				<option value="1980 1990">1980 &ndash; 1990</option>
				<option value="1970 1980">1970 &ndash; 1980</option>
				<option value="1960 1970">1960 &ndash; 1970</option>
				<option value="1950 1960">1950 &ndash; 1960</option>
				<option value="0 1949">&lt; 1950</option>
		</select>
					
		<select name="MoodRadioPlayCount">
			<option>10</option>
			<option>15</option>
			<option>25</option>
			<option>50</option>
			<option>75</option>
			<option>100</option>
			<option>150</option>
			<option>200</option>
			</select>

		<input type="button" value="Play Mood Radio!" onClick="playMoodRadio();">
		</form>
	</td>
	<td></td>
		</tr>	
		</table> 
	</c:otherwise>
</c:choose>
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