package com.example.da_tmdt_thoitrang.controller;

import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import com.example.da_tmdt_thoitrang.entity.UserEntity;
import com.example.da_tmdt_thoitrang.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public String searchProducts(@RequestParam("q") String keyword, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getCurrentUserId(userDetails);
        List<ProductEntity> results = searchService.searchProducts(keyword, userId);
        model.addAttribute("products", results);
        model.addAttribute("keyword", keyword);
        return "client/search_result";
    }

    private Long getCurrentUserId(UserDetails userDetails) {
        // Lấy ID người dùng hiện tại (tuỳ theo security config của bạn)
        return ((UserEntity) userDetails).getId();
    }
}

