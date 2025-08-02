package com.example.da_tmdt_thoitrang.service;

import com.example.da_tmdt_thoitrang.entity.CartItemEntity;
import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import com.example.da_tmdt_thoitrang.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.text.Normalizer;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSuggestionService {

    private final ProductRepository productRepository;

//    public List<ProductEntity> getSuggestedProductsFromCart(List<CartItemEntity> cartItems) {
//        List<Long> productIds = cartItems.stream()
//                .map(item -> item.getProduct().getId())
//                .collect(Collectors.toList());
//
//        List<Long> categoryIds = cartItems.stream()
//                .map(item -> item.getProduct().getCategoryId())
//                .distinct()
//                .collect(Collectors.toList());
//
//        return productRepository.findSuggestedProducts(
//                categoryIds,
//                productIds,
//                PageRequest.of(0, 8)
//        );
//    }

//    public List<ProductEntity> getSuggestedProductsFromCart(List<CartItemEntity> cartItems) {
//        if (cartItems == null || cartItems.isEmpty()) return Collections.emptyList();
//
//        Set<Long> categoryIds = new HashSet<>();
//        Set<Long> excludedProductIds = new HashSet<>();
//        List<BigDecimal> prices = new ArrayList<>();
//        List<String> keywords = new ArrayList<>();
//
//        for (CartItemEntity item : cartItems) {
//            ProductEntity p = item.getProduct();
//            if (p != null) {
//                categoryIds.add(p.getCategoryId());
//                excludedProductIds.add(p.getId());
//                prices.add(p.getPrice());
//                keywords.add(p.getName()); // ta sẽ lấy từ đây để tách keyword chung
//            }
//        }
//
//        if (categoryIds.isEmpty()) return Collections.emptyList();
//
//        // ✅ Lấy giá trung bình và giới hạn 20%
//        BigDecimal avgPrice = prices.stream()
//                .reduce(BigDecimal.ZERO, BigDecimal::add)
//                .divide(BigDecimal.valueOf(prices.size()), RoundingMode.HALF_UP);
//
//        BigDecimal minPrice = avgPrice.multiply(BigDecimal.valueOf(0.8));
//        BigDecimal maxPrice = avgPrice.multiply(BigDecimal.valueOf(1.2));
//
//        // ✅ Tạm lấy keyword từ tên sản phẩm đầu tiên
//        String keyword = extractMainKeyword(keywords.get(0)); // Có thể làm thông minh hơn
//
//        Pageable limit = PageRequest.of(0, 8); // lấy tối đa 8 gợi ý
//        return productRepository.findSuggestedProducts(
//                new ArrayList<>(categoryIds),
//                new ArrayList<>(excludedProductIds),
//                keyword,
//                minPrice,
//                maxPrice,
//                limit
//        );
//    }
//
//    private String extractMainKeyword(String name) {
//        if (name == null || name.isBlank()) return "";
//
//        // Loại bỏ dấu tiếng Việt và chuyển về lowercase
//        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
//        String noDiacritics = normalized.replaceAll("\\p{M}", "").toLowerCase();
//
//        // Tách từ và lấy từ đầu tiên (có thể cải thiện sau)
//        String[] words = noDiacritics.split(" ");
//        return words[0]; // ví dụ "áo thun" → "ao"
//    }

//    public List<ProductEntity> getSuggestedProductsFromCart(List<CartItemEntity> cartItems) {
//        if (cartItems == null || cartItems.isEmpty()) return Collections.emptyList();
//
//        Set<Long> categoryIds = new HashSet<>();
//        Set<Long> excludedProductIds = new HashSet<>();
//        List<BigDecimal> prices = new ArrayList<>();
//        List<String> keywords = new ArrayList<>();
//
//        for (CartItemEntity item : cartItems) {
//            ProductEntity p = item.getProduct();
//            if (p != null) {
//                categoryIds.add(p.getCategoryId());
//                excludedProductIds.add(p.getId());
//                prices.add(p.getPrice());
//                keywords.add(p.getName());
//            }
//        }
//
//        if (categoryIds.isEmpty()) return Collections.emptyList();
//
//        BigDecimal avgPrice = prices.stream()
//                .reduce(BigDecimal.ZERO, BigDecimal::add)
//                .divide(BigDecimal.valueOf(prices.size()), RoundingMode.HALF_UP);
//
//        BigDecimal minPrice = avgPrice.multiply(BigDecimal.valueOf(0.8));
//        BigDecimal maxPrice = avgPrice.multiply(BigDecimal.valueOf(1.2));
//
//        String keyword = extractMainKeyword(keywords.get(0));
//
//        // 👉 lấy toàn bộ sp cùng danh mục rồi lọc bên Java
//        List<ProductEntity> candidates = productRepository.findAllByCategoryIdInAndIsActiveTrue(
//                new ArrayList<>(categoryIds)
//        );
//
//        return candidates.stream()
//                .filter(p -> !excludedProductIds.contains(p.getId()))
//                .filter(p -> {
//                    String normalizedName = removeVietnamese(p.getName());
//                    return normalizedName.toLowerCase().contains(keyword.toLowerCase());
//                })
//                .filter(p -> p.getPrice().compareTo(minPrice) >= 0 && p.getPrice().compareTo(maxPrice) <= 0)
//                .sorted(Comparator.comparing(ProductEntity::getViewCount).reversed())
//                .limit(8)
//                .collect(Collectors.toList());
//    }
    //hệ thống gợi ý sản phẩm tương tự
    public List<ProductEntity> getSuggestedProductsFromCart(List<CartItemEntity> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) return Collections.emptyList();

        Set<Long> excludedProductIds = new HashSet<>();
        Set<Long> categoryIds = new HashSet<>();
        List<BigDecimal> prices = new ArrayList<>();
        List<String> keywords = new ArrayList<>();

        for (CartItemEntity item : cartItems) {
            ProductEntity p = item.getProduct();
            if (p != null) {
                excludedProductIds.add(p.getId());
                categoryIds.add(p.getCategoryId());
                prices.add(p.getPrice());
                keywords.add(p.getName());
            }
        }

        if (prices.isEmpty() || keywords.isEmpty()) return Collections.emptyList();

        BigDecimal avgPrice = prices.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(prices.size()), RoundingMode.HALF_UP);

        BigDecimal minPrice = avgPrice.multiply(BigDecimal.valueOf(0.8));
        BigDecimal maxPrice = avgPrice.multiply(BigDecimal.valueOf(1.2));

        String keyword = extractMainKeyword(keywords.get(0));

        Pageable limitGeneral = PageRequest.of(0, 6);
        Pageable limitCategory = PageRequest.of(0, 2);

        List<ProductEntity> generalSuggestions = productRepository.findSuggestedProductsGeneral(
                new ArrayList<>(excludedProductIds),
                keyword,
                minPrice,
                maxPrice,
                limitGeneral
        );

        List<ProductEntity> categorySuggestions = productRepository.findSuggestedProductsInSameCategory(
                new ArrayList<>(excludedProductIds),
                new ArrayList<>(categoryIds),
                limitCategory
        );

        // ✅ Gộp và loại trùng nếu cần
        Set<Long> seenIds = new HashSet<>();
        List<ProductEntity> finalSuggestions = new ArrayList<>();

        for (ProductEntity p : categorySuggestions) {
            if (seenIds.add(p.getId())) finalSuggestions.add(p);
        }

        for (ProductEntity p : generalSuggestions) {
            if (seenIds.add(p.getId())) finalSuggestions.add(p);
        }

        return finalSuggestions;
    }


    private String extractMainKeyword(String name) {
        if (name == null || name.isBlank()) return "";
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        String noDiacritics = normalized.replaceAll("\\p{M}", "");
        String[] words = noDiacritics.toLowerCase().split(" ");
        return words.length > 0 ? words[0] : "";
    }

//    private String extractMainKeyword(String name) {
//        if (name == null || name.isBlank()) return "";
//        return removeVietnamese(name).split(" ")[0].toLowerCase();
//    }

    private String removeVietnamese(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }




}

