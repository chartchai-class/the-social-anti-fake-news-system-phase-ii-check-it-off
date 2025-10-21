<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

// ✅ เชื่อม MySQL (ใน Docker)
$conn = mysqli_init();
mysqli_real_connect($conn, "127.0.0.1", "root", "password", "checkitoff", 3307);

// ✅ ตรวจสอบการเชื่อมต่อ
if (mysqli_connect_errno()) {
  die(json_encode(["error" => "Connection failed: " . mysqli_connect_error()]));
}

// ✅ Query ข่าวทั้งหมด
$sql = "SELECT * FROM news ORDER BY date DESC";
$result = mysqli_query($conn, $sql);

$news = [];
while ($row = mysqli_fetch_assoc($result)) {
  $news[] = $row;
}

echo json_encode(["news" => $news]);

mysqli_close($conn);
?>
