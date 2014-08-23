<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
<%@ include file="head.jsp" %>
<script type="text/javascript">
function fillBox(what)
{
	document.forms[0].query.value = "SELECT TOP 500 * FROM " + what.firstChild.nodeValue;
	return false;
}
</script>
</head><body class="mainframe bgcolor1" onload="document.getElementById('query').focus()">
<h1>Database query</h1>

<div style="float:left; text-align:left; width: 100%; display: block;">
<fmt:message key="db.tables" />
</div>
<div style="float:left; text-align:left; width: 100%; display: block;">
<fmt:message key="db.query" />
</div>
<div style="float:left;">
<form method="post" action="db.view" name="query">
    <textarea rows="10" cols="120" id="query" name="query" style="margin-top:1em">${model.query}</textarea>
    <input type="submit" value="<fmt:message key="common.ok"/>">
</form>
</div>
<!--  
<div style="float:left; text-align:left; width: 650px; height: 40px;display: block;">
<h5><fmt:message key="db.statements" /></h5>
</div>
-->
<c:if test="${not empty model.result}">
<div style="left;">
    <h1 style="margin-top:400px">Result</h1>

    <table class="indent ruleTableCellDB" style="border-collapse:collapse; white-space:nowrap; border-spacing:1px;">
        <c:forEach items="${model.result}" var="row" varStatus="loopStatus">

            <c:if test="${loopStatus.count == 1}">
                <tr>
                    <c:forEach items="${row}" var="entry">
                        <td class="ruleTableHeader">${entry.key}</td>
                    </c:forEach>
                </tr>
            </c:if>
            <tr>
                <c:forEach items="${row}" var="entry">
                    <td class="ruleTableCellDB">${entry.value}</td>
                </c:forEach>
            </tr>
        </c:forEach>

    </table>
</div>	
</c:if>

<c:if test="${not empty model.error}">
    <h1 style="margin-top:280px">Error</h1>

    <p class="warning">
        ${model.error}
    </p>
</c:if>

</body></html>