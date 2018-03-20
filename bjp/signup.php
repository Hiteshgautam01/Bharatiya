<?php
	include  'connect_to_db.php';

	$name = $_POST['user_name'];
	$user_id = $_POST['user_id'];
	$statement = $pdo->prepare("CREATE TABLE IF NOT EXISTS user_details (id INT AUTO_INCREMENT, user_name VARCHAR(255), user_id VARCHAR(255),PRIMARY KEY(user_id), KEY(id));");
	$statement->execute();

	$statement = $pdo->prepare("INSERT IGNORE INTO user_details (user_name, user_id) VALUES (?, ?);");
	$statement->execute([$name, $user_id]);

	$stmt = $pdo->prepare("SELECT id FROM user_details WHERE user_id = '$user_id' LIMIT 1;");
    $stmt->execute();
    $data = $stmt->fetch(PDO::FETCH_ASSOC);
    $stmt->closeCursor();
    echo $data['id'];

?>