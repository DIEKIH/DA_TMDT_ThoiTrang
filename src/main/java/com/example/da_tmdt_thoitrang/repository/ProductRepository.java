package com.example.da_tmdt_thoitrang.repository;

import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query("SELECT p FROM ProductEntity p WHERE p.isActive = true")
    Page<ProductEntity> findAllActive(Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.isActive = false")
    Page<ProductEntity> findAllInactive(Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.quantity <= 0 AND p.isActive = true")
    Page<ProductEntity> findOutOfStockProducts(Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.quantity > 0 AND p.quantity <= 10 AND p.isActive = true")
    Page<ProductEntity> findLowStockProducts(Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.categoryId = :categoryId AND p.isActive = true")
    Page<ProductEntity> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.brandId = :brandId AND p.isActive = true")
    Page<ProductEntity> findByBrandId(@Param("brandId") Long brandId, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:categoryId IS NULL OR p.categoryId = :categoryId) AND " +
            "(:brandId IS NULL OR p.brandId = :brandId) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "p.isActive = true")
    Page<ProductEntity> searchProducts(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("brandId") Long brandId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    Optional<ProductEntity> findBySkuAndIsActiveTrue(String sku);
    boolean existsBySkuAndIdNot(String sku, Long id);
    boolean existsBySku(String sku);

    @Modifying
    @Query("UPDATE ProductEntity p SET p.isActive = :active WHERE p.id IN :ids")
    void updateIsActiveByIds(@Param("active") boolean active, @Param("ids") List<Long> ids);

}