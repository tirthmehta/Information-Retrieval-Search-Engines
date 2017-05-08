<?php
// make sure browsers see this page as utf-8 encoded HTML
header('Content-Type: text/html; charset=utf-8');
include 'SpellCorrector.php';
ini_set('memory_limit','1024M');
ini_set("display_errors", 0);
$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$results = false;

if ($query)
{
  // The Apache Solr Client library should be on the include path
  // which is usually most easily accomplished by placing in the
  // same directory as this script ( . or current directory is a default
  // php include path entry in the php.ini)
  require_once('Apache/Solr/Service.php');
  // create a new solr service instance - host, port, and webapp
  // path (all defaults in this example)
  $solr = new Apache_Solr_Service('localhost', 8983, '/solr/myexample/');
  // if magic quotes is enabled then stripslashes will be needed
  if (get_magic_quotes_gpc() == 1)
  {
    $query = stripslashes($query);
  }

if (array_key_exists("pagerank", $_REQUEST)) {
        $additionalParameters = array(
        'sort' => 'pageRankFile desc',
        'facet' => 'true',        
        'facet.field' => array(
              'date',
              'author'
        )   
);
    }
    else{
      $additionalParameters = array(
        'facet' => 'true',
        'facet.field' => array(
              'date',
              'author'
        )       
);
    }

  try
  {
    $results = $solr->search($query, 0, $limit,$additionalParameters);
  }
  catch (Exception $e)
  {
    die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
  }
}
?>


<html>
  <head>
    <title>PHP Solr Client Example</title>
    <script>
    var x = document.getElementById("mySelect");
    x.onchange = function() {
    document.getElementById("q").value = x.value;
}
function showResult(str) {
  if (str.length==0) { 
    document.getElementById("livesearch").innerHTML="";
    document.getElementById("livesearch").style.border="0px";
    return;
  }
 
  if (window.XMLHttpRequest) {
    // code for IE7+, Firefox, Chrome, Opera, Safari
    xmlhttp=new XMLHttpRequest();
  } else {  // code for IE6, IE5
    xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
  }
  xmlhttp.onreadystatechange=function() {
    if (xmlhttp.readyState==4 && xmlhttp.status==200) {
    var suggestions=xmlhttp.responseText;
    var res = suggestions.split(",");
    var len=res.length;
    var text = "";
    document.getElementById("wordList").innerHTML = xmlhttp.responseText;
    }
  }
  xmlhttp.open("GET","a.php?q="+str,true);
  xmlhttp.send();   
}
</script>
<script>
function myFunction() {
    var x = document.getElementById("mySelect").value;
    document.getElementById("q").value = x;
}
</script>

<script>
function hideLine() {
    //window.location.reload(true);
    document.getElementById("meaning").style.visibility='hidden';
}
</script>

<meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
  </head>
  <body>

<div class="jumbotron" style="background-color: #98AEFF;">
  <h1 class="display-3" style="text-align: center;">Search Engine</h1>
  <p class="lead" style="text-align: center;">Enter your queries in the box provided</p>
  <hr class="my-4">

  <div style="text-align: center; ">


    <form  accept-charset="utf-8" method="get">
      <label for="q">Search:</label>
      <input id="q" name="q" type="text" list="wordList"onkeyup="showResult(this.value)" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/>
      <datalist id="wordList"></datalist>
      </br></br></br>
      <!--<input type="radio" name="pagerank" value="pg">Page Rank-->
     <div id="livesearch"></div>
     <!--<p id="demo"></p>-->
      <input type="submit"/>
    </form>
  </div>
