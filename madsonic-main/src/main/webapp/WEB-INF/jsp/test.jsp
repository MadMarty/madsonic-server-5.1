<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<html>
<head>
<%@ include file="head.jsp" %>
<link href="/style/artist/style2.css" rel="stylesheet" media="screen">
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js"> </script>
<script type="text/javascript" src="/script/artist/app.js" </script>
<script type="text/javascript">
	$(document).ready(function() { 
	$(".image_stack").delegate('img', 'mouseenter', function() {//when user hover mouse on image with div id=stackphotos 
			if ($(this).hasClass('stackphotos')) {				
				var $parent = $(this).parent();
				$parent.find('img#photo1').addClass('rotate1');
				$parent.find('img#photo2').addClass('rotate2');
				$parent.find('img#photo3').addClass('rotate3');
				$parent.find('img#photo1').css("left","150px"); 
				$parent.find('img#photo3').css("left","50px");
			}
		})
		.delegate('img', 'mouseleave', function() {
			$('img#photo1').removeClass('rotate1');
				$('img#photo2').removeClass('rotate2');
				$('img#photo3').removeClass('rotate3');
				$('img#photo1').css("left","");
				$('img#photo3').css("left","");
		});;
	});
</script>
</head>
<body style="padding-top: 1em">
	<!--Stack 1  -->
	<div class="image_stack" style="margin-left:600px">
	<img id="photo1" class="stackphotos" src="/photos/1.jpg"  >
	<img id="photo2" class="stackphotos" src="/photos/3.jpg" >
	<img id="photo3" class="stackphotos" src="/photos/3.jpg" > 
	</div>
	<!--Stack 2  -->
	<div class="image_stack" style="margin-left:300px">
	<img id="photo1" class="stackphotos" src="/photos/4.jpg" >
	<img id="photo2" class="stackphotos" src="/photos/5.jpg" >
	<img id="photo3" class="stackphotos" src="/photos/6.jpg" > 
	</div>
	<!--Stack 3  -->
	<div class="image_stack" style="margin-left:300px;margin-top:220px;">
	<img id="photo1" class="stackphotos" src="/photos/1.jpg"  >
	<img id="photo2" class="stackphotos" src="/photos/3.jpg" >
	<img id="photo3" class="stackphotos" src="/photos/3.jpg" > 
	</div>
	<!--Stack 4  -->
	<div class="image_stack" style="margin-left:600px;margin-top:220px;">
	<img id="photo1" class="stackphotos" src="/photos/4.jpg" >
	<img id="photo2" class="stackphotos" src="/photos/5.jpg" >
	<img id="photo3" class="stackphotos" src="/photos/6.jpg" > 
	</div>
	
</body>
</html>