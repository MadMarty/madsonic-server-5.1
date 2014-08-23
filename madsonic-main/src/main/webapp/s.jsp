<html>
<head>
<title>Madsonic Search</title>
<script type="text/javascript">
function gup( name )
{
  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regexS = "[\\?&]"+name+"=([^&#]*)";
  var regex = new RegExp( regexS );
  var url = unescape(window.location.href).replace(/\+/g," ");
  var results = regex.exec( url );
  if( results == null )
    return "";
  else
    return results[1];
}
function passpar() {
document.getElementById("query").value = gup('q');
document.forms["searchForm"].submit();
}
</script>
</head>
<body onload="passpar()">
<div style="display:none">
<form method="post" action="search.view" name="searchForm">
    <table>
      <tr>
      <td>
         <input type="text" name="query" id="query" size="30" value="">
      </td>
        <td>
         <input type="submit" id="submitform" onclick="passpar()">
      </td>
      </tr>
   </table>
</form>
</div>
</body>
</html>