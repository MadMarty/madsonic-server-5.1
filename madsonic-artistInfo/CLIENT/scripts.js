function noop() {
}

function popup(mylink, windowname) {
    return popupSize(mylink, windowname, 400, 200);
}

function popupSize(mylink, windowname, width, height) {
    var href;
    if (typeof(mylink) == "string") {
        href = mylink;
    } else {
        href = mylink.href;
    }

    var w = window.open(href, windowname, "width=" + width + ",height=" + height + ",scrollbars=yes,resizable=yes");
    w.focus();
    w.moveTo(300, 200);
    return false;
}

/// Add similar artists
(function() {
	
	Node.prototype.insertAfter = function(newNode, refNode) {
		if(refNode.nextSibling) {
			return this.insertBefore(newNode, refNode.nextSibling);
		} else {
			return this.appendChild(newNode);
		}
	};
	
	var loaded = function() {
		//load.injectDependency('https://github.com/cowboy/jquery-postmessage/raw/master/jquery.ba-postmessage.js');
		load.injectDependency('./script/similar_artists/similar_artists.js');
	};
	
    var load = function() {
    	load.injectDependency('./script/similar_artists/jquery-1.5.1.min.js');
    	load.tryReady(0);
    };
    
    load.injectDependency = function(script) {
        var sa = document.createElement('script');
        sa.type = 'text/javascript';
        sa.async = 'async';
        sa.src = script;
        var s = document.getElementsByTagName('script')[0];
        s.parentNode.insertAfter(sa, s);
    };
    
    load.tryReady = function(time_elapsed) {
    	// Continually polls to see if jQuery is loaded.
    	if(typeof(jQuery) === undefined) { // if jQuery isn't loaded yet...
    		if (time_elapsed <= 15000) { // and we havn't given up trying...
    			//setTimeout("load.tryReady(" + (time_elapsed + 200) + ")", 200); // set a timer to check again in 200 ms.
    			setTimeout(function() {
    				load.tryReady(200);
    			}, 200);
    		}
    		else {
    			alert("Timed out while loading jQuery.");
    		}
    	}
    	else {
    		loaded();
    	}
	};
	
	load();
})();
