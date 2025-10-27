package com.checkitoff.checkitoff_backend.service;

import com.checkitoff.checkitoff_backend.model.News;
import com.checkitoff.checkitoff_backend.repository.NewsRepository;
import com.checkitoff.checkitoff_backend.repository.VotesAndCommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NewsService {
	
	@Autowired
	private NewsRepository newsRepository;
	
	@Autowired
	private VotesAndCommentsRepository votesRepo;
	
	public void updateAllCounts() {
		List<News> newsList = newsRepository.findAll();
		
		for (News news : newsList) {
			long newComments = votesRepo.countCommentsByNewsId(news.getId());
			long newUp = votesRepo.countUpVotesByNewsId(news.getId());
			long newDown = votesRepo.countDownVotesByNewsId(news.getId());
			
			int currentComments = news.getCommentsCount() != null ? news.getCommentsCount() : 0;
			int currentUp = news.getUpVotes() != null ? news.getUpVotes() : 0;
			int currentDown = news.getDownVotes() != null ? news.getDownVotes() : 0;
			
			news.setCommentsCount(currentComments + (int) newComments);
			news.setUpVotes(currentUp + (int) newUp);
			news.setDownVotes(currentDown + (int) newDown);
			
			newsRepository.save(news);
		}
	}
	
	public void updateCountsByNewsId(Long newsId) {
		News news = newsRepository.findById(newsId).orElse(null);
		if (news != null) {
			long newComments = votesRepo.countCommentsByNewsId(newsId);
			long newUpVotes = votesRepo.countUpVotesByNewsId(newsId);
			long newDownVotes = votesRepo.countDownVotesByNewsId(newsId);
			
			int totalComments = news.getCommentsCount() + (int) newComments;
			int totalUpVotes = news.getUpVotes() + (int) newUpVotes;
			int totalDownVotes = news.getDownVotes() + (int) newDownVotes;
			
			news.setCommentsCount(totalComments);
			news.setUpVotes(totalUpVotes);
			news.setDownVotes(totalDownVotes);
			
			newsRepository.save(news);
		}
	}
	
	@Transactional
	public void updateCategoryForAllNews() {
		List<News> allNews = newsRepository.findAll();
		for (News news : allNews) {
			if ("Unverified".equalsIgnoreCase(news.getCategory())) {
				int up = news.getUpVotes() != null ? news.getUpVotes() : 0;
				int down = news.getDownVotes() != null ? news.getDownVotes() : 0;
				
				if (up > down) news.setCategory("Verified");
				else if (down > up) news.setCategory("Fake News");
				else news.setCategory("Unverified");
				
				newsRepository.save(news);
			}
		}
	}
	@Transactional
	public void recalculateCountsByNewsId(Long newsId) {
		News news = newsRepository.findById(newsId).orElse(null);
		if (news != null) {
			int newComments = (int) votesRepo.countCommentsByNewsId(newsId);
			int newUpVotes = (int) votesRepo.countUpVotesByNewsId(newsId);
			int newDownVotes = (int) votesRepo.countDownVotesByNewsId(newsId);
			
			news.setCommentsCount(newComments);
			news.setUpVotes(newUpVotes);
			news.setDownVotes(newDownVotes);
			
			newsRepository.save(news);
		}
	}
	
	public List<News> searchNews(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return newsRepository.findAll();
		}
		return newsRepository.searchNews(keyword.trim());
	}
	
}
