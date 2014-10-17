<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
	
	<script type="text/javascript">

	function playRandom() {
		var numsize = document.getElementsByName("size")[0].selectedIndex;
		var count = document.getElementsByName("size")[0].options[numsize].value;
		var numgenre = document.getElementsByName("genre")[0].selectedIndex;
		var genre = document.getElementsByName("genre")[0].options[numgenre].value;
		var nummood = document.getElementsByName("moods")[0].selectedIndex;
		var mood = document.getElementsByName("moods")[0].options[nummood].value;
		var numyear = document.getElementsByName("year")[0].selectedIndex;
		var year = document.getElementsByName("year")[0].options[numyear].value;
		var numfolderid = document.getElementsByName("musicFolderId")[0].selectedIndex;
		var musicFolderId = document.getElementsByName("musicFolderId")[0].options[numfolderid].value;
		
		parent.playQueue.onPlayRandomRadio(count, genre, mood, year, musicFolderId);
	}
	</script>	
</head>
<body class="mainframe bgcolor1">
<h1>
    <img id="pageimage" src="<spring:theme code="randomImage"/>" alt="" />
    <fmt:message key="more.random.title"/><span class="desc"></span>
</h1>
<br>
<c:if test="${model.user.streamRole}">
		<form>	
        <table>
            <tr>
                <td><fmt:message key="more.random.text"/></td>
                <td>
                    <select name="size">
                        <option value="1"><fmt:message key="more.random.song"><fmt:param value="1"/></fmt:message></option>					
                        <option value="3"><fmt:message key="more.random.songs"><fmt:param value="3"/></fmt:message></option>
                        <option value="5"><fmt:message key="more.random.songs"><fmt:param value="5"/></fmt:message></option>
                        <option value="8"><fmt:message key="more.random.songs"><fmt:param value="8"/></fmt:message></option>
                        <option value="10" selected="true"><fmt:message key="more.random.songs"><fmt:param value="10"/></fmt:message></option>
                        <option value="20"><fmt:message key="more.random.songs"><fmt:param value="20"/></fmt:message></option>
                        <option value="50"><fmt:message key="more.random.songs"><fmt:param value="50"/></fmt:message></option>
                            <option value="80"><fmt:message key="more.random.songs"><fmt:param value="80"/></fmt:message></option>
                            <option value="90"><fmt:message key="more.random.songs"><fmt:param value="90"/></fmt:message></option>
                            <option value="100"><fmt:message key="more.random.songs"><fmt:param value="100"/></fmt:message></option>
                            <option value="150"><fmt:message key="more.random.songs"><fmt:param value="150"/></fmt:message></option>
                            <option value="200"><fmt:message key="more.random.songs"><fmt:param value="200"/></fmt:message></option>
                            <option value="250"><fmt:message key="more.random.songs"><fmt:param value="250"/></fmt:message></option>
                            <option value="300"><fmt:message key="more.random.songs"><fmt:param value="300"/></fmt:message></option>
                            <option value="350"><fmt:message key="more.random.songs"><fmt:param value="350"/></fmt:message></option>
                    </select>
                </td>
            </tr>
            <tr>			
                <td><fmt:message key="more.random.genre"/></td>
                <td>
                    <select name="genre">
                        <option value="any"><fmt:message key="more.random.anygenre"/></option>
                        <c:forEach items="${model.genres}" var="genre">
                            <option value="${genre.name}"><str:truncateNicely upper="20">${genre.name} (${genre.songCount})</str:truncateNicely></option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>			
                <td><fmt:message key="more.random.moods"/></td>
                <td>
                    <select name="moods">
                        <option value="any"><fmt:message key="more.random.anymoods"/></option>
                        <c:forEach items="${model.moods}" var="moods">
                            <option value="${moods}"><str:truncateNicely upper="20">${moods}</str:truncateNicely></option>
                        </c:forEach>
                    </select>
                </td>
            </tr>			
            <tr>			
                <td><fmt:message key="more.random.year"/></td>
                <td>
                    <select name="year">
                        <option value="any"><fmt:message key="more.random.anyyear"/></option>

                        <c:forEach begin="0" end="${model.currentYear - 2006}" var="yearOffset">
                            <c:set var="year" value="${model.currentYear - yearOffset}"/>
                            <option value="${year} ${year}">${year}</option>
                        </c:forEach>

                        <option value="2005 2010">2005 &ndash; 2010</option>
                        <option value="2000 2005">2000 &ndash; 2005</option>
                        <option value="1990 2000">1990 &ndash; 2000</option>
                        <option value="1980 1990">1980 &ndash; 1990</option>
                        <option value="1970 1980">1970 &ndash; 1980</option>
                        <option value="1960 1970">1960 &ndash; 1970</option>
                        <option value="1950 1960">1950 &ndash; 1960</option>
                        <option value="0 1949">&lt; 1950</option>
                    </select>
                </td>
            </tr>
            <tr>			
                <td><fmt:message key="more.random.folder"/></td>
                <td>
                    <select name="musicFolderId">
                        <option value="-1"><fmt:message key="more.random.anyfolder"/></option>
                        <c:forEach items="${model.musicFolders}" var="musicFolder">
                                <option value="${musicFolder.id}">${musicFolder.name}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
			
            <tr>
			<td></td>
            </tr>
			
            <tr>	
                <td>
				</td>				
                <td>
					<input type="button" value="Play Random Radio!" onClick="playRandom();">
                </td>
            </tr>
        </table>
    </form>
</c:if>
</div>
</body>
</html>