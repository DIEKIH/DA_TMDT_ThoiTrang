package com.example.da_tmdt_thoitrang.repository;

import com.example.da_tmdt_thoitrang.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.da_tmdt_thoitrang.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findByNameContainingIgnoreCaseOrderByCreatedAtDesc(String name);

    List<CategoryEntity> findByIsActiveOrderByCreatedAtDesc(Boolean isActive);

    List<CategoryEntity> findByNameContainingIgnoreCaseAndIsActiveOrderByCreatedAtDesc(String name, Boolean isActive);

}