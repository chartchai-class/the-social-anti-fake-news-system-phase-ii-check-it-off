<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
include "connect.php";

if (!isset($_GET['email'])) {
    echo json_encode(["success" => false, "message" => "Missing email"]);
    exit;
}

$email = $_GET['email'];

$stmt = $conn->prepare("SELECT name, surname, role FROM users WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    $role = strtoupper($row["role"]);
    echo json_encode([
        "success" => true,
        "user" => [
            "name" => $row["name"],
            "surname" => $row["surname"],
            "access" => "ROLE_" . $role,
        ],
    ]);
}


$stmt->close();
$conn->close();
?>
