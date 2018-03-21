<?php
	include  'connect_to_db.php';
    
    $user_id = $_POST["user_id"];
    $page_fb = $_POST["page_first_source"];
    $page_youtube = $_POST["page_second_source"];
    if ($page_fb == 0 && $page_youtube == 0) {
    	$sql = "SELECT A.*, B.counter from ((SELECT id, message, url, image_link, source_name, source, timedate from fb_posts ORDER BY id DESC LIMIT 10) union all (select * from youtube_posts ORDER BY id DESC LIMIT 2)) AS A LEFT JOIN count_action AS B ON A.id = B.post_id WHERE EXISTS (SELECT id FROM action WHERE user_id = ? AND post_id = A.id LIMIT 1);";
        $stmt = $pdo->prepare($sql);
        $stmt->execute([$user_id]);
    }else{
    	$sql = "SELECT A.*, B.counter from ((SELECT id, message, url, image_link, source_name, source, timedate from fb_posts WHERE id <= ? ORDER BY id DESC LIMIT 10) UNION ALL (SELECT id, message, url, image_link, source_name, source, timedate FROM youtube_posts WHERE id <= ? ORDER BY id DESC LIMIT 2)) AS A LEFT JOIN count_action AS B ON A.id = B.post_id WHERE EXISTS (SELECT id FROM action WHERE user_id = ? AND post_id = A.id LIMIT 1);";
        $stmt = $pdo->prepare($sql);
        $stmt->execute([$page_fb, $page_youtube, $user_id]);
    }
    $data = $stmt->fetchAll(PDO::FETCH_ASSOC);
    $stmt->closeCursor();

    echo json_encode($data);
?>