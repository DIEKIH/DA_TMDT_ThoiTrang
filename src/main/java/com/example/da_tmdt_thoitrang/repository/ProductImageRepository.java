package com.example.da_tmdt_thoitrang.repository;


import com.example.da_tmdt_thoitrang.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {

    List<ProductImageEntity> findByProductId(Long productId);

    List<ProductImageEntity> findByProductIdOrderBySortOrder(Long productId);

    ProductImageEntity findByProductIdAndIsPrimaryTrue(Long productId);
}