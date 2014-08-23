		function setAspectRatio(wrapper,ratio) { 
            var wrapperDiv = document.getElementById(wrapper);
                if (wrapperDiv) {
					var width = 720;
					var unit = "px";
					var match = /([0-9\.]+)(:|\.)([0-9\.]+)/.exec(ratio);
					if(match){
						if(match[2]==':') height = parseInt((width/match[1])*match[3]);
						if(match[2]=='.') height = parseInt((width/match[0]));						
					}
					else{
						height = (width/16)*9;
					}
                }
                resizeWrapper(wrapper, width, height, unit);	                       
        }
        function resizeWrapper(wrapper, width, height, unit) { 
            var wrapperDiv = document.getElementById(wrapper);
                if (wrapperDiv) { 
                    if (!unit){var unit = "px";}
                    wrapperDiv.style.width = parseInt(width)+unit; 
                    wrapperDiv.style.height = parseInt(height)+unit; 
                }
        }
		function changeAspectRatio(){
			var ratio = document.getElementById('aspectRatio').value;
			if(ratio == "custom"){
				 document.getElementById('customAspectRatio').style.display = 'inline';
			}
			else{
				document.getElementById('customAspectRatio').style.display = 'none';
				setAspectRatio('wrapper',ratio);
			}
		}
		function custAspectRatio(){
			var ratio = document.getElementById('cust_ar_x').value+":"+document.getElementById('cust_ar_y').value;
			setAspectRatio('wrapper',ratio);
		}
		
		function restoreWrapper(){
			var wrapperDiv = document.getElementById('wrapper');
			wrapperDiv.innerHTML = "<div id=\"placeholder1\">reloading player..</div>";
			resizeWrapper("wrapper", "720", "480", "px");
		}
		function toggle_ar_check(){
			var check = document.getElementById('customAspectRatioCheck');
			if(check.value == "manual"){
				check.value = "auto";
				document.getElementById('customAspectRatioCheckImg').title="enable manual aspectratio selection"
				document.getElementById('customAspectRatioCheckImg').src = "/script/aspectratio/ar_off.png";
			}
			else{
				check.value = "manual";
				document.getElementById('customAspectRatioCheckImg').src = "/script/aspectratio/ar_on.png";
				document.getElementById('customAspectRatioCheckImg').title="enable auto aspectratio detection"
			}
		}
		function ar_check(){
			var tmpPosition = getPosition();
			if(document.getElementById('customAspectRatioCheck').value == "auto"){
				document.getElementById('changeAspectRatio').style.visibility = 'visible';
				swfobject.removeSWF("player1");	
				restoreWrapper();
				ar_init();
				changeAspectRatio();
				
			}
			else{
				document.getElementById('changeAspectRatio').style.visibility = 'hidden';
				swfobject.removeSWF("player1");
				restoreWrapper();
				init();	
			}
			toggle_ar_check();
			timeOffset = tmpPosition;
			play();
		}