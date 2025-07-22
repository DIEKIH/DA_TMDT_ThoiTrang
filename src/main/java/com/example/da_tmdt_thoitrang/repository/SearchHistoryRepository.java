package com.example.da_tmdt_thoitrang.repository;

import com.example.da_tmdt_thoitrang.entity.SearchHistoryEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistoryEntity, Long> {

    @Query("SELECT h.keyword FROM SearchHistoryEntity h GROUP BY h.keyword ORDER BY COUNT(h.keyword) DESC")
    List<String> findTopSearchKeywords(Pageable pageable);
}

