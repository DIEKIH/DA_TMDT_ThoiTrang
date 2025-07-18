package com.example.da_tmdt_thoitrang.repository;

import com.example.da_tmdt_thoitrang.entity.VoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<VoucherEntity, Long> {

    Optional<VoucherEntity> findByCode(String code);

    List<VoucherEntity> findByIsActiveTrue();

    @Query("SELECT v FROM VoucherEntity v WHERE v.isActive = true " +
            "AND (v.startDate IS NULL OR v.startDate <= :now) " +
            "AND (v.endDate IS NULL OR v.endDate >= :now)")
    List<VoucherEntity> findActiveVouchers(@Param("now") LocalDateTime now);

    @Query("SELECT v FROM VoucherEntity v WHERE v.code = :code " +
            "AND v.isActive = true " +
            "AND (v.startDate IS NULL OR v.startDate <= :now) " +
            "AND (v.endDate IS NULL OR v.endDate >= :now)")
    Optional<VoucherEntity> findValidVoucherByCode(@Param("code") String code,
                                                   @Param("now") LocalDateTime now);

    boolean existsByCode(String code);




}
