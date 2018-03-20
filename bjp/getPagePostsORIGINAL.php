<?php
	include  'connect_to_db.php';
	$page = 0;

	// source = 0 (facebook), 1 (youtube), 2 (others)
	$statement = $pdo->prepare("CREATE TABLE IF NOT EXISTS fb_posts (id INT AUTO_INCREMENT, message VARCHAR(2038), url VARCHAR(2038), image_link VARCHAR(2038), source_name VARCHAR(2038), source INT NOT NULL, PRIMARY KEY(id));");
	$statement->execute();
	$statement = $pdo->prepare("CREATE TABLE IF NOT EXISTS youtube_posts (id INT AUTO_INCREMENT, message VARCHAR(2038), url VARCHAR(2038), image_link VARCHAR(2038), source_name VARCHAR(2038), source INT NOT NULL, PRIMARY KEY(id));");
	$statement->execute();

	$appID = '938569129635366';
	$appSecret = '2cb32a485eea20753b74e1326aec75e2';
	//Create an access token using the APP ID and APP Secret.
	$accessToken = $appID . '|' . $appSecret;
	$sheets_url = "https://docs.google.com/spreadsheets/d/e/2PACX-1vT9hOpxCOKgYMPpZa70mZnBxoV2sLs6MZzczHehjmFCW9w-e4WUY8s17pyy1mgbkWgOP8qZCDLHhil5/pub?output=csv";

	function stripslashes_deep($value)
	{
	    $value = is_array($value) ?
	                array_map('stripslashes_deep', $value) :
	                stripslashes($value);
	    return $value;
	}

	if(!ini_set('default_socket_timeout', 15)) echo "<!-- unable to change socket timeout -->";
	if (($handle = fopen($sheets_url, "r")) !== FALSE) {
    	while (($data = fgetcsv($handle, ",")) !== FALSE) {
    		if (count($data)>3){
    	        $spreadsheet_data[] = stripslashes_deep($data[0]);
	        }
		}
	    fclose($handle);
	    for ($i = 0; $i < sizeof($spreadsheet_data); $i++){
	    	//The ID of the Facebook page in question.
			$id = $spreadsheet_data[$i];
			$attach = "https://graph.facebook.com/v2.5/$id/posts?fields=attachments,message,story&access_token=$accessToken";
			$attached = @file_get_contents($attach);
			$fbdata = json_decode($attached);
			foreach ($fbdata->data as $post)
			{
				if(!empty($post->message)){
					$msg = $post->message;
				}
				else{
				 $msg = ''; 
				}
				if (!empty($post->attachments)){
					foreach ($post->attachments as $value)
					{
						print_r($value[0]);
						// print_r($value[0]->media);
						if (!empty($value[0]->media)) {
							foreach ($value[0]->media as $medias)
							{
								$imgurl=$medias->src;
								// INSERT INTO fb_posts (message, url, image_link, source_name, source) SELECT * FROM (SELECT 'msg', 'value[0]->url', 'imgurl', '1', '0') AS tmp WHERE NOT EXISTS (SELECT url FROM fb_posts WHERE url='value[0]->url') LIMIT 1
								// $statement = $pdo->prepare("INSERT INTO fb_posts (message, url, image_link, source_name, source) SELECT * FROM (SELECT '$msg', '$value[0]->url', '$imgurl', '$id', '0') AS tmp WHERE NOT EXISTS (SELECT url FROM fb_posts WHERE url=".$value[0]->url.") LIMIT 1;");
								$statement = $pdo->prepare("INSERT INTO fb_posts (message, url, image_link, source_name, source) SELECT * FROM (SELECT ?, ?, ?, ?, ?) AS tmp WHERE NOT EXISTS (SELECT id FROM fb_posts WHERE message = ? AND source_name = ?) LIMIT 1;");
								// print_r([$msg, $value[0]->url, $imgurl, $id, 0, $value[0]->url]);
								$statement->execute([$msg, $value[0]->url, $imgurl, $id, 0, $msg, $id]);
								// [$msg, $value[0]->url, $imgurl, $id, 0]
								break;
							}
						}
					}
				}
			}
	    }
	}
?>