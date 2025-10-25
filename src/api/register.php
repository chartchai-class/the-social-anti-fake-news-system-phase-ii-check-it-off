<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Authorization");
header("Content-Type: application/json; charset=UTF-8");

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
  http_response_code(200);
  exit();
}

$servername = "127.0.0.1:3307";
$username = "root";
$password = "password";
$dbname = "checkitoff";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
  die(json_encode(["success" => false, "message" => "Database connection failed."]));
}

$data = json_decode(file_get_contents("php://input"), true);

$name = $conn->real_escape_string($data["name"] ?? "");
$surname = $conn->real_escape_string($data["surname"] ?? "");
$email = $conn->real_escape_string($data["email"] ?? "");
$rawPassword = $conn->real_escape_string($data["password"] ?? "");

if (empty($name) || empty($surname) || empty($email) || empty($rawPassword)) {
  echo json_encode(["success" => false, "message" => "Please fill in all fields."]);
  exit;
}

$check = $conn->query("SELECT * FROM users WHERE email='$email'");
if ($check->num_rows > 0) {
  echo json_encode(["success" => false, "message" => "Email already exists."]);
  exit;
}

if (preg_match("/@admin\.ornor$/i", $email)) {
  $role = "ADMIN&FORNOR";
} else {
  $role = "READER";
}

$hashedPassword = password_hash($rawPassword, PASSWORD_BCRYPT);

$sql = "INSERT INTO users (name, surname, email, password, role)
        VALUES ('$name', '$surname', '$email', '$hashedPassword', '$role')";

if ($conn->query($sql) === TRUE) {
  echo json_encode([
    "success" => true,
    "message" => "User registered successfully",
    "role" => $role
  ]);
} else {
  echo json_encode(["success" => false, "message" => "DB Error: " . $conn->error]);
}

$conn->close();
?>
