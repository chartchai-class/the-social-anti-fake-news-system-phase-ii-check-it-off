package com.checkitoff.checkitoff_backend.controller;

import com.checkitoff.checkitoff_backend.model.News;
import com.checkitoff.checkitoff_backend.model.User;
import com.checkitoff.checkitoff_backend.model.VotesAndComments;
import com.checkitoff.checkitoff_backend.repository.NewsRepository;
import com.checkitoff.checkitoff_backend.repository.UserRepository; // ✅ เพิ่มตรงนี้
import com.checkitoff.checkitoff_backend.repository.VotesAndCommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/votes")
public class VotesAndCommentsController {
	
	@Autowired
	private NewsRepository newsRepository;
	
	@Autowired
	private VotesAndCommentsRepository votesAndCommentsRepository;
	
	@Autowired
	private UserRepository userRepository; // ✅ เพิ่มตรงนี้
	
	@GetMapping
	public List<VotesAndComments> getAllVotesAndComments() {
		return votesAndCommentsRepository.findAll();
	}
	
	@GetMapping("/comments")
	public List<VotesAndComments> getCommentsByNewsId(@RequestParam Long newsId) {
		return votesAndCommentsRepository.findCommentsByNewsId(newsId);
	}
	
	@GetMapping("/news/{newsId}/votes")
	public List<VotesAndComments> getVotesOnlyByNewsId(@PathVariable Long newsId) {
		return votesAndCommentsRepository.findAll()
				.stream()
				.filter(v -> v.getNews() != null && v.getNews().getId().equals(newsId))
				.toList();
	}
	
	@PostMapping
	public ResponseEntity<?> saveVote(@RequestBody Map<String, Object> payload) {
		try {
			Long newsId = Long.valueOf(payload.get("news_id").toString());
			Integer userId = Integer.valueOf(payload.get("user_id").toString());
			String name = (String) payload.get("name");
			String voteStr = payload.get("vote").toString().trim().toLowerCase();
			String comment = (String) payload.get("comment");
			String imageUrl = (String) payload.get("image_url");
			
			News news = newsRepository.findById(newsId)
					.orElseThrow(() -> new RuntimeException("News not found"));
			
			if (news.getUpVotes() == null) news.setUpVotes(0);
			if (news.getDownVotes() == null) news.setDownVotes(0);
			if (news.getCommentsCount() == null) news.setCommentsCount(0);
			
			VotesAndComments vc = new VotesAndComments();
			vc.setUserId(userId);
			vc.setName(name);
			vc.setVote(VotesAndComments.VoteType.valueOf(voteStr));
			vc.setComment(comment);
			vc.setImageUrl(imageUrl);
			vc.setVisible(true);
			vc.setNews(news);
			
			votesAndCommentsRepository.save(vc);
			
			long commentCount = votesAndCommentsRepository.countCommentsByNewsId(newsId);
			long upVotes = votesAndCommentsRepository.countUpVotesByNewsId(newsId);
			long downVotes = votesAndCommentsRepository.countDownVotesByNewsId(newsId);
			
			news.setCommentsCount((int) commentCount);
			news.setUpVotes((int) upVotes);
			news.setDownVotes((int) downVotes);
			
			if ("Unverified".equalsIgnoreCase(news.getCategory())) {
				if (upVotes > downVotes) {
					news.setCategory("Verified");
				} else if (downVotes > upVotes) {
					news.setCategory("Fake News");
				} else {
					news.setCategory("Unverified");
				}
			}
			
			newsRepository.save(news);
			
			return ResponseEntity.ok(Map.of(
					"message", "Vote saved successfully",
					"commentsCount", commentCount,
					"upVotes", upVotes,
					"downVotes", downVotes,
					"category", news.getCategory()
			));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError()
					.body(Map.of("error", e.getMessage()));
		}
	}
	
	
	@GetMapping("/news/{newsId}")
	public List<VotesAndComments> getVotesByNewsId(@PathVariable Long newsId) {
		return votesAndCommentsRepository.findCommentsByNewsId(newsId);
	}
	
	@GetMapping("/hidden")
	public List<VotesAndComments> getHiddenComments() {
		return votesAndCommentsRepository.findHiddenComments();
	}
	
