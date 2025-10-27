package com.checkitoff.checkitoff_backend.repository;

import com.checkitoff.checkitoff_backend.model.VotesAndComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VotesAndCommentsRepository extends JpaRepository<VotesAndComments, Long> {
	
	@Query("SELECT COUNT(v) FROM VotesAndComments v WHERE v.news.id = :newsId AND v.comment IS NOT NULL")
	long countCommentsByNewsId(Long newsId);
	
	@Query("SELECT COUNT(v) FROM VotesAndComments v WHERE v.news.id = :newsId AND v.vote = 'upvote'")
	long countUpVotesByNewsId(Long newsId);
	
	@Query("SELECT COUNT(v) FROM VotesAndComments v WHERE v.news.id = :newsId AND v.vote = 'downvote'")
	long countDownVotesByNewsId(Long newsId);
	
	@Query("SELECT v FROM VotesAndComments v WHERE v.news.id = :newsId")
	List<VotesAndComments> findCommentsByNewsId(Long newsId);
	
	@Query("SELECT v FROM VotesAndComments v WHERE v.isVisible = false")
	List<VotesAndComments> findHiddenComments();
	
}
