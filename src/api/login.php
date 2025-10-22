<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");
header("Content-Type: application/json; charset=UTF-8");

$servername = "127.0.0.1:3307";
$username = "root";
$password = "password";
$dbname = "checkitoff";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
  die(json_encode(["success" => false, "message" => "Database connection failed."]));
}

$data = json_decode(file_get_contents("php://input"), true);

$email = $conn->real_escape_string($data["email"] ?? "");
$rawPassword = $data["password"] ?? "";

if (empty($email) || empty($rawPassword)) {
  echo json_encode(["success" => false, "message" => "Please enter both email and password."]);
  exit;
}

$sql = "SELECT * FROM users WHERE email='$email' LIMIT 1";
$result = $conn->query($sql);

if ($result->num_rows === 1) {
  $user = $result->fetch_assoc();

  if (password_verify($rawPassword, $user["password"])) {
    echo json_encode([
      "success" => true,
      "message" => "Login successful",
      "id" => $user["id"],
      "name" => $user["name"],
      "surname" => $user["surname"],
      "email" => $user["email"],
      "role" => $user["role"]
    ]);
  } else {
    echo json_encode(["success" => false, "message" => "Incorrect password."]);
  }
} else {
  echo json_encode(["success" => false, "message" => "Email not found."]);
}

$conn->close();
?>
