package com.example.da_tmdt_thoitrang.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_details")
public class ProductDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    private String size;
    private String color;

    @Column(columnDefinition = "DECIMAL(10,2)")
    private BigDecimal price;

    @Column(unique = true)
    private String sku;

    @Column(nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", insertable = false, updatable = false)
    private ProductEntity product;

    // Quan hệ với tồn kho (có thể nhiều bản ghi)
    @OneToMany(mappedBy = "productDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InventoryEntity> inventories = new ArrayList<>();

    @OneToMany(mappedBy = "productDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItemEntity> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "productDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    // ==== Getter bổ sung nếu Lombok không xử lý đúng ====
//    public Boolean getIsActive() {
//        return isActive;
//    }
//
//    // ==== Các phương thức xử lý tồn kho ====
//
//    /**
//     * Lấy tổng số lượng tồn kho từ các bản ghi có isSummary = true
//     */
//    @Transient
//    public Integer getTotalStock() {
//        if (inventories == null || inventories.isEmpty()) return 0;
//
//        return inventories.stream()
//                .filter(inv -> Boolean.TRUE.equals(inv.getIsSummary()))
//                .filter(inv -> inv.getQuantityInStock() != null)
//                .mapToInt(InventoryEntity::getQuantityInStock)
//                .sum();
//    }
//
//    /**
//     * Lấy số lượng có thể bán = tồn kho - số đã đặt giữ
//     */
//    @Transient
//    public Integer getAvailableStock() {
//        if (inventories == null) return 0;
//
//        return inventories.stream()
//                .filter(inv -> inv.getIsSummary() != null && inv.getIsSummary())
//                .mapToInt(inv -> inv.getQuantityInStock() - inv.getQuantityReserved())
//                .sum();
//    }
//
//    /**
//     * Kiểm tra sản phẩm còn hàng không
//     */
//    @Transient
//    public Boolean isInStock() {
//        return getAvailableStock() > 0;
//    }
}
