<?php
	include  'connect_to_db.php';
	// 1 = upvote, -1 = downvote 2 = comment
	$action = $_POST['actions'];
	$user_id = $_POST['user_id'];
	// $post_id = $_POST['post_id'];
	// message = n/a for likes
	// $message = $_POST['message'];
	$statement = $pdo->prepare("CREATE TABLE IF NOT EXISTS action (id INT AUTO_INCREMENT, action_id INT NOT NULL, user_id INT NOT NULL, post_id INT NOT NULL, PRIMARY KEY(id), UNIQUE unique_index (user_id, post_id));");
	$statement->execute();
	$statement = $pdo->prepare("CREATE TABLE IF NOT EXISTS count_action (post_id INT NOT NULL, counter INT NOT NULL, PRIMARY KEY(post_id), UNIQUE(post_id));");
	$statement->execute();
	$array = json_decode($action, true);
	foreach ($array as $value) {
		$action_id = $value["action_id"];
		$post_id = $value["post_id"];
		$statement = $pdo->prepare("INSERT INTO action (action_id, user_id, post_id) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE action_id = ?;");
		$statement->execute([$action_id, $user_id, $post_id, $action_id]);
		$statement = $pdo->prepare("INSERT INTO count_action (post_id, counter) VALUES (?, ?) ON DUPLICATE KEY UPDATE counter = counter + ?");
		$statement->execute([$post_id, $action_id, $action_id]);
	}
?>