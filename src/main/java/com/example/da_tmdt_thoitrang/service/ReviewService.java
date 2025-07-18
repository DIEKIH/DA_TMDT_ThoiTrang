package com.example.da_tmdt_thoitrang.service;

import com.example.da_tmdt_thoitrang.dto.RatingDTO;
import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import com.example.da_tmdt_thoitrang.entity.ReviewEntity;
import com.example.da_tmdt_thoitrang.repository.ProductRatingProjection;
import com.example.da_tmdt_thoitrang.repository.ProductRepository;
import com.example.da_tmdt_thoitrang.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ProductRepository productRepository;

    public void submitReview(Long userId, Long productId, Integer rating, String comment) {
        ReviewEntity review = ReviewEntity.builder()
                .userId(userId)
                .productId(productId)
                .rating(rating)
                .comment(comment)
                .isApproved(true) // chờ admin duyệt
                .build();

        reviewRepository.save(review);
    }

//    public List<ReviewEntity> getApprovedReviewsByProduct(Long productId) {
//        return reviewRepository.findByProductIdAndIsApprovedTrue(productId);
//    }

//    public List<ReviewEntity> getApprovedReviewsByProduct(Long productId) {
//        return reviewRepository.findByProductIdAndIsApprovedTrue(productId);
//    }

    public List<ReviewEntity> getApprovedReviewsByProduct(Long productId) {
        return reviewRepository.findByProductIdAndIsApprovedTrue(productId);
    }

    public void approveReview(Long id) {
        ReviewEntity review = reviewRepository.findById(id).orElseThrow();
        review.approve();
        reviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }



    //Hệ thống gợi ý
    public List<ProductEntity> getTop8RecommendedProducts() {
        List<ProductRatingProjection> productRatings = reviewRepository.findAllProductAverageRatings();

        // Tính C: rating trung bình toàn hệ thống
        double totalRating = 0;
        long totalCount = 0;
        for (ProductRatingProjection pr : productRatings) {
            totalRating += pr.getAvgRating() * pr.getRatingCount();
            totalCount += pr.getRatingCount();
        }

        double C = totalCount > 0 ? totalRating / totalCount : 0;
        final int m = 2; // ngưỡng tối thiểu để ổn định thống kê

        // Tính điểm Bayesian Average
        List<ProductScore> scoredProducts = productRatings.stream()
                .map(pr -> {
                    double R = pr.getAvgRating();
                    long v = pr.getRatingCount();
                    double bayesScore = ((double)v / (v + m)) * R + ((double)m / (v + m)) * C;
                    return new ProductScore(pr.getProductId(), bayesScore);
                })
                .sorted(Comparator.comparing(ProductScore::getScore).reversed())
                .limit(4)
                .collect(Collectors.toList());

        // Trả về danh sách ProductEntity
        List<Long> topProductIds = scoredProducts.stream()
                .map(ProductScore::getProductId)
                .collect(Collectors.toList());

        return productRepository.findAllById(topProductIds);
    }

    @Data
    @AllArgsConstructor
    private static class ProductScore {
        private Long productId;
        private Double score;
    }


    public List<ProductEntity> recommendForUser(Long userId, int topN) {
        List<RatingDTO> allRatings = reviewRepository.findAllRatings();

        // 1. Tạo ma trận đánh giá
        Map<Long, Map<Long, Integer>> userRatings = new HashMap<>();
        for (RatingDTO rating : allRatings) {
            userRatings
                    .computeIfAbsent(rating.getUserId(), k -> new HashMap<>())
                    .put(rating.getProductId(), rating.getRating());
        }

        // 2. Tính độ tương đồng giữa user hiện tại và các user khác
        Map<Long, Double> similarityScores = new HashMap<>();
        Map<Long, Integer> targetRatings = userRatings.get(userId);

        if (targetRatings == null) return List.of(); // User chưa đánh giá gì cả

        for (Map.Entry<Long, Map<Long, Integer>> entry : userRatings.entrySet()) {
            Long otherUserId = entry.getKey();
            if (otherUserId.equals(userId)) continue;

            Map<Long, Integer> otherRatings = entry.getValue();
            double sim = cosineSimilarity(targetRatings, otherRatings);
            if (sim > 0) {
                similarityScores.put(otherUserId, sim);
            }
        }

        // 3. Dự đoán điểm cho sản phẩm chưa đánh giá
        Map<Long, Double> predictedScores = new HashMap<>();
        Map<Long, Double> simSums = new HashMap<>();

        for (Map.Entry<Long, Double> simEntry : similarityScores.entrySet()) {
            Long otherUserId = simEntry.getKey();
            double similarity = simEntry.getValue();
            Map<Long, Integer> otherRatings = userRatings.get(otherUserId);

            for (Map.Entry<Long, Integer> ratingEntry : otherRatings.entrySet()) {
                Long productId = ratingEntry.getKey();
                Integer rating = ratingEntry.getValue();

                if (targetRatings.containsKey(productId)) continue; // đã đánh giá

                predictedScores.merge(productId, rating * similarity, Double::sum);
                simSums.merge(productId, similarity, Double::sum);
            }
        }

        // 4. Tính điểm dự đoán trung bình
        List<ProductScore> predicted = predictedScores.entrySet().stream()
                .map(e -> new ProductScore(
                        e.getKey(),
                        e.getValue() / simSums.get(e.getKey())
                ))
                .sorted(Comparator.comparing(ProductScore::getScore).reversed())
                .limit(topN)
                .collect(Collectors.toList());

        List<Long> recommendedIds = predicted.stream()
                .map(ProductScore::getProductId)
                .collect(Collectors.toList());

        return productRepository.findAllById(recommendedIds);
    }


    private double cosineSimilarity(Map<Long, Integer> a, Map<Long, Integer> b) {
        Set<Long> common = new HashSet<>(a.keySet());
        common.retainAll(b.keySet());

        if (common.isEmpty()) return 0.0;

        double dot = 0, normA = 0, normB = 0;

        for (Long itemId : common) {
            dot += a.get(itemId) * b.get(itemId);
        }

        for (Integer val : a.values()) {
            normA += val * val;
        }

        for (Integer val : b.values()) {
            normB += val * val;
        }

        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }


}

