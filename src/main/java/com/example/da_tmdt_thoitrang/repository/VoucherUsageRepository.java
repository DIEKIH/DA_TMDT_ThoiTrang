package com.example.da_tmdt_thoitrang.repository;

import com.example.da_tmdt_thoitrang.entity.UserEntity;
import com.example.da_tmdt_thoitrang.entity.VoucherEntity;
import com.example.da_tmdt_thoitrang.entity.VoucherUsageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoucherUsageRepository extends JpaRepository<VoucherUsageEntity, Long> {

    @Query("SELECT vu FROM VoucherUsageEntity vu WHERE vu.user.id = :userId AND vu.voucher.id = :voucherId")
    Optional<VoucherUsageEntity> findByUserIdAndVoucherId(@Param("userId") Long userId,
                                                          @Param("voucherId") Long voucherId);

//    boolean existsByUserIdAndVoucherId(Long userId, Long voucherId);

    long countByVoucherId(Long voucherId);

    boolean existsByUserAndVoucher(UserEntity user, VoucherEntity voucher);

    boolean existsByUserIdAndVoucherId(Long userId, Long voucherId);




}
