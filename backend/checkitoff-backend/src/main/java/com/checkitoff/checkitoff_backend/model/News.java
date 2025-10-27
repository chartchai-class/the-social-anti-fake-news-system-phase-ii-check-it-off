package com.checkitoff.checkitoff_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "news")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class News {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String title;
	private String category;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@Column(name = "fulldescription", columnDefinition = "LONGTEXT")
	private String fullDescription;
	
	private String author;
	private String date;
	private String image;
	
	private Integer upVotes = 0;
	private Integer downVotes = 0;
	private Integer commentsCount = 0;
	
	private Boolean visible = true;
}
