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
    <c:param name="cat" value="group"/>
    <c:param name="toast" value="${model.toast}"/>
</c:import>
<br>
<form method="post" action="groupSettings.view">

<table class="indent">
    <tr>
        <th style="padding-right:2em"><fmt:message key="groupsettings.name"/></th>
		<th style="padding-right:1em"> Video DefaultBitrate</th>
		<th style="padding-right:1em"> Video MaxBitrate</th>		
        <th style="padding-left:1em"><fmt:message key="common.delete"/></th>
    </tr>
    <c:forEach items="${model.groups}" var="groups">
        <tr>
            <td><input style="font-family:monospace" type="text" name="name[${groups.id}]" size="15" value="${groups.name}"/></td>
			<td><select style="font-family:monospace" type="text" name="videoDefaultBitrate[${groups.id}]" size="1"/>
			<c:if test="${groups.videoDefaultBitrate eq 100}"><option selected="selected" value="100">100 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate ne 100}"><option value="100">100 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate eq 200}"><option selected="selected" value="200">200 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate ne 200}"><option value="200">200 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate eq 300}"><option selected="selected" value="300">300 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate ne 300}"><option value="300">300 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate eq 500}"><option selected="selected" value="500">500 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate ne 500}"><option value="500">500 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate eq 700}"><option selected="selected" value="700">700 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate ne 700}"><option value="700">700 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate eq 1000}"><option selected="selected" value="1000">1000 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate ne 1000}"><option value="1000">1000 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate eq 1200}"><option selected="selected" value="1200">1200 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate ne 1200}"><option value="1200">1200 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate eq 1500}"><option selected="selected" value="1500">1500 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate ne 1500}"><option value="1500">1500 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate eq 2000}"><option selected="selected" value="2000">2000 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate ne 2000}"><option value="2000">2000 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate eq 3000}"><option selected="selected" value="3000">3000 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate ne 3000}"><option value="3000">3000 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate eq 5000}"><option selected="selected" value="5000">5000 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate ne 5000}"><option value="5000">5000 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate eq 8000}"><option selected="selected" value="8000">8000 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate ne 8000}"><option value="8000">8000 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate eq 10000}"><option selected="selected" value="10000">10000 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate ne 10000}"><option value="10000">10000 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate eq 15000}"><option selected="selected" value="15000">15000 Kbps</option></c:if>
			<c:if test="${groups.videoDefaultBitrate ne 15000}"><option value="15000">15000 Kbps</option></c:if>
		   </td>
			<td><select style="font-family:monospace" type="text" name="videoMaxBitrate[${groups.id}]" size="1"/>
			<c:if test="${groups.videoMaxBitrate eq 100}"><option selected="selected" value="100">100 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate ne 100}"><option value="100">100 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate eq 200}"><option selected="selected" value="200">200 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate ne 200}"><option value="200">200 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate eq 300}"><option selected="selected" value="300">300 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate ne 300}"><option value="300">300 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate eq 500}"><option selected="selected" value="500">500 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate ne 500}"><option value="500">500 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate eq 700}"><option selected="selected" value="700">700 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate ne 700}"><option value="700">700 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate eq 1000}"><option selected="selected" value="1000">1000 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate ne 1000}"><option value="1000">1000 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate eq 1200}"><option selected="selected" value="1200">1200 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate ne 1200}"><option value="1200">1200 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate eq 1500}"><option selected="selected" value="1500">1500 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate ne 1500}"><option value="1500">1500 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate eq 2000}"><option selected="selected" value="2000">2000 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate ne 2000}"><option value="2000">2000 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate eq 3000}"><option selected="selected" value="3000">3000 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate ne 3000}"><option value="3000">3000 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate eq 5000}"><option selected="selected" value="5000">5000 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate ne 5000}"><option value="5000">5000 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate eq 8000}"><option selected="selected" value="8000">8000 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate ne 8000}"><option value="8000">8000 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate eq 10000}"><option selected="selected" value="10000">10000 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate ne 10000}"><option value="10000">10000 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate eq 15000}"><option selected="selected" value="15000">15000 Kbps</option></c:if>
			<c:if test="${groups.videoMaxBitrate ne 15000}"><option value="15000">15000 Kbps</option></c:if>
		   </td>
		   
            <td align="center" style="padding-left:1em"><input type="checkbox" name="delete[${groups.id}]" class="checkbox"/></td>
        </tr>
    </c:forEach>
    <tr>
        <th colspan="2" align="left" style="padding-top:1em"><fmt:message key="groupsettings.add"/></th>
    </tr>
	<tr>
		<td><input style="font-family:monospace" type="text" name="name" size="15" value=""/></td>
	<td/>
    </tr>
</table>

<p style="padding-top:0.75em">
	<input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
	<input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'" style="margin-right:1.3em">
</p>

</form>
<!-- <p class="forward"><a href="groupSettings.view?resetControl">Reset Access Control</a></p> -->
<c:if test="${not empty model.error}">
    <p class="warning"><fmt:message key="${model.error}"/></p>
</c:if>

<!-- CONTENT -->
</div>
</body></html>