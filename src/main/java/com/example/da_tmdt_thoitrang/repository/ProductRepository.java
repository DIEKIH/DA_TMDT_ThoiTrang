package com.example.da_tmdt_thoitrang.repository;


import com.example.da_tmdt_thoitrang.entity.ProductEntity;
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
}