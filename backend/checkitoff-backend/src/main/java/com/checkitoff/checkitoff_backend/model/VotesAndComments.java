package com.checkitoff.checkitoff_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "votesandcomments")
public class VotesAndComments {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "news_id", nullable = false)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private News news;
	
	@Column(name = "user_id")
	private Integer userId;
	
	private String name;
	
	@Enumerated(EnumType.STRING)
	private VoteType vote;
	
	@Column(name = "up_votes", nullable = false)
	private Integer upVotes = 0;
	
	@Column(name = "down_votes", nullable = false)
	private Integer downVotes = 0;
	
	private String comment;
	
	@Column(name = "image_url")
	private String imageUrl;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private java.sql.Timestamp createdAt;
	
	@Column(name = "is_visible", nullable = false)
	private Boolean isVisible = true;
	
	public enum VoteType {
		upvote, downvote
	}
	
	public boolean isVisible() {
		return isVisible;
	}
	
	public void setVisible(boolean visible) {
		this.isVisible = visible;
	}
}
