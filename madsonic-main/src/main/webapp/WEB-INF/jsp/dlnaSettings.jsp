<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
</head>

<body class="mainframe bgcolor1">

<div id="content_2" class="content_main">
<!-- CONTENT -->

<script type="text/javascript" src="<c:url value="/script/wz_tooltip.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/tip_balloon.js"/>"></script>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="dlna"/>
    <c:param name="toast" value="${model.toast}"/>
</c:import>
<br>
<form method="post" action="dlnaSettings.view">

    <p>
    <input type="checkbox" name="dlnaEnabled" id="dlnaEnabled" class="checkbox"
           <c:if test="${model.dlnaEnabled}">checked="checked"</c:if>/>
    <label for="dlnaEnabled"><fmt:message key="dlnasettings.enabled"/></label>
    </p>

    <p class="detail" style="width:30%;white-space:normal">
        <fmt:message key="dlnasettings.description"/>
    </p>
	
	<br>
		
    <div>
        <fmt:message key="dlnasettings.servername"/>
        <input name="dlnaServerName" id="dlnaServerName" size="40"
               value="<c:out value="${model.dlnaServerName}" escapeXml="true"/>"/>
    </div>
    <p class="detail" style="width:60%;white-space:normal;padding-top:10px">
        <fmt:message key="dlnasettings.servername.description"/>
    </p>

    <p>
        <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
        <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">
    </p>
</form>

<!-- CONTENT -->

</div>
</body></html>