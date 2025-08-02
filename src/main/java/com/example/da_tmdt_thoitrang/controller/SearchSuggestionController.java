package com.example.da_tmdt_thoitrang.controller;

import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import com.example.da_tmdt_thoitrang.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/suggest")
public class SearchSuggestionController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<String> suggest(@RequestParam("q") String keyword) {
        List<ProductEntity> products = productRepository
                .findTop5ByNameContainingIgnoreCaseAndIsActiveTrue(keyword);
        return products.stream()
                .map(ProductEntity::getName)
                .collect(Collectors.toList());
    }
}
