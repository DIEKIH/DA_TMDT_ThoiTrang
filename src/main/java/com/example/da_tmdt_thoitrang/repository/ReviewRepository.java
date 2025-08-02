package com.example.da_tmdt_thoitrang.repository;

import com.example.da_tmdt_thoitrang.dto.RatingDTO;
import com.example.da_tmdt_thoitrang.entity.ReviewEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
//public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
//
////    List<ReviewEntity> findByProductIdAndIsApprovedTrue(Long productId);
//
////    @EntityGraph(attributePaths = {"user"})
////    List<ReviewEntity> findByProductIdAndIsApprovedTrue(Long productId);
//        @EntityGraph(attributePaths = {"user"})  // 👈 Quan trọng
//        List<ReviewEntity> findByProductIdAndIsApprovedTrue(Long productId);
//}
import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

//        @EntityGraph(attributePaths = {"user"})
//        List<ReviewEntity> findByProductIdAndIsApprovedTrue(Long productId);

        @EntityGraph(attributePaths = {"user"})
        List<ReviewEntity> findByProductIdAndIsApprovedTrue(Long productId);


        @Query("SELECT r.productId AS productId, " +
                "AVG(r.rating) AS avgRating, " +
                "COUNT(r.id) AS ratingCount " +
                "FROM ReviewEntity r " +
                "WHERE r.isApproved = true " +
                "GROUP BY r.productId")
        List<ProductRatingProjection> findAllProductAverageRatings();


        @Query("SELECT new com.example.da_tmdt_thoitrang.dto.RatingDTO(r.userId, r.productId, r.rating) " +
                "FROM ReviewEntity r WHERE r.isApproved = true")
        List<RatingDTO> findAllRatings();

}
