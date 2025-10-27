package com.checkitoff.checkitoff_backend.repository;

import com.checkitoff.checkitoff_backend.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
	List<News> findByVisibleTrue();
	List<News> findByVisibleFalse();
	long countByCategoryIgnoreCase(String category);
	
	@Query("SELECT n FROM News n WHERE " +
			"LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			"LOWER(n.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			"LOWER(n.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			"LOWER(n.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
			"ORDER BY n.date DESC")
	List<News> searchNews(@Param("keyword") String keyword);
}
