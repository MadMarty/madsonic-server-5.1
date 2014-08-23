<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%--@elvariable id="command" type="org.madsonic.command.UserSettingsCommand"--%>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>

	<c:if test="${customScrollbar}">
		<link href="<c:url value="/style/customScrollbar.css"/>" rel="stylesheet">
		<script type="text/javascript" src="<c:url value="/script/jquery.mousewheel.min.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/script/jquery.mCustomScrollbar.js"/>"></script>
	</c:if>	
</head>
<body class="mainframe bgcolor1" onload="enablePasswordChangeFields();">

<div id="content_2" class="content_main">
<!-- CONTENT -->

<script type="text/javascript" src="<c:url value="/script/wz_tooltip.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/tip_balloon.js"/>"></script>

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="user"/>
    <c:param name="toast" value="${command.toast}"/>
</c:import>
<br>
<script type="text/javascript" language="javascript">
    function enablePasswordChangeFields() {
        var changePasswordCheckbox = $("#passwordChange");
        var ldapCheckbox = $("#ldapAuthenticated");
        var passwordChangeTable = $("#passwordChangeTable");
        var passwordChangeCheckboxTable = $("#passwordChangeCheckboxTable");

        if (changePasswordCheckbox && changePasswordCheckbox.is(":checked") && (ldapCheckbox == null || !ldapCheckbox.is(":checked"))) {
            passwordChangeTable.show();
        } else {
            passwordChangeTable.hide();
        }

        if (changePasswordCheckbox) {
            if (ldapCheckbox && ldapCheckbox.is(":checked")) {
                passwordChangeCheckboxTable.hide();
            } else {
                passwordChangeCheckboxTable.show();
            }
        }
    }
</script>

<table class="indent">
    <tr>
        <td style="width:120;"><b>Select Task</b></td>
        <td>
            <select name="action" style="width:200px;" onchange="location='userSettings.view?usrAct=' + ( value);">
                <option value="edit">-- edit User --</option>
                <option value="new" ${command.newUser eq true ? "selected" : ""}>-- new User --</option>
				<option value="clone" ${command.newClone eq true ? "selected" : ""}>-- clone User --</option> 
            </select>
        </td>
		<td><c:import url="helpToolTip.jsp"><c:param name="topic" value="editUser"/></c:import></td>
    </tr>

    <tr>
        <td><b></b></td>
        <td>
		<c:if test="${command.newUser ne true and command.newClone ne true}">
            <select name="username"  style="width:200px;" onchange="location='userSettings.view?userIndex=' + (selectedIndex - 1 ) + '&usrAct=edit';">
				<option value="" selected>-- select User --</option>
                <c:forEach items="${command.users}" var="user">
                    <option ${user.username eq command.username ? "selected" : ""}
                            value="${user.username}">${user.username}</option>
                </c:forEach>
            </select>
		</c:if>
        </td>
		<td>
		<c:if test="${command.newUser ne true and command.newClone ne true}">
			<c:import url="helpToolTip.jsp"><c:param name="topic" value="selectUser"/></c:import>
		</c:if>
		</td>
    </tr>
	
    <tr>
        <td><b> </b></td>
        <td>
		<c:if test="${command.newUser ne true and command.newClone ne true}">
			<c:if test="${command.username ne null}">

			<sub:url value="profileSettings.view" var="editSettingsUrl">
				<sub:param name="profile" value="${command.username}"/>
			</sub:url>

				<div class="forward" style="float:left">
					<a href="${editSettingsUrl}">edit Usersettings</a>
				</div>
			</c:if>
		</c:if>
        </td>
    </tr>	
</table>
<p/>
<c:if test="${command.username ne null or command.newUser eq true}">
	
