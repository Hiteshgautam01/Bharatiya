<?php
	include  'connect_to_db.php';
	$page = 0;

	// source = 0 (facebook), 1 (youtube), 2 (others)
	$statement = $pdo->prepare("CREATE TABLE IF NOT EXISTS fb_posts (id INT AUTO_INCREMENT, message VARCHAR(2038), url VARCHAR(2038), image_link VARCHAR(2038), source_name VARCHAR(2038), source INT NOT NULL, timedate VARCHAR(255) NOT NULL, fb_id VARCHAR(255) NOT NULL, PRIMARY KEY(id), KEY(fb_id)) DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;");
	$statement->execute();
	$statement = $pdo->prepare("CREATE TABLE IF NOT EXISTS comments (id INT AUTO_INCREMENT, user_id INT NOT NULL, post_id INT NOT NULL, message VARCHAR(2038), PRIMARY KEY(id));");
    $statement->execute();
	$statement = $pdo->prepare("CREATE TABLE IF NOT EXISTS youtube_posts (id INT AUTO_INCREMENT, message VARCHAR(2038), url VARCHAR(2038), image_link VARCHAR(2038), source_name VARCHAR(2038), source INT NOT NULL, timedate VARCHAR(255) NOT NULL ,PRIMARY KEY(id));");
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
			// full_picture
			$attach = "https://graph.facebook.com/v2.5/$id/posts?fields=full_picture,permalink_url,message,story,created_time&limit=2&access_token=$accessToken";
			$attached = @file_get_contents($attach);
			$fbdata = json_decode($attached, true);
			foreach ((array)$fbdata['data'] as $post)
			{
				if (!empty($post["permalink_url"]) && !empty($post["created_time"]) && !empty($post["id"])){
					if(!empty($post["message"])){
						$msg = $post["message"];
					}else if (!empty($post["story"])){
						$msg = $post["story"];
					}else{
					 $msg = ''; 
					}
					$url = $post["permalink_url"];
					$created_time = $post["created_time"];
					$fb_id = $post["id"];
					if (!empty($post["full_picture"])){
						$imgurl=$post["full_picture"];
					}else{
						$imgurl="";
					}
					// INSERT INTO fb_posts (message, url, image_link, source_name, source) SELECT * FROM (SELECT 'msg', 'value[0]->url', 'imgurl', '1', '0') AS tmp WHERE NOT EXISTS (SELECT url FROM fb_posts WHERE url='value[0]->url') LIMIT 1
					// $statement = $pdo->prepare("INSERT INTO fb_posts (message, url, image_link, source_name, source) SELECT * FROM (SELECT '$msg', '$value[0]->url', '$imgurl', '$id', '0') AS tmp WHERE NOT EXISTS (SELECT url FROM fb_posts WHERE url=".$value[0]->url.") LIMIT 1;");
					// $statement = $pdo->prepare("INSERT INTO fb_posts (message, url, image_link, source_name, source, timedate, fb_id) SELECT * FROM (SELECT ?, ?, ?, ?, ?, ?, ?) AS tmp WHERE NOT EXISTS (SELECT id FROM fb_posts WHERE fb_id = ?) LIMIT 1;");
					// print_r([$msg, $value[0]->url, $imgurl, $id, 0, $value[0]->url]);
					// try{
					// 	$statement->execute([$msg, $url, $imgurl, $id, 0, $created_time, $fb_id, $fb_id]);
					// }catch (PDOException $e){
					// 	echo ($e->getMessage());
					// }
					// if (!empty($post['attachments'])){
					// 	foreach ($post->attachments as $value)
					// 	{
							// print_r($value[0]);
							// print_r($value[0]->media);
							// if (!empty($value[0]->media)) {
							// 	foreach ($value[0]->media as $medias)
							// 	{
							// 		$imgurl=$medias->src;
									// INSERT INTO fb_posts (message, url, image_link, source_name, source) SELECT * FROM (SELECT 'msg', 'value[0]->url', 'imgurl', '1', '0') AS tmp WHERE NOT EXISTS (SELECT url FROM fb_posts WHERE url='value[0]->url') LIMIT 1
									// $statement = $pdo->prepare("INSERT INTO fb_posts (message, url, image_link, source_name, source) SELECT * FROM (SELECT '$msg', '$value[0]->url', '$imgurl', '$id', '0') AS tmp WHERE NOT EXISTS (SELECT url FROM fb_posts WHERE url=".$value[0]->url.") LIMIT 1;");
									// $statement = $pdo->prepare("INSERT INTO fb_posts (message, url, image_link, source_name, source) SELECT * FROM (SELECT ?, ?, ?, ?, ?) AS tmp WHERE NOT EXISTS (SELECT id FROM fb_posts WHERE message = ? AND source_name = ?) LIMIT 1;");
									$statement = $pdo->prepare("INSERT INTO fb_posts (message, url, image_link, source_name, source, timedate, fb_id) SELECT * FROM (SELECT ?, ?, ?, ?, ?, ?, ?) AS tmp WHERE NOT EXISTS (SELECT id FROM fb_posts WHERE fb_id = ?) LIMIT 1;");
									try{
										$statement->execute([$msg, $url, $imgurl, $id, 0, $created_time, $fb_id, $fb_id]);
									}catch (PDOException $e){
										// echo ($e->getMessage());
									}
									// [$msg, $value[0]->url, $imgurl, $id, 0]
									// break;
								// }
							// }
						// }
					// }
				}
			}
	    }
	}
	// if(!ini_set('default_socket_timeout', 15)) echo "<!-- unable to change socket timeout -->";
	// if (($handle = fopen($sheets_url, "r")) !== FALSE) {
 //    	while (($data = fgetcsv($handle, ",")) !== FALSE) {
 //    		if (count($data)>3){
 //    	        $spreadsheet_data[] = stripslashes_deep($data[0]);
	//         }
	// 	}
	//     fclose($handle);
	//     for ($i = 0; $i < sizeof($spreadsheet_data); $i++){
	//     	//The ID of the Facebook page in question.
	// 		$id = $spreadsheet_data[$i];
	// 		$attach = "https://graph.facebook.com/v2.5/$id/posts?fields=permalink_url,picture,message,story,created_time&limit=5&access_token=$accessToken";
	// 		// $attach = "https://graph.facebook.com/v2.5/$id/posts?fields=attachments,message,story,created_time&access_token=$accessToken";
	// 		$attached = @file_get_contents($attach);
	// 		$fbdata = json_decode($attached, true);
	// 		// print_r($fbdata);
	// 		foreach ((array)$fbdata["data"] as $post)
	// 		{
	// 			if (!empty($post["permalink_url"]) && !empty($post["created_time"]) && !empty($post["id"])){
	// 				if(!empty($post["message"])){
	// 					$msg = $post["message"];
	// 				}else if (!empty($post["story"])){
	// 					$msg = $post["story"];
	// 				}else{
	// 				 $msg = ''; 
	// 				}
	// 				if (!empty($post["picture"])){
	// 					$imgurl=$post["picture"];
	// 				}else{
	// 					$imgurl="";

	// 				}
	// 				$url = $post["permalink_url"];
	// 				$created_time = $post["created_time"];
	// 				$fb_id = $post["id"];
	// 				// INSERT INTO fb_posts (message, url, image_link, source_name, source) SELECT * FROM (SELECT 'msg', 'value[0]->url', 'imgurl', '1', '0') AS tmp WHERE NOT EXISTS (SELECT url FROM fb_posts WHERE url='value[0]->url') LIMIT 1
	// 				// $statement = $pdo->prepare("INSERT INTO fb_posts (message, url, image_link, source_name, source) SELECT * FROM (SELECT '$msg', '$value[0]->url', '$imgurl', '$id', '0') AS tmp WHERE NOT EXISTS (SELECT url FROM fb_posts WHERE url=".$value[0]->url.") LIMIT 1;");
	// 				$statement = $pdo->prepare("INSERT INTO fb_posts (message, url, image_link, source_name, source, timedate, fb_id) SELECT * FROM (SELECT ?, ?, ?, ?, ?, ?, ?) AS tmp WHERE NOT EXISTS (SELECT id FROM fb_posts WHERE fb_id = ?) LIMIT 1;");
	// 				// print_r([$msg, $value[0]->url, $imgurl, $id, 0, $value[0]->url]);
	// 				try{
	// 					$statement->execute([$msg, $url, $imgurl, $id, 0, $created_time, $fb_id, $fb_id]);
	// 				}catch (PDOException $e){
	// 					echo ($e->getMessage());
	// 				}
	// 			}
	// 		}
	//     }
	// }
?>