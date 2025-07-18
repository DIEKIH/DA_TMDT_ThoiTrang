package com.example.da_tmdt_thoitrang.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "voucher_usages", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "voucher_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherUsageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Simple user ID instead of entity relationship for simplicity
    @ManyToOne
    @JoinColumn(name = "user_id")  // tên cột trong DB
    private UserEntity user;


    // Liên kết với Voucher
    @ManyToOne
    @JoinColumn(name = "voucher_id", nullable = false)
    private VoucherEntity voucher;

    @Column(nullable = false)
    private LocalDateTime usedAt = LocalDateTime.now();
}