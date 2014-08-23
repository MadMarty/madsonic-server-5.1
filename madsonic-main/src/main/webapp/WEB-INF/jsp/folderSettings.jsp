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
    <c:param name="cat" value="folder"/>
    <c:param name="toast" value="${model.toast}"/>
    <c:param name="done" value="${model.done}"/>
    <c:param name="warn" value="${model.warn}"/>
    <c:param name="warnInfo" value="${model.warnInfo}"/>
    <c:param name="bug" value="${model.bug}"/>	
    <c:param name="bugInfo" value="${model.bugInfo}"/>		
</c:import>
<br>
<p class="forward"><a href="folderSettings.view?scanNow">Normal Rescan</a></p>
<p class="detail" style="padding-left: 20px;width:65%;white-space:normal;margin-top:-5px;"><fmt:message key="cleanupsettings.scannow"/></p>
<c:if test="${model.scanning}">
	<p style="width:65%"><b><fmt:message key="musicfoldersettings.nowscanning"/></b></p>
</c:if>
<br>
<p class="forward"><a href="folderSettings.view?FullscanNow"><fmt:message key="cleanupsettings.fullscan.title"/></a></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><fmt:message key="cleanupsettings.fullscan"/></p>
<br>
<p class="forward"><a href="folderSettings.view?FullCleanupNow"><fmt:message key="cleanupsettings.fullcleanupscan.title"/></a></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><fmt:message key="cleanupsettings.fullcleanupscan"/></p>
<br>
<p class="forward"><a href="folderSettings.view?expunge"><fmt:message key="cleanupsettings.expunge.title"/></a></p>
<p class="detail" style="padding-left: 20px;width:80%;white-space:normal;margin-top:-5px;"><fmt:message key="cleanupsettings.expunge"/></p>
<br>
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