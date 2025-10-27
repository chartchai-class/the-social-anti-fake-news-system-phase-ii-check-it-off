package com.checkitoff.checkitoff_backend.controller;

import com.checkitoff.checkitoff_backend.model.User;
import com.checkitoff.checkitoff_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	@GetMapping("/roles")
	public ResponseEntity<?> getAllRoles() {
		List<Map<String, String>> roles = List.of(
				Map.of("role", "Admin"),
				Map.of("role", "Member"),
				Map.of("role", "Reader")
		);
		return ResponseEntity.ok(roles);
	}
	
	@GetMapping("/roles/all")
	public ResponseEntity<?> getAllUsersWithRoles() {
		try {
			List<Map<String, Object>> usersWithRoles = userRepository.findAll().stream()
					.map(u -> {
						Map<String, Object> userMap = new java.util.HashMap<>();
						userMap.put("id", u.getId());
						userMap.put("name", u.getName());
						userMap.put("surname", u.getSurname());
						userMap.put("email", u.getEmail());
						userMap.put("role", u.getRole() != null ? u.getRole() : "READER");
						userMap.put("visible", u.getVisible());
						return userMap;
					})
					.toList();
			
			return ResponseEntity.ok(usersWithRoles);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
		}
	}
	
	@PutMapping("/{id}/role")
	public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody Map<String, Object> body) {
		System.out.println("Received role update for user ID: " + id);
		System.out.println("New role: " + body.get("role"));
		
		try {
			User user = userRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("User not found"));
			
			String newRole = (String) body.get("role");
			
			if (newRole != null) {
				newRole = switch (newRole.trim().toLowerCase()) {
					case "admin" -> "ADMIN";
					case "member" -> "MEMBER";
					case "reader" -> "READER";
					default -> "READER";
				};
			}
			
			user.setRole(newRole.toUpperCase()); // ✅ เก็บเป็นตัวใหญ่ใน DB
			userRepository.save(user);
			
			return ResponseEntity.ok(Map.of("message", "Role updated to " + newRole));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
		}
	}
	
	// ✅ ซ่อน user (deactivate)
	@PutMapping("/hide/{id}")
	public ResponseEntity<?> hideUser(@PathVariable Long id) {
		try {
			User user = userRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("User not found"));
			
			user.setVisible(false);
			userRepository.save(user);
			
			return ResponseEntity.ok(Map.of("message", "User hidden successfully"));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
		}
	}
	
	@PutMapping("/show/{id}")
	public ResponseEntity<?> showUser(@PathVariable Long id) {
		try {
			User user = userRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("User not found"));
			
			user.setVisible(true);
			userRepository.save(user);
			
			return ResponseEntity.ok(Map.of("message", "User shown successfully"));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody Map<String, Object> body) {
		String email = (String) body.get("email");
		String password = (String) body.get("password");
		
		try {
			User user = userRepository.findByEmail(email);
			if (user == null) {
				return ResponseEntity.status(404).body(Map.of(
						"success", false,
						"message", "Email not found."
				));
			}
			
			// 🔐 ตรวจรหัสผ่าน (BCrypt)
			if (!BCrypt.checkpw(password, user.getPassword())) {
				return ResponseEntity.status(401).body(Map.of(
						"success", false,
						"message", "Incorrect password."
				));
			}
			
			// 🚫 ถ้า user ถูกซ่อน
			if (user.getVisible() != null && !user.getVisible()) {
				return ResponseEntity.status(403).body(Map.of(
						"success", false,
						"message", "⚠️ Your account is currently inactive. You cannot log in.",
						"visible", false
				));
			}
			
			// ✅ สำเร็จ
			return ResponseEntity.ok(Map.of(
					"success", true,
					"message", "Login successful",
					"id", user.getId(),
					"name", user.getName(),
					"surname", user.getSurname(),
					"email", user.getEmail(),
					"role", user.getRole(),
					"visible", user.getVisible()
			));
			
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(Map.of(
					"success", false,
					"message", e.getMessage()
			));
		}
	}
	
	@PostMapping("/create-user")
	public ResponseEntity<?> createUser(@RequestBody Map<String, Object> body) {
		try {
			String name = (String) body.get("name");
			String surname = (String) body.get("surname");
			String email = (String) body.get("email");
			String password = (String) body.get("password");
			Boolean visible = body.get("visible") != null ? (Boolean) body.get("visible") : true;
			
			if (email == null || email.isBlank() || password == null || password.isBlank()) {
				return ResponseEntity.badRequest().body(Map.of(
						"success", false,
						"message", "Email and password cannot be empty"
				));
			}
			
			// Trim และ lowercase email ก่อนตรวจ
			String trimmedEmail = email.trim().toLowerCase();
			
			// กำหนด role ตาม email
			String role;
			if (trimmedEmail.endsWith("@admin.expm")) {
				role = "ADMIN";
			} else {
				role = body.get("role") != null ? ((String) body.get("role")).toUpperCase() : "READER";
			}
			
			// เช็ค email ซ้ำ
			if (userRepository.findByEmail(email) != null) {
				return ResponseEntity.status(400).body(Map.of(
						"success", false,
						"message", "Email already exists"
				));
			}
			
			String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
			
			User newUser = new User();
			newUser.setName(name);
			newUser.setSurname(surname);
			newUser.setEmail(email.trim());
			newUser.setPassword(hashedPassword);
			newUser.setRole(role);
			newUser.setVisible(visible);
			
			userRepository.save(newUser);
			
			System.out.println("Created user: " + email + " with role: " + role); // Debug
			
			return ResponseEntity.ok(Map.of(
					"success", true,
					"message", "User registered successfully",
					"user", newUser
			));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(Map.of(
					"success", false,
					"message", e.getMessage()
			));
		}
	}
	
	
}
