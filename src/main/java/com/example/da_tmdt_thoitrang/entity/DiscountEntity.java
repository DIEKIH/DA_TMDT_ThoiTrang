package com.example.da_tmdt_thoitrang.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
@Table(name = "discounts")
public class DiscountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private DiscountType type;

    public enum DiscountType {
        PRODUCT,
        CATEGORY,
        BRAND
    }

    @Column(precision = 5, scale = 2, nullable = false)
    @Min(value = 0, message = "Discount value must be positive")
    private BigDecimal value;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "discount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductDiscountEntity> productDiscounts = new ArrayList<>();

//    public Long getId() { return id; }
//    public String getName() { return name; }
//    public BigDecimal getValue() { return value; }
//    public Boolean isActive() { return isActive; }
//    public Boolean isValid() {
//        LocalDateTime now = LocalDateTime.now();
//        return isActive &&
//                (startDate == null || now.isAfter(startDate)) &&
//                (endDate == null || now.isBefore(endDate));
//    }
//    public void activate() { this.isActive = true; }
//    public void deactivate() { this.isActive = false; }
//
    @Enumerated(EnumType.STRING)
    private ValueType valueType = ValueType.PERCENTAGE;

    public enum ValueType {
        PERCENTAGE, // giảm theo %
        FIXED_AMOUNT // giảm số tiền cố định
    }

    // Other getters and setters...
}

