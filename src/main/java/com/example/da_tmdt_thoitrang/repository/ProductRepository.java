package com.example.da_tmdt_thoitrang.repository;


import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import com.example.da_tmdt_thoitrang.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByIsActiveTrue();

    List<ProductEntity> findByCategoryIdAndIsActiveTrue(Long categoryId);

    List<ProductEntity> findByBrandIdAndIsActiveTrue(Long brandId);

    List<ProductEntity> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);

    @Query("SELECT p FROM ProductEntity p WHERE p.categoryId = :categoryId")
    List<ProductEntity> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM ProductEntity p WHERE p.brandId = :brandId")
    List<ProductEntity> findByBrandId(@Param("brandId") Long brandId);

    @Query("SELECT p FROM ProductEntity p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ProductEntity> searchByKeyword(@Param("keyword") String keyword);

    List<ProductEntity> findByIsActiveTrueAndImageUrlIsNotNull();

//    List<ProductEntity> getAllActiveProducts();
    ProductEntity getProductById(Long id);

//    ProductEntity getProductWithImages(Long id);

    // 👉 Lấy ảnh sản phẩm theo ID ảnh (không bắt buộc nếu không dùng riêng ảnh)
    ProductImageEntity getProductImageById(Long imageId);

    // ✅ Cách 2: Sử dụng @Query annotation
    @Query("SELECT p FROM ProductEntity p WHERE p.isActive = true")
    List<ProductEntity> getAllActiveProducts();

    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.productImages WHERE p.id = :id")
    ProductEntity getProductWithImages(@Param("id") Long id);


    // Các method khác sử dụng naming convention
//    List<ProductEntity> findByIsActiveTrueAndImageUrlIsNotNull();

    boolean existsByIdAndIsActiveTrue(Long id);

    List<ProductEntity> findByCategoryIdAndIsActiveTrueOrderByCreatedAtDesc(Long categoryId);

    List<ProductEntity> findByBrandIdAndIsActiveTrueOrderByCreatedAtDesc(Long brandId);

    List<ProductEntity> findByNameContainingIgnoreCaseAndIsActiveTrueOrderByCreatedAtDesc(String keyword);

    List<ProductEntity> findByIsActiveTrueOrderByViewCountDescCreatedAtDesc();

    List<ProductEntity> findByIsActiveTrueOrderByCreatedAtDesc();

    @Query("SELECT p FROM ProductEntity p WHERE p.isActive = true AND p.price BETWEEN ?1 AND ?2 ORDER BY p.price ASC")
    List<ProductEntity> findByIsActiveTrueAndPriceBetweenOrderByPriceAsc(Double minPrice, Double maxPrice);

    List<ProductEntity> findByCategoryIdAndIsActiveTrueAndIdNotOrderByViewCountDesc(Long categoryId, Long excludeId);


}