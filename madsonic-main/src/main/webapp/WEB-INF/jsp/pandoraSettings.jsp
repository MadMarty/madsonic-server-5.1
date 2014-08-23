<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jsp" %>
    <%@ include file="jquery.jsp" %>
	<link href="<c:url value="/style/customScrollbar.css"/>" rel="stylesheet">	
	
	 <script>
  $(function() {
    $( "#slider1" ).slider({
      value: ${model.similarAlbumtitle},
	  range: "max",
      min:0,
      max: 10,
      step: 1,
      slide: function( event, ui ) {
        $( "#similarAlbumtitle" ).val( ui.value );
      }
    });
    $( "#similarAlbumtitle" ).val( $( "#slider1" ).slider( "value" ) );
  });

  $(function() {
    $( "#slider2" ).slider({
      value: ${model.similarArtistsTopTrack},
	  range: "max",
      min: 0,
      max: 10,
      step: 1,
      slide: function( event, ui ) {
        $( "#similarArtistsTopTrack" ).val( ui.value );
      }
    });
    $( "#similarArtistsTopTrack" ).val( $( "#slider2" ).slider( "value" ) );
  });  
  
  
  $(function() {
    $( "#slider3" ).slider({
      value: ${model.similarArtists},
	  range: "max",
      min: 0,
      max: 10,
      step: 1,
      slide: function( event, ui ) {
        $( "#similarArtists" ).val( ui.value );
      }
    });
    $( "#similarArtists" ).val( $( "#slider3" ).slider( "value" ) );
  });

  $(function() {
    $( "#slider4" ).slider({
      value: ${model.similarGenre},
	  range: "max",
      min: 0,
      max: 20,
      step: 2,
      slide: function( event, ui ) {
        $( "#similarGenre" ).val( ui.value );
      }
    });
    $( "#similarGenre" ).val( $( "#slider4" ).slider( "value" ) );
  });

  $(function() {
    $( "#slider5" ).slider({
      value: ${model.similarMood},
	  range: "max",
      min: 0,
      max: 20,
      step: 2,
      slide: function( event, ui ) {
        $( "#similarMood" ).val( ui.value );
      }
    });
    $( "#similarMood" ).val( $( "#slider5" ).slider( "value" ) );
  });
  
  $(function() {
    $( "#slider6" ).slider({
      value: ${model.similarOther},
	  range: "max",
      min: 0,
      max: 20,
      step: 2,
      slide: function( event, ui ) {
        $( "#similarOther" ).val( ui.value );
      }
    });
    $( "#similarOther" ).val( $( "#slider6" ).slider( "value" ) );
  });
  </script>
  
</head>
<body class="mainframe bgcolor1">

<div id="content_2" class="content_main">
<!-- CONTENT -->

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="pandora"/>
    <c:param name="toast" value="${model.reload}"/>
</c:import>
<br>
<form method="post" action="pandoraSettings.view">
<table>
	<tr>
    <p class="detail" style="width:80%;white-space:normal">
        <fmt:message key="pandorasettings.description"/>
    </p>
    <p class="detail" style="width:80%;white-space:normal">
        <fmt:message key="pandorasettings.info"/>
    </p>
    <p class="detail" style="width:80%;white-space:normal">
	<br>
    </p>
	</tr>

	<tr>
		<td style="width:150px;"><label for="similarAlbumtitle">same Albumtitle</label></td>
		<td><input type="text" id="similarAlbumtitle" name="similarAlbumtitle" size="2" style="border: 0; color: #000000; font-weight: bold;text-align: center" /></td>
		<td><div id="slider1" style="width:300px;margin-left:10px;"></div></td>
	</tr>

	<tr>
		<td style="width:150px;"><label for="similarArtistsTopTrack">same Artists TopTrack</label></td>
		<td><input type="text" id="similarArtistsTopTrack" name="similarArtistsTopTrack" size="2" style="border: 0; color: #000000; font-weight: bold;text-align: center" /></td>
		<td><div id="slider2" style="width:300px;margin-left:10px;"></div></td>
	</tr>	
	
	<tr>
		<td style="width:150px;"><label for="similarArtists">same Artists</label></td>
		<td><input type="text" id="similarArtists" name="similarArtists" size="2" style="border: 0; color: #000000; font-weight: bold;text-align: center" /></td>
		<td><div id="slider3" style="width:300px;margin-left:10px;"></div></td>
	</tr>	

	<tr>
		<td style="width:150px;"><label for="similarGenre">similar Genre</label></td>
		<td><input type="text" id="similarGenre" name="similarGenre" size="2" style="border: 0; color: #000000; font-weight: bold;text-align: center" /></td>
		<td><div id="slider4" style="width:300px;margin-left:10px;"></div></td>
	</tr>

	<tr>
		<td style="width:150px;"><label for="similarMood">similar Moods</label></td>
		<td><input type="text" id="similarMood" name="similarMood" size="2" style="border: 0; color: #000000; font-weight: bold;text-align: center" /></td>
		<td><div id="slider5" style="width:300px;margin-left:10px;"></div></td>
	</tr>		

		<tr>
		<td style="width:150px;"><label for="similarOther">similar Artists</label></td>
		<td><input type="text" id="similarOther" name="similarOther" size="2" style="border: 0; color: #000000; font-weight: bold;text-align: center" /></td>
		<td><div id="slider6" style="width:300px;margin-left:10px;"></div></td>
	</tr>		
	
	    <tr>
        <td style="padding-top:1.5em" colspan="5">
            <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
            <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">
        </td>
    </tr>
</table>
</form>

<c:if test="${not empty model.error}">
    <p class="warning"><fmt:message key="${model.error}"/></p>
</c:if>

<c:if test="${model.reload}">
    <script language="javascript" type="text/javascript">parent.frames.playQueue.location.href="playQueue.view?"</script>
    <script language="javascript" type="text/javascript">parent.frames.main.location.href="pandoraSettings.view?"</script>
</c:if>
<!-- CONTENT -->
</div>
</body></html>