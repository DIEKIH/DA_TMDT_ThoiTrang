package com.example.da_tmdt_thoitrang.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "inventories")
public class InventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productDetailId;

    // Tồn kho tổng
    @Column(nullable = false)
    private Integer quantityInStock = 0;

    @Column(nullable = false)
    private Integer quantityReserved = 0;

    @Column(nullable = false)
    private Integer quantitySold = 0;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    // Trường để phân biệt giữa bản ghi "tổng" và bản ghi "giao dịch"
    @Column(nullable = false)
    private Boolean isSummary = true; // true: tồn kho tổng, false: giao dịch nhập/xuất

    // Giao dịch nhập/xuất (chỉ dùng khi isSummary = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private Integer transactionQuantity;

    private String note;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productDetailId", insertable = false, updatable = false)
    private ProductDetailEntity productDetail;


    // ====== Hàm xử lý logic tồn kho ======
//    public Integer getAvailableStock() {
//        return quantityInStock - quantityReserved;
//    }
//
//    public Boolean reserveStock(Integer quantity) {
//        if (getAvailableStock() >= quantity) {
//            this.quantityReserved += quantity;
//            return true;
//        }
//        return false;
//    }
//
//    public void releaseReservedStock(Integer quantity) {
//        this.quantityReserved = Math.max(0, this.quantityReserved - quantity);
//    }
//
//    public Boolean deductStock(Integer quantity) {
//        if (quantityInStock >= quantity) {
//            this.quantityInStock -= quantity;
//            this.quantitySold += quantity;
//            return true;
//        }
//        return false;
//    }
//
//    public Boolean isInStock() {
//        return getAvailableStock() > 0;
//    }
//
//    public Integer getTotalStock() {
//        return quantityInStock;
//    }
//
//    public Boolean getIsSummary() {
//        return isSummary;
//    }
//    public Integer getQuantityInStock() {
//        return quantityInStock;
//    }
//    public Integer getQuantityReserved() {
//        return quantityReserved;
//    }


    public enum TransactionType {
        IMPORT,
        EXPORT
    }
}
