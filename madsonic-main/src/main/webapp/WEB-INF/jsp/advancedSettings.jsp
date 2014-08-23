<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%--@elvariable id="command" type="org.madsonic.command.AdvancedSettingsCommand"--%>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
	
    <script type="text/javascript" language="javascript">
        function enableLdapFields() {
            $("#ldap").is(":checked") ? $("#ldapTable").show() : $("#ldapTable").hide();
        }
    </script>
	
	<c:if test="${customScrollbar}">
		<link href="<c:url value="/style/customScrollbar.css"/>" rel="stylesheet">
		<script type="text/javascript" src="<c:url value="/script/jquery.mousewheel.min.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/script/jquery.mCustomScrollbar.js"/>"></script>
	</c:if>		
</head>

<body class="mainframe bgcolor1" onload="enableLdapFields()">

<div id="content_2" class="content_main">
<!-- CONTENT -->

<script type="text/javascript" src="<c:url value="/script/wz_tooltip.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/tip_balloon.js"/>"></script>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="advanced"/>
    <c:param name="toast" value="${command.toast}"/>
</c:import>
<br>
<form:form method="post" action="advancedSettings.view" commandName="command">
    <table style="white-space:nowrap;" class="indent">
        <tr>
            <td><fmt:message key="advancedsettings.coverartlimit"/></td>
            <td>
                <form:input path="coverArtLimit" size="8"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="coverartlimit"/></c:import>
            </td>
        </tr>
        <tr>
            <td><fmt:message key="advancedsettings.downloadlimit"/></td>
            <td>
                <form:input path="downloadLimit" size="8"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="downloadlimit"/></c:import>
            </td>
        </tr>
        <tr>
            <td><fmt:message key="advancedsettings.uploadlimit"/></td>
            <td>
                <form:input path="uploadLimit" size="8"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="uploadlimit"/></c:import>
            </td>
        </tr>
        <tr>
            <td><fmt:message key="advancedsettings.streamport"/></td>
            <td>
                <form:input path="streamPort" size="8"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="streamport"/></c:import>
            </td>
        </tr>
        <tr>
        <tr>
            <td> </td>
            <td> </td>
        </tr>
        </tr>
        <tr>
	<td><fmt:message key="advancedsettings.info"/></td>
        </tr>
    </table>
    
  
    
<!-- TODO: RE-ENABLE LDAP

    <table class="indent"><tr><td>
        <form:checkbox path="ldapEnabled" id="ldap" cssClass="checkbox" onclick="enableLdapFields()"/>
        <label for="ldap"><fmt:message key="advancedsettings.ldapenabled"/></label>
        <c:import url="helpToolTip.jsp"><c:param name="topic" value="ldap"/></c:import>
    </td></tr></table>
-->

    <table class="indent" id="ldapTable" style="padding-left:2em;padding-bottom: 1em">
        <tr>
            <td><fmt:message key="advancedsettings.ldapurl"/></td>
            <td colspan="3">
                <form:input path="ldapUrl" size="110"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="ldapurl"/></c:import>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="advancedsettings.ldapsearchfilter"/></td>
            <td colspan="3">
                <form:input path="ldapSearchFilter" size="110"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="ldapsearchfilter"/></c:import>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="advancedsettings.ldapmanagerdn"/></td>
            <td>
                <form:input path="ldapManagerDn" size="70"/>
            </td>
            <td><fmt:message key="advancedsettings.ldapmanagerpassword"/></td>
            <td>
                <form:password path="ldapManagerPassword" size="20"/>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="ldapmanagerdn"/></c:import>
            </td>
        </tr>

        <tr>
            <td colspan="5">
                <form:checkbox path="ldapAutoShadowing" id="ldapAutoShadowing" cssClass="checkbox"/>
                <label for="ldapAutoShadowing"><fmt:message key="advancedsettings.ldapautoshadowing"><fmt:param value="${command.brand}"/></fmt:message></label>
                <c:import url="helpToolTip.jsp"><c:param name="topic" value="ldapautoshadowing"/></c:import>
            </td>
        </tr>
    </table>

    <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
    <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">

</form:form>

<c:if test="${command.reloadNeeded}">
    <script language="javascript" type="text/javascript">
        parent.frames.left.location.href="left.view?";
        parent.frames.playQueue.location.href="playQueue.view?";
    </script>
</c:if>

<!-- CONTENT -->
</div>

</body>

<c:if test="${customScrollbar}">
<script type="text/javascript">    

		(function($){
			$(window).load(function(){
				$("#content_2").mCustomScrollbar({
					set_width:false, /*optional element width: boolean, pixels, percentage*/
					set_height:false, /*optional element height: boolean, pixels, percentage*/
					horizontalScroll:false, /*scroll horizontally: boolean*/
					scrollInertia:850, /*scrolling inertia: integer (milliseconds)*/
					mouseWheel:true, /*mousewheel support: boolean*/
					mouseWheelPixels:"auto", /*mousewheel pixels amount: integer, "auto"*/
					autoDraggerLength:true, /*auto-adjust scrollbar dragger length: boolean*/
					autoHideScrollbar:false, /*auto-hide scrollbar when idle*/
					scrollButtons:{ /*scroll buttons*/
						enable:true, /*scroll buttons support: boolean*/
						scrollType:"continuous", /*scroll buttons scrolling type: "continuous", "pixels"*/
						scrollSpeed:"auto", /*scroll buttons continuous scrolling speed: integer, "auto"*/
						scrollAmount:150 /*scroll buttons pixels scroll amount: integer (pixels)*/
					},
					advanced:{
						updateOnBrowserResize:true, /*update scrollbars on browser resize (for layouts based on percentages): boolean*/
						updateOnContentResize:false, /*auto-update scrollbars on content resize (for dynamic content): boolean*/
						autoExpandHorizontalScroll:false, /*auto-expand width for horizontal scrolling: boolean*/
						autoScrollOnFocus:true, /*auto-scroll on focused elements: boolean*/
						normalizeMouseWheelDelta:false /*normalize mouse-wheel delta (-1/1)*/
					},
					contentTouchScroll:true, /*scrolling by touch-swipe content: boolean*/
					callbacks:{
						onScrollStart:function(){}, /*user custom callback function on scroll start event*/
						onScroll:function(){}, /*user custom callback function on scroll event*/
						onTotalScroll:function(){}, /*user custom callback function on scroll end reached event*/
						onTotalScrollBack:function(){}, /*user custom callback function on scroll begin reached event*/
						onTotalScrollOffset:0, /*scroll end reached offset: integer (pixels)*/
						onTotalScrollBackOffset:0, /*scroll begin reached offset: integer (pixels)*/
						whileScrolling:function(){} /*user custom callback function on scrolling event*/
					},
					theme:"light" /*"light", "dark", "light-2", "dark-2", "light-thick", "dark-thick", "light-thin", "dark-thin"*/
				});
			});
		})(jQuery);

$(".content_main").resize(function(e){
	$(".content_main").mCustomScrollbar("update");
});
</script>
</c:if>	

</html>
