
/// Default
 
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

    var w = window.open(href, windowname, "width=" + width + ",height=" + height + ",resizable=yes,scrollbars=yes,toolbar=no,titlebar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no");
    w.focus();
    w.moveTo(300, 200);
    return false;
}

/// Row&Columns

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
