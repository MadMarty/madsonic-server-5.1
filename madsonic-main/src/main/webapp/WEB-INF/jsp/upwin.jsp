<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
</head>
<body class="bgcolor2">

<h1><fmt:message key="upload.title"/></h1>
<div class="detail">
<c:forEach items="${model.uploadedFiles}" var="file">
    <p><fmt:message key="upload.success"><fmt:param value="${file.path}"/></fmt:message></p>
</c:forEach>

<c:forEach items="${model.unzippedFiles}" var="file">
    <fmt:message key="upload.unzipped"><fmt:param value="${file.path}"/></fmt:message><br/>
</c:forEach>

<c:choose>
    <c:when test="${not empty model.exception}">
        <p><fmt:message key="upload.failed"><fmt:param value="${model.exception.message}"/></fmt:message></p>
    </c:when>
    <c:when test="${empty model.uploadedFiles}">
        <p><fmt:message key="upload.empty"/></p>
    </c:when>
</c:choose>
</div>
<table>
<tr>
<td>
<div class="back"><a href="javascript:window.close()"> Close Window</a></td><td> 
<div class="back"><a href="moree.view?"> <fmt:message key="common.back"/></a></div></td>
</tr>
</table>

</div>
</body>
</html>