	@PutMapping("/hide/{id}")
	public ResponseEntity<?> hideComment(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
		try {
			VotesAndComments c = votesAndCommentsRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Comment not found"));
			
			boolean isVisible = true;
			if (payload.containsKey("is_visible")) {
				Object v = payload.get("is_visible");
				if (v instanceof Boolean b) isVisible = b;
				else if (v instanceof Number n) isVisible = n.intValue() != 0;
			}
			
			String voteType = null;
			if (payload.containsKey("vote")) {
				voteType = (String) payload.get("vote");
			}
			
			News news = c.getNews();
			if (news.getCommentsCount() == null) news.setCommentsCount(0);
			if (news.getUpVotes() == null) news.setUpVotes(0);
			if (news.getDownVotes() == null) news.setDownVotes(0);
			
			if (!isVisible && c.isVisible()) {
				if (news.getCommentsCount() > 0) news.setCommentsCount(news.getCommentsCount() - 1);
				
				if (voteType != null) {
					if (voteType.equalsIgnoreCase("upvote")) {
						news.setUpVotes(Math.max(0, news.getUpVotes() - 1));
					} else if (voteType.equalsIgnoreCase("downvote")) {
						news.setDownVotes(Math.max(0, news.getDownVotes() - 1));
					}
				} else {
					if (c.getVote() == VotesAndComments.VoteType.upvote) {
						news.setUpVotes(Math.max(0, news.getUpVotes() - 1));
					} else if (c.getVote() == VotesAndComments.VoteType.downvote) {
						news.setDownVotes(Math.max(0, news.getDownVotes() - 1));
					}
				}
				
				c.setVisible(false);
			}
			
			else if (isVisible && !c.isVisible()) {
				news.setCommentsCount(news.getCommentsCount() + 1);
				
				if (voteType != null) {
					if (voteType.equalsIgnoreCase("upvote")) {
						news.setUpVotes(news.getUpVotes() + 1);
					} else if (voteType.equalsIgnoreCase("downvote")) {
						news.setDownVotes(news.getDownVotes() + 1);
					}
				} else {
					if (c.getVote() == VotesAndComments.VoteType.upvote) {
						news.setUpVotes(news.getUpVotes() + 1);
					} else if (c.getVote() == VotesAndComments.VoteType.downvote) {
						news.setDownVotes(news.getDownVotes() + 1);
					}
				}
				
				c.setVisible(true);
			}
			
			votesAndCommentsRepository.save(c);
			newsRepository.save(news);
			
			return ResponseEntity.ok(Map.of(
					"message", "Comment visibility and votes updated successfully",
					"newsUpVotes", news.getUpVotes(),
					"newsDownVotes", news.getDownVotes(),
					"commentsCount", news.getCommentsCount()
			));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(Map.of("error", "Failed to update comment visibility and votes"));
		}
	}
	
	@GetMapping("/api/news/{id}")
	public ResponseEntity<News> getNews(@PathVariable Long id) {
		return ResponseEntity.of(newsRepository.findById(id));
	}
	
	@PutMapping("/news/{newsId}/recalculate")
	public ResponseEntity<?> recalculateCounts(@PathVariable Long newsId) {
		try {
			News news = newsRepository.findById(newsId)
					.orElseThrow(() -> new RuntimeException("News not found"));
			
			long commentCount = votesAndCommentsRepository.countCommentsByNewsId(newsId);
			long upVotes = votesAndCommentsRepository.countUpVotesByNewsId(newsId);
			long downVotes = votesAndCommentsRepository.countDownVotesByNewsId(newsId);
			
			news.setCommentsCount((int) commentCount);
			news.setUpVotes((int) upVotes);
			news.setDownVotes((int) downVotes);
			
			if ("Unverified".equalsIgnoreCase(news.getCategory())) {
				if (upVotes > downVotes) {
					news.setCategory("Verified");
				} else if (downVotes > upVotes) {
					news.setCategory("Fake News");
				} else {
					news.setCategory("Unverified");
				}
			}
			
			newsRepository.save(news);
			
			return ResponseEntity.ok(Map.of(
					"message", "Recalculated successfully",
					"commentsCount", commentCount,
					"upVotes", upVotes,
					"downVotes", downVotes,
					"category", news.getCategory()
			));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError()
					.body(Map.of("error", "Failed to recalculate counts"));
		}
	}
	
	
	@PostMapping("/create-user")
	public ResponseEntity<?> createUser(@RequestBody User user) {
		System.out.println("Received user: " + user.getEmail());
		try {
			if (user.getEmail() != null && user.getEmail().toLowerCase().matches(".*@adminxmnon\\..+")) {
				user.setRole("ADMIN");
			} else if (user.getRole() == null || user.getRole().isEmpty()) {
				user.setRole("READER");
			}
			
			
			user.setVisible(true);
			
			if (user.getPassword() == null || user.getPassword().isEmpty()) {
				return ResponseEntity.badRequest().body(Map.of(
						"success", false,
						"message", "Password is required"
				));
			}
			
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			String hashedPassword = encoder.encode(user.getPassword());
			user.setPassword(hashedPassword);
			
			userRepository.save(user);
			
			return ResponseEntity.ok(Map.of(
					"success", true,
					"message", "User registered successfully",
					"role", user.getRole()
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
