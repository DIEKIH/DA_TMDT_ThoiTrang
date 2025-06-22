package com.example.da_tmdt_thoitrang.repository;

import com.example.da_tmdt_thoitrang.entity.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<BrandEntity, Long> {
    List<BrandEntity> findByIsActiveTrue();
    List<BrandEntity> findByNameContainingIgnoreCase(String name);
    Optional<BrandEntity> findByNameIgnoreCase(String name);

    @Query("SELECT b FROM BrandEntity b WHERE b.isActive = :isActive")
    List<BrandEntity> findByActiveStatus(@Param("isActive") Boolean isActive);
}