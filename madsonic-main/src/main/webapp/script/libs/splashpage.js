var isSafari = Object.prototype.toString.call(window.HTMLElement).indexOf('Constructor') > 0;
var isChrome = !isSafari && testCSS('WebkitTransform');  // Chrome 1+	
var isFirefox = navigator.userAgent.toLowerCase().indexOf('firefox') > -1;

function testCSS(prop) { return prop in document.documentElement.style; }

$(function(){

	// BROWSER CHECK
	
  $(window).bind( 'hashchange', function(e) {
		var pages = ['message-1', 'message-2', 'about', 'credits'];
    var url = $.param.fragment();

    if (url == "" || $.inArray(url, pages) == -1) {
	    $('#container').children().fadeOut('500', function(){
				setTimeout(function() {
					if (!isChrome || !isFirefox) {
						$('canvas').remove();
						$('img#staticbg').fadeIn('500');
						$('#message-2').fadeIn('500');
					} else
					$('#index-content').fadeIn('500');
					$('#logos').fadeIn('500');
				},500);		
			});
		} else {
			if (url == "message-1" || url == "message-2")
				var go_here = "#"+url;
			else {
		  	var go_here = '#'+url+"-content";
		
				$(document).unbind('mouseup').mouseup(function (e)
				{
					////console.log("target: "+$('footer').has(e.target).length);
				  var container = $(go_here);
				  if (container.has(e.target).length === 0 && $('footer').has(e.target).length === 0)
						window.location.hash = "";
				});
			}
	    $( '#container' ).children().not(go_here).fadeOut('500', function(){
				setTimeout(function() {
					$(go_here).fadeIn('500');	
				},500);
			});			
		}
  })
  
	$('.close .btn').click(function(){
		window.location.hash = "";
	});
  // Since the event is only triggered when the hash changes, we need to trigger
  // the event now, to handle the hash the page may have loaded with.
  $(window).trigger( 'hashchange' );
  
});