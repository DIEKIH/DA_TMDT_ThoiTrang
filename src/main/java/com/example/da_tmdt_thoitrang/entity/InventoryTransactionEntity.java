//package com.example.da_tmdt_thoitrang.entity;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//import org.springframework.data.annotation.Id;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Entity
//@Table(name = "inventory_transactions")
//public class InventoryTransactionEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private Long productDetailId;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private TransactionType type;
//
//    @Column(nullable = false)
//    private Integer quantity;
//
//    @Column(columnDefinition = "TEXT")
//    private String note;
//
//    @CreationTimestamp
//    private LocalDateTime createdAt;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "productDetailId", insertable = false, updatable = false)
//    private InventoryEntity inventory;
//
//    public enum TransactionType {
//        IMPORT,
//        EXPORT
//    }
//
//    public Long getId() { return id; }
//    public Long getProductDetailId() { return productDetailId; }
//    public TransactionType getType() { return type; }
//    public Integer getQuantity() { return quantity; }
//    public String getNote() { return note; }
//    public LocalDateTime getCreatedAt() { return createdAt; }
//
//    // Other getters and setters...
//}