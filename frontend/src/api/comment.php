<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

$conn = mysqli_init();
mysqli_real_connect($conn, "127.0.0.1", "root", "password", "checkitoff", 3307);

if (mysqli_connect_errno()) {
  echo json_encode(["success" => false, "message" => "Database connection failed"]);
  exit();
}

if (isset($_GET["news_id"])) {
  $news_id = intval($_GET["news_id"]);
  $sql = "SELECT id, news_id, user_id, name, vote, comment, image_url, created_at 
          FROM votesAndcomments 
          WHERE news_id = $news_id 
          ORDER BY created_at DESC";
} else {
  $sql = "SELECT id, news_id, user_id, name, vote, comment, image_url, created_at 
          FROM votesAndcomments 
          ORDER BY created_at DESC";
}

$result = mysqli_query($conn, $sql);

$comments = [];
while ($row = mysqli_fetch_assoc($result)) {
  $comments[] = [
    "id" => $row["id"],
    "news_id" => $row["news_id"],
    "user_id" => $row["user_id"],
    "name" => $row["name"],
    "status" => $row["vote"] === "upvote" ? "Real" : "Fake",
    "comment" => $row["comment"],
    "imageUrl" => $row["image_url"],
    "created_at" => $row["created_at"]
  ];
}

echo json_encode([
  "success" => true,
  "count" => count($comments),
  "comments" => $comments
]);

mysqli_close($conn);
?>