</div>
<?php
if ($results)
{
  $total = (int) $results->response->numFound;
  $start = min(1, $total);
  $end = min($limit, $total);
?>
<?php
if ($total == 0) 
{
 ?>

 <?php
     $arr =  explode(" ", $query);
     $arr[0] = SpellCorrector::correct($arr[0]);
    $arr[1] = SpellCorrector::correct($arr[1]);
      $new_query = $arr[0]." ".$arr[1]; 


  if (get_magic_quotes_gpc() == 1)
  {
    $query = stripslashes($new_query);
  }

if (array_key_exists("pagerank", $_REQUEST)) {
        $additionalParameters = array(
        'sort' => 'pageRankFile desc',
        'facet' => 'true',        
        'facet.field' => array(
              'date',
              'author'
        )   
);
    }
    else{
      $additionalParameters = array(
        'facet' => 'true',
        'facet.field' => array(
              'date',
              'author'
        )       
);
    }

  try
  {
    $results = $solr->search($new_query, 0, $limit,$additionalParameters);
  }
  catch (Exception $e)
  {
    die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
  }

  $total = (int) $results->response->numFound;
  $start = min(1, $total);
  $end = min($limit, $total);
  $query = $new_query;

?>
<div id="meaning">
  <p>Did you mean?
  <a href="" onclick="hideLine()"><?php echo "$new_query"; ?> </a></p>
  </div>
<?php
} 
?>
    <div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>
    <ol>
<?php
 foreach ($results->response->docs as $doc)
{
    $html = file_get_contents($doc->og_url);
  
  libxml_use_internal_errors(true);
  $dom = new DOMDocument();
  $dom->loadHTML($html);
  $body="";

  foreach($dom->getElementsByTagName("body")->item(0)->childNodes as $child)
  {
    $body .= $dom->saveHTML($child);
  }
  $body = strip_tags($body,'<p><a><div><span><img><table><th><td><tr><li></td>');
  if(strpos($body,$query)!=false)
  {
      $first = strpos($body, $query);
      $second = explode($query, $body);     
      $count = 0;
      $snipper = "";
      foreach($second as $key => $value)
      {
        $count = $count + 1;
        if($count == 3)
        {
       
          $Snippet= $query . " ". $value . "\n";
        }
      }
  }
?>

<li> 
<table style="border: 1px solid black; text-align: left"> 
<tr> 
<th><?php echo htmlspecialchars($doc->field, ENT_NOQUOTES, 'utf-8'); ?></th> 
</tr>
<tr>
<td> <a href="<?php echo htmlspecialchars($doc->og_url, ENT_NOQUOTES, 'utf-8'); ?>"><?php echo htmlspecialchars($doc->title, ENT_NOQUOTES, 'utf-8'); ?> </a></td> 
</tr>
<tr>
<td><?php echo htmlspecialchars($doc->og_url, ENT_NOQUOTES, 'utf-8'); ?> </td>
</tr>

<tr>
<td>
  
<?php 

$lower = strtolower($doc->description);

if( strpos($lower, strtolower($query)) != false)
{
  $pos =  strpos($doc->description, $query);
  echo "..." . substr($doc->description, $pos) . "...";    
}
else if (strpos($doc->og_description, strtolower($query)) != false)
{
   $posog = strpos($doc->og_description, $query);
   echo "..." . substr($doc->og_description, $posog ) . "..." ;
}
else if (strpos(strtolower($doc->og_title), strtolower($query)) != false)
{
   $posTitle = strpos($doc->og_title, $query);
   echo "..." . substr($doc->description, $posTitle) . "...";
}
else if (strpos(strtolower($doc->title, $query), strtolower($query)) != false)
{
    $posTi = strpos($doc->title, $query);
    echo "..." . substr($doc->description, $posTi) . "...";
}
else if (strpos(strtolower($doc->dc_title, $query), strtolower($query)) != false)
{
    $posdc = strpos($doc->dc_title, $query);
    echo "..." . substr($doc->dc_title, $posdc) . "...";
}
else
{

  //echo $Snippet;
}
?>
</td>


</tr>
</table> 
</li> 
<?php 
} 
?> 
</ol> 
<?php
}
?>

  </body>
</html>