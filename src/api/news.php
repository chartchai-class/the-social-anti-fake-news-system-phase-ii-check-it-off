<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

$conn = mysqli_init();
mysqli_real_connect($conn, "127.0.0.1", "root", "password", "checkitoff", 3307);

if (mysqli_connect_errno()) {
  die(json_encode(["error" => "Connection failed: " . mysqli_connect_error()]));
}

$sql = "SELECT * FROM news ORDER BY date DESC";
$result = mysqli_query($conn, $sql);

$news = [];
while ($row = mysqli_fetch_assoc($result)) {
  $news[] = $row;
}

echo json_encode(["news" => $news]);

mysqli_close($conn);
?>
