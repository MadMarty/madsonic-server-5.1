<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<html>
<head>
    <%@ include file="head.jsp" %>
</head>
<body class="mainframe bgcolor1">

<h1>
    <img src="<spring:theme code="donateImage"/>" alt=""/>
    <fmt:message key="madsonic.title"/>
</h1>
<c:if test="${not empty command.path}">
    <sub:url value="main.view" var="backUrl">
        <sub:param name="path" value="${command.path}"/>
    </sub:url>
    <div class="back"><a href="${backUrl}">
        <fmt:message key="common.back"/>
    </a></div>
    <br/>
</c:if>

<c:url value="http://forum.madsonic.org/donate.php" var="donateUrl"/>

<div style="width:50em; max-width:50em">

<fmt:message key="madsonic.textbefore"><fmt:param value="${command.brand}"/></fmt:message>

<table cellpadding="10">
    <tr>
        <td>
            <table>
                <tr>
                    <td><a href="${donateUrl}" target="_blank"><img src="<spring:theme code="paypalImage"/>" alt="#"/></a> </td>
                </tr>
                <tr>
                    <td class="detail" style="text-align:center;"></td>
                </tr>
            </table>
        </td>
	</tr>
</table>
</div>
</body>
</html>