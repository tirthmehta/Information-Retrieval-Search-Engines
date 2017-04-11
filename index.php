<?php

// make sure browsers see this page as utf-8 encoded HTML
header('Content-Type: text/html; charset=utf-8');

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

  // in production code you'll always want to use a try /catch for any
  // possible exceptions emitted  by searching (i.e. connection
  // problems or a query parsing error)
try
  { 
    $radiobuttonresult=$_GET['check'];
if(isset($_GET['check']) && $radiobuttonresult=='pagerank'){
     $order='pageRankFile desc';
  }
  else{
     $order='score desc';
 }

$newPar=array('sort'=>$order);
 
  $results=$solr->search($query, 0, $limit, $newPar);
  }
  catch (Exception $e)
  {
    // in production you'd probably log or email this error to an admin
    // and then show a special message to the user but for this example
    // we're going to show the full exception
    die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
  }
}

?>
<html> 
<head> 
<title>PHP Solr Client Example</title> 
<meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
</head> 
<body> 

<!-- Jumbotron starts -->
<div class="jumbotron">
  <h1 class="display-3" style="text-align: center;">Search Engine</h1>
  <p class="lead" style="text-align: center;">Enter your queries in the box provided</p>
  <hr class="my-4">
  <p style="text-align: center;">Select a button below</p>
  <div style="text-align: center;">
  <form accept-charset="utf-8" method="get" > 
<label for="q">Search:</label> 
<input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/>
<input type="submit"/>
<br>
<br>
   <input type="radio" name="check" value="lucene" checked="checked"> Solr</input>
  <input type="radio" name="check" value="pagerank"> PageRank</input>
    </form>

<?php
if ($results)
{
  $total = (int) $results->response->numFound;
  $start = min(1, $total);
  $end = min($limit, $total);
?>
    <div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>
<?php
  foreach ($results->response->docs as $doc)
  {
  
$row = 1;
if (($handle = fopen("mapLATimesDataFile.csv", "r")) !== FALSE)
{
    while (($data = fgetcsv($handle, ",")) !== FALSE)
    {
        $num = count($data);
        $row++;
  $idss=explode('/', $doc->id);
  for ($c=0; $c < $num; $c++) 
  {
  if(strpos($data[$c], $idss[6]) !== false)
  {
    $c++;         
    $differentvals=explode("\r",$data[$c]);
  }
  }
  }
    fclose($handle);
}

?>

<table >

<tr>
  <table>      
  <tr><td style="font-size:20px";> <a href='<?php echo htmlspecialchars($differentvals[0], ENT_NOQUOTES, 'utf-8'); ?>'> <?php echo htmlspecialchars($doc->title, ENT_NOQUOTES, 'utf-8'); ?> </a> </td></tr>
  <tr><td><a href='<?php echo htmlspecialchars($differentvals[0], ENT_NOQUOTES, 'utf-8'); ?>'> <?php echo htmlspecialchars($differentvals[0], ENT_NOQUOTES, 'utf-8'); ?> </a> </td></tr>
  <tr><td><?php echo htmlspecialchars($doc->id, ENT_NOQUOTES, 'utf-8'); ?></td></tr>
  <tr><td><?php echo htmlspecialchars($doc->description, ENT_NOQUOTES, 'utf-8'); ?></td></tr>
  <br/>
  </table> 
</tr>

        </table>
<?php
  }
?>
<?php
}
?>
  </body>
</html>
