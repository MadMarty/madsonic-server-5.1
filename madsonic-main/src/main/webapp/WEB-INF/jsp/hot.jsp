<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>

<%--
Creates HTML for displaying the rating stars.
PARAMETERS
  id: MediaFile id
  flag: HotFlag int.
--%>

<sub:url value="setHot.view" var="hotUrl">
	<sub:param name="id" value="${param.id}"/>
	<sub:param name="flag" value="1"/>
</sub:url>

<c:choose>
	<c:when test="${param.flag eq '1'}">
		<spring:theme code="hotOnImage" var="imageUrl"/>
	</c:when>
	<c:otherwise>
		<spring:theme code="hotOffImage" var="imageUrl"/>
	</c:otherwise>
</c:choose>

    <c:choose>
        <c:when test="${param.readonly}">
            <img src="${imageUrl}" style="margin-right:-3px" alt="" title="<fmt:message key="hot.rating"/>">
        </c:when>
        <c:otherwise>
            <a href="${hotUrl}"><img src="${imageUrl}" style="margin-right:-3px" alt="" title="<fmt:message key="hot.rating"/>"></a>
        </c:otherwise>
    </c:choose>

<sub:url value="setHot.view" var="clearHotUrl">
    <sub:param name="id" value="${param.id}"/>
    <sub:param name="flag" value="0"/>
</sub:url>

<c:if test="${not param.readonly}">
    | <a href="${clearHotUrl}"><img src="<spring:theme code="clearRatingImage"/>" alt="" title="<fmt:message key="hot.clearrating"/>" style="margin-left:-3px; margin-right:5px"></a>
</c:if>