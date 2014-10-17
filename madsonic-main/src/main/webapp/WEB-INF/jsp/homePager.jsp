<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

		<table>
		<tr>
		   <c:if test="${model.listType eq 'decade'}">
				<td style="padding-left: 0em">
					<fmt:message key="home.decade.text"/>
				</td>
				<td>
					<select name="decade" onchange="location='home.view?listType=${model.listType}&amp;listRows=${model.listRows}&amp;listColumns=${model.listColumns}&amp;decade=' + options[selectedIndex].value">
						<c:forEach items="${model.decades}" var="decade">
							<option
								${decade eq model.decade ? "selected" : ""} value="${decade}">${decade}</option>
						</c:forEach>
					</select>
				</td>
			</c:if>
			<c:if test="${model.listType eq 'genre'}">
				<td style="padding-left: 0em">
					<fmt:message key="home.genre.text"/>
				</td>
				<td>
					<select name="genre" onchange="location='home.view?listType=${model.listType}&amp;listRows=${model.listRows}&amp;listColumns=${model.listColumns}&amp;genre=' + options[selectedIndex].value">
						<c:forEach items="${model.genres}" var="genre">
							<option ${genre.name eq model.genre ? "selected" : ""} value="${genre.name}">${genre.name} (${genre.albumCount})</option>
						</c:forEach>
					</select>
				</td>
			</c:if>
			<td style="padding-left: 0em">
			<select name="listRows" id="listRows" class="inputWithIcon vcenter" onchange="location='home.view?listType=${model.listType}&amp;listColumns=${model.listColumns}&amp;listRows=' + options[selectedIndex].value;">
				<c:forTokens items="1 2 3 4 5 6 7 8 9 10" delims=" " var="listrows">
					<option ${listrows eq model.listRows ? "selected" : ""} value="${listrows}"><fmt:message key="home.listrows"><fmt:param value="${listrows}"/></fmt:message>${listrows gt 1 ? pluralizer : ""}</option>
				</c:forTokens>
			</select>
					
			<select name="listColumns" id="listColumns" class="inputWithIcon vcenter" onChange="location='home.view?listType=${model.listType}&amp;listRows=${model.listRows}&amp;listColumns=' + options[selectedIndex].value;">
				<c:forEach begin="1" end="10" var="listcolumns">
					<c:if test="${listcolumns gt 10}">
						<c:set var="listcolumns" value="${listcolumns}"/>
					</c:if>
					<option ${listcolumns eq model.listColumns ? "selected" : ""} value="${listcolumns}"><fmt:message key="home.listcolumns"><fmt:param value="${listcolumns}"/></fmt:message>${listcolumns gt 1 ? pluralizer : ""}</option>
				</c:forEach>
			</select> 
			</td>
			<td style="padding-left: 0em">
			<c:choose>
			<c:when test="${model.listType eq 'random'}">
				<td><div class="forwardright"><a href="home.view?listType=random&amp;listRows=${model.listRows}&amp;listColumns=${model.listColumns}"><fmt:message key="common.more"/></a></div></td>
			</c:when>
			
			<c:otherwise>
				<sub:url value="home.view" var="previousUrl">
					<sub:param name="listType" value="${model.listType}"/>
					<sub:param name="listRows" value="${model.listRows}"/>
					<sub:param name="listColumns" value="${model.listColumns}"/>
					<sub:param name="listOffset" value="${model.listOffset - model.listSize}"/>
					<sub:param name="genre" value="${model.genre}"/>
					<sub:param name="decade" value="${model.decade}"/>
				</sub:url>
				
				<sub:url value="home.view" var="nextUrl">
					<sub:param name="listType" value="${model.listType}"/>
					<sub:param name="listRows" value="${model.listRows}"/>
					<sub:param name="listColumns" value="${model.listColumns}"/>
					<sub:param name="listOffset" value="${model.listOffset + model.listSize}"/>
					<sub:param name="genre" value="${model.genre}"/>
					<sub:param name="decade" value="${model.decade}"/>					
				</sub:url>
				
				<c:if test="${model.listOffset gt 0}">
					<td style="padding-right:1.5em"><div class="back"><a href="${previousUrl}"><fmt:message key="common.previous"/></a></div></td>
                </c:if>

				<c:if test="${model.listType eq 'allArtist' || model.listType eq 'starredArtist' }"> 
					<td style="padding-right:1.5em"><fmt:message key="home.artists"><fmt:param value="${model.listOffset + 1}"/><fmt:param value="${model.listOffset + model.listSize}"/></fmt:message></td>
				</c:if>
	
				<c:if test="${model.listType ne 'allArtist' && model.listType ne 'starredArtist' }">
					<td style="padding-right:1.5em"><fmt:message key="home.albums"><fmt:param value="${model.listOffset + 1}"/><fmt:param value="${model.listOffset + model.listSize}"/></fmt:message></td>
				</c:if>
				
                <c:if test="${fn:length(model.albums) eq model.listSize}">
					<td><div class="forwardright"><a href="${nextUrl}"><fmt:message key="common.next"/></a></div></td>
                </c:if>
			</c:otherwise>
		</c:choose>

			</tr>
		</table>
