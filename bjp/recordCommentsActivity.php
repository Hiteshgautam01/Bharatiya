<?php
	include  'connect_to_db.php';
	// 1 = upvote, -1 = downvote 2 = comment
	$user_id = $_POST['user_id'];
	$message = $_POST['message'];
	$post_id = $_POST['post_id'];
	$statement = $pdo->prepare("CREATE TABLE IF NOT EXISTS comments (id INT AUTO_INCREMENT, user_id INT NOT NULL, post_id INT NOT NULL, message VARCHAR(2038), PRIMARY KEY(id));");
	$statement->execute();
	$statement = $pdo->prepare("INSERT INTO comments (user_id, post_id, message) VALUES (?, ?, ?)");
	$statement->execute([$user_id, $post_id, $message]);
	$stmt = $pdo->prepare("SELECT id FROM comments WHERE user_id = ? AND post_id = ? AND message = ? LIMIT 1;");
    $stmt->execute([$user_id, $post_id, $message]);
    $data = $stmt->fetch(PDO::FETCH_ASSOC);
    $stmt->closeCursor();
    echo $data['id'];
?>