<?php
	include  'connect_to_db.php';
    
    $post_id = $_POST["post_id"];
	$statement = $pdo->prepare("CREATE TABLE IF NOT EXISTS comments (id INT AUTO_INCREMENT, user_id INT NOT NULL, post_id INT NOT NULL, message VARCHAR(2038), PRIMARY KEY(id));");
    $statement->execute();
    $stmt = $pdo->prepare("SELECT C.id, C.user_name, C.message FROM (SELECT A.id, A.message, A.post_id, B.user_name FROM comments AS A INNER JOIN user_details AS B ON A.user_id = B.id WHERE post_id = ?) AS C");
    $stmt->execute([$post_id]);
    $data = $stmt->fetchAll(PDO::FETCH_ASSOC);
    $stmt->closeCursor();

    echo json_encode($data);
?>