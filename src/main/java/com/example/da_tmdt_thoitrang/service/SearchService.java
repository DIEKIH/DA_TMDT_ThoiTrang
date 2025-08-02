package com.example.da_tmdt_thoitrang.service;

import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import com.example.da_tmdt_thoitrang.entity.SearchHistoryEntity;
import com.example.da_tmdt_thoitrang.repository.ProductRepository;
import com.example.da_tmdt_thoitrang.repository.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SearchService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    public List<ProductEntity> searchProducts(String keyword, Long userId) {
        List<ProductEntity> results = productRepository.searchByKeyword(keyword);

        SearchHistoryEntity history = SearchHistoryEntity.builder()
                .keyword(keyword)
                .userId(userId)
                .searchTime(LocalDateTime.now())
                .resultCount(results.size())
                .build();

        searchHistoryRepository.save(history);
        return results;
    }

    public List<String> getTopSearchKeywords(int limit) {
        return searchHistoryRepository.findTopSearchKeywords(PageRequest.of(0, limit));
    }
}
