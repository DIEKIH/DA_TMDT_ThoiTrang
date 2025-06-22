package com.example.da_tmdt_thoitrang.repository;

import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>,
        JpaSpecificationExecutor<ProductEntity> {

    Page<ProductEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<ProductEntity> findByIsActive(Boolean isActive, Pageable pageable);
    Page<ProductEntity> findByCategoryId(Long categoryId, Pageable pageable);
    Page<ProductEntity> findByBrandId(Long brandId, Pageable pageable);

    long countByIsActive(Boolean isActive);

    @Query("SELECT p FROM ProductEntity p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:categoryId IS NULL OR p.categoryId = :categoryId) AND " +
            "(:brandId IS NULL OR p.brandId = :brandId) AND " +
            "(:isActive IS NULL OR p.isActive = :isActive) AND " +
            "(:minPrice IS NULL OR p.basePrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.basePrice <= :maxPrice)")
    Page<ProductEntity> findProductsWithFilters(@Param("name") String name,
                                                @Param("categoryId") Long categoryId,
                                                @Param("brandId") Long brandId,
                                                @Param("isActive") Boolean isActive,
                                                @Param("minPrice") BigDecimal minPrice,
                                                @Param("maxPrice") BigDecimal maxPrice,
                                                Pageable pageable);

    List<ProductEntity> findTop10ByOrderByViewCountDesc();
    List<ProductEntity> findTop10ByOrderByCreatedAtDesc();
}