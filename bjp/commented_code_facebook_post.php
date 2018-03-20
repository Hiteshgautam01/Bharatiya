<!-- <?php
	$appID = '938569129635366';
	$appSecret = '2cb32a485eea20753b74e1326aec75e2';
	//Create an access token using the APP ID and APP Secret.
	$accessToken = $appID . '|' . $appSecret;
	$id = $_POST['id'];
	$attach = "https://graph.facebook.com/v2.5/$id/feed?fields=attachments,message,story&access_token=$accessToken";
	$attached = @file_get_contents($attach);
	$fbdata = json_decode($attached);
	foreach ($fbdata->data as $post )
	{
		echo json_encode($post);
		if(!empty($post->message)){
			$msg = $post->message;
		}
		else{
		 $msg = '';
		}
		if (isset($post->attachments)){
			foreach ($post->attachments as $value )
			{
				  foreach ($value[0]->media as $medias )
					{
						 $imgurl=$medias->src;
					}
					$output[] = [$value[0]->url, $msg, $imgurl];
					// $value[0]->url
					// $msg
					// $imgurl
			}
		}
	}
	// echo $output;
	// 938569129635366|2cb32a485eea20753b74e1326aec75e2
?> -->