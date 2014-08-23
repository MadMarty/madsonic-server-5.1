/**
 * Similar artists stuff
 */
 function getSimilarArtists() {

     /**
     * Add css
     */
    jQuery('<link />', {
        'type' : 'text/css',
        'rel' : 'stylesheet',
        'href' : '/script/similar_artists/similar_artists.css'
    }).appendTo('head');
    
  
	/**
	 * Escape HTML function
	 */
	var escapeHTML = function(str) {
		var div = jQuery('<div style="display:none" />');
		div.text(str);
		var html = div.html();
		div.remove();
		return html;
	};
	
	// Dont do anything if nowPlaying doesnt exist
	//if(jQuery('#nowPlaying2').length >= 0) {
	var body = jQuery('body');
	if(body.hasClass('rightframe')) {
		rightFrame();
	}
	else if(body.hasClass('playlistframe')) {
		playlistFrame();
	}
	else if(body.hasClass('mainframe')) {
		mainFrame();
	}
	
	
	/**
	 * The main frame.
	 */
	function mainFrame() {
		
		/*monitorCookie('right', function(data) {
			console.debug(data);
		});*/
		
	    /**
	     * Get similar artists config
	     */
		// Similar artists server
	    var url = '//localhost/ArtistInfo/jsonp.php';
		
		// How many seconds between each retry
	    var retry = 5;
	    
	    // Limit number of suggested artists
	    var limit = 5;
	    
	    // If >= 2 different artists, collapse the suggested artists boxes
	    // To disable feature, set to a very high value
	    var collapseThreshold = 6;
		
	    
	    var debug = false && console && console.debug;
		var playingArtistsCount = 0;
		var lastArtists = null;
	    var attempts = 0;
		
		
		/**
		 * Monitor for artist changes
		 */
		setInterval(function() {
			var curArtists = jQuery('#nowPlaying .detail em');
			var playingArtists = []; var k = 0;
			for(var i = 0; i < curArtists.length; i++) {
				playingArtists[k++] = jQuery(curArtists[i]).text();
			}
			playingArtists = jQuery.unique(playingArtists);
			playingArtistsCount = playingArtists.length;
			if(playingArtists.length == 0) return;
			if(lastArtists !== playingArtists.join(',')) {
				
				//console.debug(playingArtists);
				lastArtists = playingArtists.join(',');
				resetSimilarArtists();
				for(var i = 0; i < playingArtists.length; i++) {
					var curArtist = playingArtists[i];
					if(curArtist === '') { continue; }
					if(debug) {
						console.debug('Currently playing: ' + curArtist);
					}
					fetchSimilarArtists(curArtist);
				}
			}
		}, 1000);
		
		/**
		 * Get similar artists from server
		 */
		var fetchSimilarArtists = function(artist) {
			jQuery.ajax({
				'url' : url,
				'data' : {
					'name' : artist,
					'limit' : limit
				},
				'dataType' : 'jsonp',
	//			'jsonp' : 'callback',
				'error' : function(jqXHR, msg) {
					var tmpArtist = lastArtists;
					if(debug) { 
						console.debug('Server failed to respond correctly: '+msg);
						console.debug('Retrying in ' + retry + ' sec');
					}
					setTimeout(function(s) {
						if(s === lastArtists) {
							lastArtists = null;
							if(debug) { console.debug('Retrying...'); }
						}
						else if(debug) {
							console.debug('Aborting retry, artist changed.');
						}
					}, retry * 1000, tmpArtist);
				},
				'success' : function(data, status, jqXHR) {
					if(data.error) {
						this.error(jqXHR, data.error);
					}
					else {
						if(debug) { console.debug(data.data); }
						injectSimilarArtists(data.data, data.data.similar);
					}
				}
			});
		};
		
		/**
		 * Clear the already injected info.
		 */
		var resetSimilarArtists = function() {
			if(jQuery('#similarArtists').length > 0) {
				jQuery('#similarArtists').remove();
			}
			jQuery('#nowPlaying2').after('<div id="similarArtists"></div>');
			
			// Form, exists?
			if(jQuery('#similarArtistsSearch').length == 0) {
				jQuery('#similarArtists').append(
					'<div style="display:none">'+
						'<form target="main" action="search.view" method="post">'+
							'<input type="text" name="query" />'+
						'</form>'+
					'</div>');
			}
		};
		
		/**
		 * Inject a list with similar artists
		 */
		var injectSimilarArtists = function(artist, artists) {
			
			// inject
			var desc = ((artist.type !== 'Unknown') ? '('+artist.type+') ' : '') + artist.disambiguation;
			var section = jQuery(
				'<div class="section">'+
					'<h2 class="head">Similar Artists for</h2>'+
					'<a class="name" href="http://musicbrainz.org/search/textsearch.html?query='+encodeURIComponent(artist.name)+
					'&type=artist" target="_blank" title="'+escapeHTML(desc)+'">'+
					escapeHTML(artist.name)+'</a>'+
					//'<ul style="max-height:200px;overflow:auto;"></ul>'
				'</div>'
			);
			jQuery('#similarArtists').append(section);
			
			// Open/Close
			var openCloseTrigger = jQuery(
					'<a href="#" class="openCloseTrigger">'+
						'<span class="isOpenLabel">&uarr;</span>'+
						'<span class="isClosedLabel">&darr;</span>'+
					'</a>');
			openCloseTrigger.data('isOpen', true);
			openCloseTrigger.bind('openList', function(e, animate) {
				var list = jQuery(this).data('list');
				if(animate || animate == undefined) {
					list.slideDown();
				}
				else {
					list.show();
				}
				jQuery(this).data('isOpen', true);
				jQuery(this).removeClass('isClosed');
			});
			openCloseTrigger.bind('closeList', function(e, animate) {
				var list = jQuery(this).data('list');
				if(animate || animate == undefined) {
					list.slideUp();
				}
				else {
					list.hide();
				}
				jQuery(this).data('isOpen', false);
				jQuery(this).addClass('isClosed');
			});
			openCloseTrigger.click(function(e) {
				e.preventDefault();
				if(jQuery(this).data('isOpen')) { jQuery(this).trigger('closeList'); }
				else { jQuery(this).trigger('openList'); }
			});
			section.append(openCloseTrigger);
			
			// Addding similar artists
			var ul = jQuery('<ul class="similar"></ul>');
			section.append(ul);
			openCloseTrigger.data('list', ul);
			
			var similarCount = 0;
			for(var i = 0; i < artists.length; i++) {
				if(jQuery.trim(artists[i]) === '') continue;
				similarCount++;
				var item = jQuery('<li><a href="#">'+artists[i]+'</a></li>');
				item.click(function(e) {
					e.preventDefault();
					jQuery('#similarArtists input[type=text]').val(jQuery(this).text());
					jQuery('#similarArtists form').submit();
				});
				ul.append(item);
			}
			
			if(similarCount === 0) {
				ul.after('None');
				ul.remove();
				openCloseTrigger.remove();
			}
			else {
				if(playingArtistsCount >= collapseThreshold) {
					openCloseTrigger.trigger('closeList', [ false ]);
					if(debug) {
						console.debug('Closing similar artists, too many people listening to different artists now.');
					}
				}
			}
		};
	
	}
	
	
	/**
	 * Playlist (bottom frame)
	 */
	function playlistFrame() {
//		//console.debug('Its playlist');
//		var table = jQuery('#playlistBody').parent();
//		var add = jQuery('<button>Add suggested song</button>');
//		//add.insertAfter(table);
//		
//		add.click(function(e) {
//			e.preventDefault();
//			//jQuery.cookie('right', 'hello!');
//		});
	}
	
	
	/**
	 * Right sidebar stuff (similar artists widget)
	 */
	function rightFrame() {
		
		/*monitorCookie('right', function(data) {
			console.debug(data);
		});*/
		
	    /**
	     * Get similar artists config
	     */
		// Similar artists server
	    var url = '//localhost/ArtistInfo/jsonp.php';
		
		// How many seconds between each retry
	    var retry = 5;
	    
	    // Limit number of suggested artists
	    var limit = 3;
	    
	    // If >= 2 different artists, collapse the suggested artists boxes
	    // To disable feature, set to a very high value
	    var collapseThreshold = 3;
		
	    
	    var debug = false && console && console.debug;
		var playingArtistsCount = 0;
		var lastArtists = null;
	    var attempts = 0;
		
		
		/**
		 * Monitor for artist changes
		 */
		setInterval(function() {
			var curArtists = jQuery('#nowPlaying .detail em');
			var playingArtists = []; var k = 0;
			for(var i = 0; i < curArtists.length; i++) {
				playingArtists[k++] = jQuery(curArtists[i]).text();
			}
			playingArtists = jQuery.unique(playingArtists);
			playingArtistsCount = playingArtists.length;
			if(playingArtists.length == 0) return;
			if(lastArtists !== playingArtists.join(',')) {
				//console.debug(playingArtists);
				lastArtists = playingArtists.join(',');
				resetSimilarArtists();
				for(var i = 0; i < playingArtists.length; i++) {
					var curArtist = playingArtists[i];
					if(curArtist === '') { continue; }
					if(debug) {
						console.debug('Currently playing: ' + curArtist);
					}
					fetchSimilarArtists(curArtist);
				}
			}
		}, 1000);
		
		/**
		 * Get similar artists from server
		 */
		var fetchSimilarArtists = function(artist) {
			jQuery.ajax({
				'url' : url,
				'data' : {
					'name' : artist,
					'limit' : limit
				},
				'dataType' : 'jsonp',
	//			'jsonp' : 'callback',
				'error' : function(jqXHR, msg) {
					var tmpArtist = lastArtists;
					if(debug) { 
						console.debug('Server failed to respond correctly: '+msg);
						console.debug('Retrying in ' + retry + ' sec');
					}
					setTimeout(function(s) {
						if(s === lastArtists) {
							lastArtists = null;
							if(debug) { console.debug('Retrying...'); }
						}
						else if(debug) {
							console.debug('Aborting retry, artist changed.');
						}
					}, retry * 1000, tmpArtist);
				},
				'success' : function(data, status, jqXHR) {
					if(data.error) {
						this.error(jqXHR, data.error);
					}
					else {
						if(debug) { console.debug(data.data); }
						injectSimilarArtists(data.data, data.data.similar);
					}
				}
			});
		};
		
		/**
		 * Clear the already injected info.
		 */
		var resetSimilarArtists = function() {
			if(jQuery('#similarArtists').length > 0) {
				jQuery('#similarArtists').remove();
			}
			jQuery('#nowPlaying').after('<div id="similarArtists"></div>');
			
			// Form, exists?
			if(jQuery('#similarArtistsSearch').length == 0) {
				jQuery('#similarArtists').append(
					'<div style="display:none">'+
						'<form target="main" action="search.view" method="post">'+
							'<input type="text" name="query" />'+
						'</form>'+
					'</div>');
			}
		};
		
		/**
		 * Inject a list with similar artists
		 */
		var injectSimilarArtists = function(artist, artists) {
			
			// inject
			var desc = ((artist.type !== 'Unknown') ? '('+artist.type+') ' : '') + artist.disambiguation;
			var section = jQuery(
				'<div class="section">'+
					'<h2 class="head">Similar Artists for</h2>'+
					'<a class="name" href="http://musicbrainz.org/search/textsearch.html?query='+encodeURIComponent(artist.name)+
					'&type=artist" target="_blank" title="'+escapeHTML(desc)+'">'+
					escapeHTML(artist.name)+'</a>'+
					//'<ul style="max-height:200px;overflow:auto;"></ul>'
				'</div>'
			);
			jQuery('#similarArtists').append(section);
			
			// Open/Close
			var openCloseTrigger = jQuery(
					'<a href="#" class="openCloseTrigger">'+
						'<span class="isOpenLabel">&uarr;</span>'+
						'<span class="isClosedLabel">&darr;</span>'+
					'</a>');
			openCloseTrigger.data('isOpen', true);
			openCloseTrigger.bind('openList', function(e, animate) {
				var list = jQuery(this).data('list');
				if(animate || animate == undefined) {
					list.slideDown();
				}
				else {
					list.show();
				}
				jQuery(this).data('isOpen', true);
				jQuery(this).removeClass('isClosed');
			});
			openCloseTrigger.bind('closeList', function(e, animate) {
				var list = jQuery(this).data('list');
				if(animate || animate == undefined) {
					list.slideUp();
				}
				else {
					list.hide();
				}
				jQuery(this).data('isOpen', false);
				jQuery(this).addClass('isClosed');
			});
			openCloseTrigger.click(function(e) {
				e.preventDefault();
				if(jQuery(this).data('isOpen')) { jQuery(this).trigger('closeList'); }
				else { jQuery(this).trigger('openList'); }
			});
			section.append(openCloseTrigger);
			
			// Addding similar artists
			var ul = jQuery('<ul class="similar"></ul>');
			section.append(ul);
			openCloseTrigger.data('list', ul);
			
			var similarCount = 0;
			for(var i = 0; i < artists.length; i++) {
				if(jQuery.trim(artists[i]) === '') continue;
				similarCount++;
				var item = jQuery('<li><a href="#">'+artists[i]+'</a></li>');
				item.click(function(e) {
					e.preventDefault();
					jQuery('#similarArtists input[type=text]').val(jQuery(this).text());
					jQuery('#similarArtists form').submit();
				});
				ul.append(item);
			}
			
			if(similarCount === 0) {
				ul.after('None');
				ul.remove();
				openCloseTrigger.remove();
			}
			else {
				if(playingArtistsCount >= collapseThreshold) {
					openCloseTrigger.trigger('closeList', [ false ]);
					if(debug) {
						console.debug('Closing similar artists, too many people listening to different artists now.');
					}
				}
			}
		};
	
	}
}