<form:form method="post" action="userSettings.view" commandName="command">
    <c:if test="${not command.admin}">
        <table>
            <tr>
                <td><form:checkbox path="locked" id="locked" cssClass="checkbox"/></td>
                <td><label for="locked"><fmt:message key="usersettings.locked"/></label></td>
            </tr>
		
            <tr>
                <td><form:checkbox path="adminRole" id="admin" cssClass="checkbox"/></td>
                <td><label for="admin"><fmt:message key="usersettings.admin"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="settingsRole" id="settings" cssClass="checkbox"/></td>
                <td><label for="settings"><fmt:message key="usersettings.settings"/></label></td>
            </tr>
            <tr>
                <td style="padding-top:1em"><form:checkbox path="streamRole" id="stream" cssClass="checkbox"/></td>
                <td style="padding-top:1em"><label for="stream"><fmt:message key="usersettings.stream"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="searchRole" id="search" cssClass="checkbox"/></td>
                <td><label for="search"><fmt:message key="usersettings.search"/></label></td>
            </tr>			
            <tr>
                <td><form:checkbox path="jukeboxRole" id="jukebox" cssClass="checkbox"/></td>
                <td><label for="jukebox"><fmt:message key="usersettings.jukebox"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="downloadRole" id="download" cssClass="checkbox"/></td>
                <td><label for="download"><fmt:message key="usersettings.download"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="uploadRole" id="upload" cssClass="checkbox"/></td>
                <td><label for="upload"><fmt:message key="usersettings.upload"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="shareRole" id="share" cssClass="checkbox"/></td>
                <td><label for="share"><fmt:message key="usersettings.share"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="coverArtRole" id="coverArt" cssClass="checkbox"/></td>
                <td><label for="coverArt"><fmt:message key="usersettings.coverart"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="commentRole" id="comment" cssClass="checkbox"/></td>
                <td><label for="comment"><fmt:message key="usersettings.comment"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="podcastRole" id="podcast" cssClass="checkbox"/></td>
                <td><label for="podcast"><fmt:message key="usersettings.podcast"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="lastFMRole" id="lastfm" cssClass="checkbox"/></td>
                <td><label for="podcast"><fmt:message key="usersettings.lastfm"/></label></td>
            </tr>
		</table>
    </c:if>

    <c:if test="${not command.admin}">
    <table class="indent">
		<tr>
            <td style="width:120;">User Level </td>
			<td><form:select path="groupId" id="groupId" size="1">
			<c:forEach items="${command.groups}" var="group">
				<form:option value="${group.id}" label="${group.name}"/>
			</c:forEach>
			</form:select>
			</td>
		    <td><c:import url="helpToolTip.jsp"><c:param name="topic" value="access"/></c:import></td>
			</tr>
    </table>
    </c:if>
	 
    <table class="indent">
        <tr>
            <td style="width:120;"><fmt:message key="playersettings.maxbitrate"/></td>
            <td>
                <form:select path="transcodeSchemeName" cssStyle="width:8em">
                    <c:forEach items="${command.transcodeSchemeHolders}" var="transcodeSchemeHolder">
                        <form:option value="${transcodeSchemeHolder.name}" label="${transcodeSchemeHolder.description}"/>
                    </c:forEach>
                </form:select>
            </td>
            <td><c:import url="helpToolTip.jsp"><c:param name="topic" value="transcode"/></c:import></td>
            <c:if test="${not command.transcodingSupported}">
                <td class="warning"><fmt:message key="playersettings.nolame"/></td>
            </c:if>
        </tr>
    </table>

    <c:if test="${command.ldapEnabled and not command.admin}">
        <table>
            <tr>
                <td><form:checkbox path="ldapAuthenticated" id="ldapAuthenticated" cssClass="checkbox" onclick="javascript:enablePasswordChangeFields()"/></td>
                <td><label for="ldapAuthenticated"><fmt:message key="usersettings.ldap"/></label></td>
                <td><c:import url="helpToolTip.jsp"><c:param name="topic" value="ldap"/></c:import></td>
            </tr>
        </table>
    </c:if>

    <c:choose>
        <c:when test="${command.newUser}">

            <table class="indent">
                <tr>
                    <td style="width:120;"><fmt:message key="usersettings.username"/></td>
                    <td><form:input path="username" size="32"/></td>
                    <td class="warning"><form:errors path="username"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="usersettings.email"/></td>
                    <td><form:input path="email" size="32"/></td>
                    <td class="warning"><form:errors path="email"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="usersettings.password"/></td>
                    <td><form:password path="password" size="32"/></td>
                    <td class="warning"><form:errors path="password"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="usersettings.confirmpassword"/></td>
                    <td><form:password path="confirmPassword" size="32"/></td>
                    <td/>
                </tr>
                <tr>
                    <td style="width:120;">comment</td>
                    <td><form:input path="comment" size="64"/></td>
                    <td class="warning"><form:errors path="comment"/></td>
                </tr>				
            </table>
        </c:when>

        <c:otherwise>

			<table>
                <tr>
                    <td style="width:120;"><fmt:message key="usersettings.email"/></td>
                    <td><form:input path="email" size="32"/></td>
                    <td class="warning"><form:errors path="email"/></td>
                </tr>
            </table>
			
			<table>
                <tr>
                    <td style="width:120;">comment</td>
                    <td><form:input path="comment" size="64"/></td>
                    <td class="warning"><form:errors path="comment"/></td>
                </tr>
            </table>
		
            <table id="passwordChangeCheckboxTable">
                <tr>
                    <td><form:checkbox path="passwordChange" id="passwordChange" onclick="enablePasswordChangeFields();" cssClass="checkbox"/></td>
                    <td><label for="passwordChange"><fmt:message key="usersettings.changepassword"/></label></td>
                </tr>
            </table>

            <table id="passwordChangeTable" style="display:none">
                <tr>
                    <td style="width:120;"><fmt:message key="usersettings.newpassword"/></td>
                    <td><form:password path="password" id="path" size="32"/></td>
                    <td class="warning"><form:errors path="password"/></td>
                </tr>
                <tr>
                    <td style="width:120;"><fmt:message key="usersettings.confirmpassword"/></td>
                    <td><form:password path="confirmPassword" id="confirmPassword" size="32"/></td>
                    <td/>
                </tr>
            </table>

        </c:otherwise>
    </c:choose>

	<c:if test="${not command.newUser and not command.admin}">
        <table class="indent">
            <tr>
                <td><form:checkbox path="delete" id="delete" cssClass="checkbox"/></td>
                <td><label for="delete"><fmt:message key="usersettings.delete"/></label></td>
            </tr>
        </table>
	</c:if>
	
    <input type="submit" value="<fmt:message key="common.save"/>" style="margin-top:1.5em;margin-right:0.3em">
    <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'" style="margin-top:1.5em">
</form:form>

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
