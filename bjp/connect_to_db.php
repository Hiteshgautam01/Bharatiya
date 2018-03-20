<?php
	error_reporting(E_ALL);
	ini_set('display_errors', 'On');
	$dbhost = 'localhost';
	$dbuser = 'root';
	$dbpass = 'Desh@1996WAYNE';
	$db = 'bjp';

	// ---------x---------
	$dsn = 'mysql:host='.$dbhost.';dbname='.$db.';charset=utf8';
	$options = array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION);

// Now create the actual connection object and assign it to a variable
	try {
	   $pdo = new PDO($dsn, $dbuser, $dbpass, $options);
	} catch(PDOException $e) {
		echo ($e->getMessage());
	   exit;
	}
?>
