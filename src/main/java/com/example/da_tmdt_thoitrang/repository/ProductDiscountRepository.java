package com.example.da_tmdt_thoitrang.repository;



import com.example.da_tmdt_thoitrang.entity.ProductDiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDiscountRepository extends JpaRepository<ProductDiscountEntity, Long> {
    List<ProductDiscountEntity> findByDiscountId(Long discountId);
    List<ProductDiscountEntity> findByProductId(Long productId);
    boolean existsByProductIdAndDiscountId(Long productId, Long discountId);
    void deleteByDiscountId(Long discountId);
    void deleteByProductIdAndDiscountId(Long productId, Long discountId);
}
