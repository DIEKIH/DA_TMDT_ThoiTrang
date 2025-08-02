package com.example.da_tmdt_thoitrang.repository;


import com.example.da_tmdt_thoitrang.entity.DiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<DiscountEntity, Long> {
}
