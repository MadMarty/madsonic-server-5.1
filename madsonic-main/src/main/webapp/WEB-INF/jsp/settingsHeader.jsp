<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%@ include file="include.jsp" %>

<c:if test="${param.bug}">
    <script type="text/javascript">
        $(document).ready(function () {
			// reconfiguring the toasts as sticky
			$().toastmessage({sticky:true});
            $().toastmessage("showErrorToast", "${param.bugInfo}"); // <fmt:message key="common.bug"/> 
        });
    </script>
</c:if>

<c:if test="${param.warn}"> 
    <script type="text/javascript">
        $(document).ready(function () {
			// reconfiguring the toasts as sticky
			$().toastmessage({sticky:true});		
            $().toastmessage("showWarningToast", "${param.warnInfo}" ); 
        });
    </script>
</c:if>

<c:if test="${param.toast}">
    <script type="text/javascript">
        $(document).ready(function () {
			$().toastmessage({sticky:false});		
            $().toastmessage("showNoticeToast", "<fmt:message key="common.settingssaved"/>");
        });
    </script>
</c:if>

<c:if test="${param.done}">
    <script type="text/javascript">
        $(document).ready(function () {
			$().toastmessage({sticky:false});		
            $().toastmessage("showSuccessToast", "<fmt:message key="common.done"/>");
        });
    </script>
</c:if>


<c:set var="categories" value="${param.restricted ? 'personal password player share' : 'musicFolder  folder general advanced personal default user group access icon lastfm player pandora share network dlna internetRadio podcast transcoding cleanup playlist'}"/>
<h1>
    <img src="<spring:theme code="settingsImage"/>" alt=""/>
    <fmt:message key="settingsheader.title"/>
</h1>

<h2>
<c:forTokens items="${categories}" delims=" " var="cat" varStatus="loopStatus">
    <c:choose>
        <c:when test="${loopStatus.count > 1 and  (loopStatus.count - 1) % 11 != 0}">&nbsp;<img src="<spring:theme code="sepImage"/>" alt="">&nbsp;</c:when>
        <c:otherwise></h2><h2></c:otherwise>
    </c:choose>

    <c:url var="url" value="${cat}Settings.view?"/>

    <c:choose>
        <c:when test="${param.cat eq cat}">
            <span class="headerSelected"><fmt:message key="settingsheader.${cat}"/></span>
        </c:when>
        <c:otherwise>
            <a href="${url}"><fmt:message key="settingsheader.${cat}"/></a>
        </c:otherwise>
    </c:choose>

</c:forTokens>
</h2>

<p></p>
