<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

$conn = mysqli_init();
mysqli_real_connect($conn, "127.0.0.1", "root", "password", "checkitoff", 3307);

if (mysqli_connect_errno()) {
  echo json_encode(["success" => false, "message" => "Database connection failed"]);
  exit();
}

$data = json_decode(file_get_contents("php://input"), true);

if (
  !isset($data["news_id"]) ||
  !isset($data["user_id"]) ||
  !isset($data["vote"]) ||
  !isset($data["comment"]) ||
  !isset($data["name"])
) {
  echo json_encode(["success" => false, "message" => "Missing required fields"]);
  exit();
}

$news_id = intval($data["news_id"]);
$user_id = intval($data["user_id"]);
$name = $data["name"];
$vote = $data["vote"];
$comment = $data["comment"];

$sql = "INSERT INTO votesAndcomments (news_id, user_id, name, vote, comment)
        VALUES (?, ?, ?, ?, ?)";

$stmt = mysqli_prepare($conn, $sql);
mysqli_stmt_bind_param($stmt, "iisss", $news_id, $user_id, $name, $vote, $comment);

if (mysqli_stmt_execute($stmt)) {
  echo json_encode(["success" => true, "message" => "Vote recorded successfully"]);
} else {
  echo json_encode(["success" => false, "message" => "Failed to insert vote"]);
}

mysqli_stmt_close($stmt);
mysqli_close($conn);
?>
