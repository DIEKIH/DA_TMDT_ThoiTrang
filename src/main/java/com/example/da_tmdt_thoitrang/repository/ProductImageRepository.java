package com.example.da_tmdt_thoitrang.repository;
import com.example.da_tmdt_thoitrang.entity.ProductImageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {
    List<ProductImageEntity> findByProductIdOrderBySortOrder(Long productId);
    void deleteByProductId(Long productId);
    Optional<ProductImageEntity> findByProductIdAndIsPrimaryTrue(Long productId);

    @Query("SELECT pi FROM ProductImageEntity pi WHERE pi.productId = :productId ORDER BY pi.isPrimary DESC, pi.sortOrder ASC")
    List<ProductImageEntity> findByProductIdOrderByPrimaryAndSort(@Param("productId") Long productId);
}
