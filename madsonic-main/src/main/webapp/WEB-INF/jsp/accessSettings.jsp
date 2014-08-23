<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
	
	<link href="<c:url value="/style/customScrollbar.css"/>" rel="stylesheet">
</head>
<body class="mainframe bgcolor1">

<div id="content_2" class="content_main">
<!-- CONTENT -->

<script type="text/javascript" src="<c:url value="/script/wz_tooltip.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/tip_balloon.js"/>"></script>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="access"/>
    <c:param name="toast" value="${model.toast}"/>
</c:import>
<br>
<form method="post" action="accessSettings.view">
<table class="indent">
    <tr>
        <th style="padding-right:1em;"><fmt:message key="accessSettings.name"/> </th>
		
		    <c:forEach items="${model.musicFolders}" var="musicFolders">

				<c:if test="${musicFolders.enabled eq 'true'}">
				<th style="padding-left:1em; width:80px;background-color:gray;color:#EEE;">${musicFolders.name}</th>
				</c:if>

				<c:if test="${musicFolders.enabled eq 'false'}">
				<th style="padding-left:1em; width:80px;background-color:gray;"><font color="red">${musicFolders.name} (DISABLED)</font></th>
				</c:if>
			
			</c:forEach>
	</tr>

    <c:forEach items="${model.accessToken}" var="accessToken">
	<tr>
		<td>${accessToken.name}</td>
		<c:forEach items="${accessToken.accessRights}" var="accessRights">
		
			<c:if test="${accessRights.musicfolder_enabled eq 'false'}">
				<c:if test="${accessRights.enabled eq 'true'}">
				<td align="center" style="padding-left:1em;background-color:gray;"><input type="checkbox" name="toggle[${accessToken.name}${accessRights.musicfolder_id}]" disabled checked class="checkbox"/></td>
				</c:if>
				<c:if test="${accessRights.enabled eq 'false'}">
				<td align="center" style="padding-left:1em;background-color:gray;"><input type="checkbox" name="toggle[${accessToken.name}${accessRights.musicfolder_id}]" disabled class="checkbox"/></td>
				</c:if>
			</c:if>
		
			<c:if test="${accessRights.musicfolder_enabled eq 'true'}">
				<c:if test="${accessRights.enabled eq 'true'}">
				<td align="center" style="padding-left:1em;background-color: rgb(0, 155, 0);"><input type="checkbox" name="toggle[${accessToken.name}${accessRights.musicfolder_id}]" checked class="checkbox"/></td>
				</c:if>
				<c:if test="${accessRights.enabled eq 'false'}">
				<td align="center" style="padding-left:1em;background-color:red;"><input type="checkbox" name="toggle[${accessToken.name}${accessRights.musicfolder_id}]" class="checkbox"/></td>
				</c:if>
			</c:if>
		</c:forEach>
		</tr>
    </c:forEach>
	
    <tr>
        <th colspan="6" align="left" style="padding-top:1em"><fmt:message key="accessSettings.add"/></th>
    </tr>
</table>

<p style="padding-top:0.75em">
	<input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
	<input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'" style="margin-right:1.3em">
</p>

</form>

<c:if test="${not empty model.error}">
    <p class="warning"><fmt:message key="${model.error}"/></p>
</c:if>

<!-- CONTENT -->
</div>

</body></html>