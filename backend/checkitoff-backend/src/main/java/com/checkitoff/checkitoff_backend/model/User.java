package com.checkitoff.checkitoff_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private String surname;
	private String email;
	private String password;
	private String role;
	
	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private Boolean visible = true;
	
}
