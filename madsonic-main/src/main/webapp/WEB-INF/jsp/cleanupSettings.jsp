<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
	
	<link href="<c:url value="/style/customScrollbar.css"/>" rel="stylesheet">	
</head>
<body class="mainframe bgcolor1">

<div id="content_2" class="content_main">
<!-- CONTENT -->

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="cleanup"/>
    <c:param name="toast" value="${model.toast}"/>
    <c:param name="done" value="${model.done}"/>
    <c:param name="warn" value="${model.warn}"/>
    <c:param name="warnInfo" value="${model.warnInfo}"/>
    <c:param name="bug" value="${model.bug}"/>	
    <c:param name="bugInfo" value="${model.bugInfo}"/>		
</c:import>
<br>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;margin-bottom:15px;"><b>Reset Transcoding profil to ...</b></p> 
<p class="forward"><a href="cleanupSettings.view?reset2MadsonicFLV"> Madsonic - FLV/WEBM/MKV </a> - all-in-one <b>(recommended)</b></p>

<p class="forward"><a href="cleanupSettings.view?reset2MadsonicWEBM">Madsonic - WEBM only</a></p>
<!--<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><b>INFO</b> Reset settings for WEBM</p>-->

<!--<p class="forward"><a href="cleanupSettings.view?reset2MadsonicMP4">Reset Transcoding profil to Madsonic MP4</a></p> -->
<!--<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><b>INFO</b> Reset settings for MP4</p> -->

<p class="forward"><a href="cleanupSettings.view?reset2FLV">Madsonic - FLV only</a></p>
<!--<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><b>INFO</b> Reset settings for Flash Videoplayer</p>

<p class="forward"><a href="cleanupSettings.view?reset2WEBM">old Madsonic WEBM only</a></p>
<!--<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><b>INFO</b> Reset settings for HTML5 Videoplayer</p> -->
<p class="forward"><a href="cleanupSettings.view?reset2Subsonic">Subsonic - Default</a></p>
<!--<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><b>INFO</b> Reset settings to Subsonic default</p> -->
<br>

<p class="forward"><a href="cleanupSettings.view?resetControl"><fmt:message key="cleanupsettings.resetac.title"/></a></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><fmt:message key="cleanupsettings.resetac"/></p>
<br>
<p class="forward"><a href="cleanupSettings.view?resetStats"><fmt:message key="cleanupsettings.stats.title"/></a></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><fmt:message key="cleanupsettings.stats"/></p>
<br>

<p class="forward"><a href="cleanupSettings.view?cleanupHistory"><fmt:message key="cleanupsettings.history.title"/></a></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><fmt:message key="cleanupsettings.history"/></p>
<c:if test="${not empty model.error}">
    <p class="warning"><fmt:message key="${model.error}"/></p>
</c:if>

<c:if test="${model.reload}">
    <script language="javascript" type="text/javascript">parent.frames.upper.location.href="top.view?"</script>
    <script language="javascript" type="text/javascript">parent.frames.left.location.href="left.view?"</script>
</c:if>
<!-- CONTENT -->

</div>
</body></html>