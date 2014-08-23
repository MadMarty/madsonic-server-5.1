<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">

<html><head>
    <%@ include file="head.jsp" %>

    <link rel="alternate" type="application/rss+xml" title="Madsonic Podcast" href="podcast.view?suffix=.rss">

<c:if test="${model.autohideChat}">	

	<%@ include file="jquery.jsp" %>	

    <script type="text/javascript" src="<c:url value="/script/jScrollPane.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/jquery.mousewheel.js"/>"></script>

    <script type="text/javascript">
   $(function() {
      $('#frame-left').jScrollPane();
   });
   $(function() {
       $("#right")
      .mouseover(function() {
          document.getElementById('middleframe').cols = '*,210';
      })
      .mouseout(function() {
          document.getElementById('middleframe').cols = '*,15';
      });
   });
    </script>
</c:if>
</head>

<frameset rows="82,*,0" border="0" framespacing="0" frameborder="0">
    <frame name="upper" src="top.view?">
    <frameset cols="${model.leftframeSize},*" border="${model.LeftFrameResizeable ? 1 : 0}" framespacing="0" frameborder="${model.LeftFrameResizeable ? 1 : 0}">
        <frame name="left" src="left.view?" marginwidth="0" marginheight="0">

        <frameset rows="*,${model.playQueueSize}" border="${model.PlayQueueResizeable ? 1 : 0}" framespacing="0" frameborder="${model.PlayQueueResizeable ? 1 : 0}">
            <frameset id="middleframe" cols="*,${model.showRight ? 220 : 0}" border="0" framespacing="0" frameborder="0">
                <frame id="main" name="main" src="home.view?listType=${model.listType}&listRows=${model.listRows}&listColumns=${model.listColumns}" marginwidth="0" marginheight="0">
                <frame id="right" name="right" src="right.view?">
            </frameset>
            <frame name="playQueue" src="playQueue.view?" scrolling="${model.customScrollbar ? 'no' : 'yes'}" >
        </frameset>
    </frameset>
    <frame name="hidden" frameborder="0" noresize="noresize">

</frameset>

</html>