package com.checkitoff.checkitoff_backend.controller;

import com.checkitoff.checkitoff_backend.model.News;
import com.checkitoff.checkitoff_backend.repository.NewsRepository;
import com.checkitoff.checkitoff_backend.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/news")
public class NewsController {
	
	@Autowired
	private NewsRepository newsRepository;
	
	@Autowired
	private NewsService newsService;
	
	@GetMapping
	public List<News> getAllNews() {
		return newsRepository.findAll();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<News> getNewsById(@PathVariable Long id) {
		return newsRepository.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
	
	@PutMapping("/update-all-counts")
	public ResponseEntity<Map<String, String>> updateAllCounts() {
		newsService.updateAllCounts();
		
		Map<String, String> response = new HashMap<>();
		response.put("status", "success");
		response.put("message", "Upvotes, Downvotes, and Comments count updated for all news.");
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/{id}/update-all-counts")
	public ResponseEntity<Map<String, String>> updateCountsByNewsId(@PathVariable Long id) {
		newsService.updateCountsByNewsId(id);
		
		Map<String, String> response = new HashMap<>();
		response.put("status", "success");
		response.put("message", "Counts updated for news ID: " + id);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping
	public ResponseEntity<Map<String, Object>> addNews(@RequestBody News news) {
		News savedNews = newsRepository.save(news);
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		response.put("message", "🌟 News added successfully");
		response.put("data", savedNews);
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/{id}/toggle-visibility")
	public ResponseEntity<Map<String, Object>> toggleVisibility(@PathVariable Long id) {
		Optional<News> optional = newsRepository.findById(id);
		Map<String, Object> response = new HashMap<>();
		
		if (optional.isPresent()) {
			News news = optional.get();
			news.setVisible(!Boolean.TRUE.equals(news.getVisible()));
			newsRepository.save(news);
			
			response.put("success", true);
			response.put("visible", news.getVisible());
			response.put("message", news.getVisible() ? "News is now visible" : "🚫 News hidden");
			return ResponseEntity.ok(response);
		}
		
		response.put("success", false);
		response.put("message", "News not found");
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}
	
	@PutMapping("/hide/{id}")
	public ResponseEntity<Map<String, Object>> hideNews(@PathVariable Long id) {
		return updateVisibility(id, false);
	}
	
	@PutMapping("/show/{id}")
	public ResponseEntity<Map<String, Object>> showNews(@PathVariable Long id) {
		return updateVisibility(id, true);
	}
	
	private ResponseEntity<Map<String, Object>> updateVisibility(Long id, boolean visible) {
		Optional<News> optional = newsRepository.findById(id);
		Map<String, Object> response = new HashMap<>();
		
		if (optional.isPresent()) {
			News news = optional.get();
			news.setVisible(visible);
			newsRepository.save(news);
			
			response.put("success", true);
			response.put("visible", visible);
			response.put("message", visible ? "🌟 News shown successfully" : "🚧 News hidden successfully");
			return ResponseEntity.ok(response);
		}
		
		response.put("success", false);
		response.put("message", "News not found");
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}
	
	@GetMapping("/stats")
	public Map<String, Object> getStats() {
		long total = newsRepository.count();
		long verified = newsRepository.countByCategoryIgnoreCase("Verified");
		long fake = newsRepository.countByCategoryIgnoreCase("Fake News");
		long unverified = newsRepository.countByCategoryIgnoreCase("Unverified");
		
		Map<String, Object> stats = new HashMap<>();
		stats.put("total", total);
		stats.put("verified", verified);
		stats.put("fake", fake);
		stats.put("unverified", unverified);
		
		Map<String, Object> response = new HashMap<>();
		response.put("stats", stats);
		return response;
	}
	
	@GetMapping("/api/news/{id}")
	public ResponseEntity<News> getNews(@PathVariable Long id) {
		return ResponseEntity.of(newsRepository.findById(id));
	}
	
	@GetMapping("/search")
	public ResponseEntity<List<News>> searchNews(@RequestParam("q") String keyword) {
		try {
			List<News> results = newsService.searchNews(keyword);
			return ResponseEntity.ok(results);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	
}
