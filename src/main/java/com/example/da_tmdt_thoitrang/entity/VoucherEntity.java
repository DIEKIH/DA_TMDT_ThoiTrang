package com.example.da_tmdt_thoitrang.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "vouchers")
public class VoucherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private VoucherType type;

    public enum VoucherType {
        PERCENTAGE,
        FIXED_AMOUNT
    }

    // Changed to BigDecimal for monetary values
    @Column(precision = 10, scale = 2)
    private BigDecimal value;

    @Column(precision = 10, scale = 2)
    private BigDecimal minOrderAmount = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    private Integer usageLimit;

    @Column(nullable = false)
    private Integer usedCount = 0;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderEntity> orders = new ArrayList<>();

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoucherUsageEntity> voucherUsages = new ArrayList<>();


//    public Long getId() { return id; }
//    public String getCode() { return code; }
//
//    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
//        if (!canUse(orderAmount)) return BigDecimal.ZERO;
//
//        BigDecimal discount = type == VoucherType.PERCENTAGE
//                ? orderAmount.multiply(value.divide(BigDecimal.valueOf(100)))
//                : value;
//
//        return maxDiscountAmount != null && discount.compareTo(maxDiscountAmount) > 0
//                ? maxDiscountAmount : discount;
//    }
//
//    public Boolean canUse(BigDecimal orderAmount) {
//        return isValid() && !isExpired() &&
//                orderAmount.compareTo(minOrderAmount) >= 0 &&
//                (usageLimit == null || usedCount < usageLimit);
//    }
//
//    public void use() { this.usedCount++; }
//    public Boolean isValid() { return isActive; }
//    public Boolean isExpired() {
//        return endDate != null && LocalDateTime.now().isAfter(endDate);
//    }

    // Additional getters and setters can be added here if needed
}