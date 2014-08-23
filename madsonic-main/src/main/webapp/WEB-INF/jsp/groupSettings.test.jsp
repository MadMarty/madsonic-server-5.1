<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
</head>
<body class="mainframe bgcolor1">
<script type="text/javascript" src="<c:url value="/script/wz_tooltip.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/tip_balloon.js"/>"></script>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="group"/>
    <c:param name="toast" value="${model.toast}"/>
</c:import>

<form method="post" action="groupSettings.view">
<table class="indent">
    <tr>
        <th><fmt:message key="groupsettings.name"/></th>
		<td>Default <c:import url="helpToolTip.jsp"><c:param name="topic" value="transcode"/></c:import></td>
		<td>MAX <c:import url="helpToolTip.jsp"><c:param name="topic" value="transcode"/></c:import></td>
		<td>Default <c:import url="helpToolTip.jsp"><c:param name="topic" value="transcode"/></c:import></td>
		<td>MAX <c:import url="helpToolTip.jsp"><c:param name="topic" value="transcode"/></c:import></td>
		
        <th style="padding-left:1em"><fmt:message key="common.delete"/></th>
    </tr>
    <c:forEach items="${model.groups}" var="groups">
        <tr>
            <td><input style="font-family:monospace" type="text" name="name[${groups.id}]" size="15" value="${groups.name}"/></td>

            <td>
				<select name="AudioDefaultBitrate">
					<option>128 Kbps</option>
					<option>160 Kbps</option>
					<option>192 Kbps</option>
					<option>256 Kbps</option>
					<option>320 Kbps</option>
					<option>AUTO</option>
				</select>
            </td>

            <td>
				<select name="AudioMaxBitrate">
					<option>128 Kbps</option>
					<option>160 Kbps</option>
					<option>192 Kbps</option>
					<option>256 Kbps</option>
					<option>320 Kbps</option>
					<option>AUTO</option>
				</select>
            </td>
            <td>
				<select name="VideoDefaultBitrate">
					<option>100 Kpbs</option>
					<option>200 Kpbs</option>
					<option>500 Kpbs</option>
					<option>1000 Kpbs</option>
					<option>2000 Kpbs</option>
					<option>5000 Kpbs</option>
					<option>10000 Kpbs</option>
					<option>AUTO</option>
					</select>
            </td>

            <td>
				<select name="VideoMaxBitrate">
					<option>100 Kpbs</option>
					<option>200 Kpbs</option>
					<option>500 Kpbs</option>
					<option>1000 Kpbs</option>
					<option>2000 Kpbs</option>
					<option>5000 Kpbs</option>
					<option>10000 Kpbs</option>
					<option>AUTO</option>
				</select>
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
</body></html>