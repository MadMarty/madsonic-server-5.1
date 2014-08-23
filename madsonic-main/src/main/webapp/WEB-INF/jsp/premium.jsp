<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%--@elvariable id="command" type="org.madsonic.command.PremiumCommand"--%>
<html>
<head>
    <%@ include file="head.jsp" %>
</head>
<body class="mainframe bgcolor1">

<h1>
    <img src="<spring:theme code="donateImage"/>" alt=""/>
    <fmt:message key="premium.title"/>
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

<div style="width:50em; max-width:50em">

    <fmt:message key="premium.text"/>
    <fmt:formatDate value="${command.licenseInfo.licenseExpires}" dateStyle="long" var="expirationDate"/>

    <c:if test="${command.licenseInfo.licenseValid}">
        <c:choose>
            <c:when test="${empty command.licenseInfo.licenseExpires}">
                <p><b><fmt:message key="premium.licensed"/></b></p>
            </c:when>
            <c:otherwise>
                <p><b><fmt:message key="premium.licensedexpires"><fmt:param value="${expirationDate}"/></fmt:message></b></p>
            </c:otherwise>
        </c:choose>
        <c:if test="${not command.forceChange and not command.submissionError}">
            <p>
                <fmt:message key="premium.licensedto"><fmt:param value="${command.licenseInfo.licenseEmail}"/></fmt:message>
            </p>
            <c:if test="${command.user.adminRole}">
                <div class="forward"><a href="premium.view?change"><fmt:message key="premium.forcechange"/></a></div>
            </c:if>
        </c:if>
    </c:if>

    <c:if test="${not command.licenseInfo.licenseValid}">
        <c:if test="${not empty command.licenseInfo.licenseExpires}">
            <p><b><fmt:message key="premium.licensedexpired"><fmt:param value="${expirationDate}"/></fmt:message></b></p>
        </c:if>
        <p class="forward" style="font-size:1.2em;margin-left: 1em"><b><a href="http://subsonic.org/pages/premium.jsp" target="_blank">
            <fmt:message key="premium.getpremium"/>
            <c:if test="${command.licenseInfo.trialDaysLeft gt 0}">
                &ndash; <fmt:message key="top.trialdaysleft"><fmt:param value="${command.licenseInfo.trialDaysLeft}"/></fmt:message>
            </c:if>
        </a></b></p>

        <p><fmt:message key="premium.register"/></p>
    </c:if>

    <c:if test="${not command.licenseInfo.licenseValid or command.forceChange or command.submissionError}">
        <form:form commandName="command" method="post" action="premium.view">
            <form:hidden path="path"/>
            <table>
                <tr>
                    <td><fmt:message key="premium.register.email"/></td>
                    <td>
                        <form:input path="licenseInfo.licenseEmail" size="40"/>
                    </td>
                </tr>
                <tr>
                    <td><fmt:message key="premium.register.license"/></td>
                    <td>
                        <form:input path="licenseCode" size="40"/>
                    </td>
                    <td><input type="submit" value="<fmt:message key="common.ok"/>"/></td>
                </tr>
                <tr>
                    <td/>
                    <td class="warning"><form:errors path="licenseCode"/></td>
                </tr>
            </table>
        </form:form>

        <p><fmt:message key="premium.resend"/></p>
    </c:if>

</div>
</body>
</html>