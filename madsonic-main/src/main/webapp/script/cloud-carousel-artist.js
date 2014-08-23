	$(document).ready(function(){
		$("#carousel1").CloudCarousel({			
		reflHeight: 25,
		reflGap:2,
		titleBox: $('#carousel1-title'),
		altBox: $('#carousel1-alt'),
		buttonLeft: $('#left-but'),
		buttonRight: $('#right-but'),
		yRadius: 5,
		xRadius: 540,
		xPos: 70,
		yPos: 5,
		speed: 0.80,
		minScale: 0.5,
		autoRotate: 'right',
		autoRotateDelay: 3000,
		bringToFront: true,
		mouseWheel: true,
		OpacityMode: true
		});
			
		
		/* Navigation carousel with dragging the mouse */
		var xpos = 0;
		var ypos = 0;
		var de;
		$('#carousel1')
			.bind('dragstart',function(event){
				xpos=event.offsetX;
				dragEnd=false;
				if (de!=false) clearTimeout(de);
			})
			.bind('drag',function(event){
					dragline=Math.abs(xpos-event.offsetX);
					mindrag=$(window).width()/14;
					if (Math.abs(xpos-event.offsetX)>mindrag) {
						n=Math.floor(dragline/mindrag);
						for (i=0;i<n;i++) {
							if (xpos<event.offsetX) $("#left-but").mouseup();
							if (xpos>event.offsetX) $("#right-but").mouseup();
						}
						xpos=event.offsetX;
					}
			})
			.bind('dragend',function(event){
				 de = setTimeout("dragEnd=true;", 1500);
			});
			
		/* Navigation using the arrow keys */
		$(document).keydown(function(e) {
			 if (e.keyCode == 39) { 
				$("#right-but").mouseup();
			 }   
			 if (e.keyCode == 37) { 
				$("#left-but").mouseup();
			 }   
 		})	
		
		/* Preloader */

		/* Change the properties of preloader */
		$("#preloader").css({
			"opacity":"0.8",
			"height":$("#resizable").height()
		});
		$("#preloaderText").css({
			"margin-top":$("#resizable").height()/2+20,
			"margin-left":$("#resizable").width()/2-20
		});
		$("#currentProcess").html("loading ...");
		
		/* Percentage (for each image proportionally increases the number) */
		$("#carousel1 img").each(function() {
			$(this).load(function() {
				if (typeof imgCNumb == "undefined") imgCNumb = 0;
				$("#persent").html(Math.round(imgCNumb*100/17) + "%");
				imgCNumb++;
			});
		});
		
		/* The ability to stop the loading on a slow connection */
		$("#stopLoading").delay(4000).css("display","block");
		$("#stopLoading").click(function() {
			if (window.stop !== undefined) window.stop();
			else if (document.execCommand !== undefined) {
				document.execCommand("Stop",false);
			}
			Stopped = 1;
			$(this).hide("slow");
			$("#startLoading").show("slow");
			$("#currentProcess").html(" - ");
		});
		
		/* Resume download */
		$("#startLoading").click(function() {
			window.location.reload();
		});
		
		/* hide preloader when loading is complete */
		$(window).load(function() {
			if (typeof Stopped == "undefined") Stopped = 0;
			if (Stopped == 0) $("#preloader").delay(300).hide("fast"); 
		});
		
		/* Alignment adjustment - when the page is loaded and resized */
		$(window).bind('load resize', function() {
			
			$("#preloader").css("height",$("#resizable").height());	
			$("#preloaderText").css({
				"margin-top":$("#resizable").height()/2+40,
				"margin-left":$("#resizable").width()/2-60
			});	
		});
	});