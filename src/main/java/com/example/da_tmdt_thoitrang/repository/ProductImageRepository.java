package com.example.da_tmdt_thoitrang.repository;

import com.example.da_tmdt_thoitrang.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {

    List<ProductImageEntity> findByProductIdOrderBySortOrder(Long productId);

    void deleteByProductId(Long productId);

    @Query("SELECT MAX(pi.sortOrder) FROM ProductImageEntity pi WHERE pi.productId = :productId")
    Integer findMaxSortOrderByProductId(@Param("productId") Long productId);

    List<ProductImageEntity> findByProductId(Long productId);

    /**
     * Tìm tất cả ảnh phụ của sản phẩm, sắp xếp theo sortOrder
     */
//    List<ProductImageEntity> findByProductIdOrderBySortOrder(Long productId);
//
//    /**
//     * Lấy sortOrder lớn nhất của ảnh phụ cho sản phẩm
//     */
//    @Query("SELECT MAX(pi.sortOrder) FROM ProductImageEntity pi WHERE pi.productId = :productId")
//    Integer findMaxSortOrderByProductId(@Param("productId") Long productId);
//
//    /**
//     * Xóa tất cả ảnh phụ của sản phẩm
//     */
//    void deleteByProductId(Long productId);

    /**
     * Đếm số lượng ảnh phụ của sản phẩm
     */
    long countByProductId(Long productId);
}