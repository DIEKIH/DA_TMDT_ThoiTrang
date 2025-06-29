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
@Table(name = "cart_items")
public class CartItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long cartId;

//    @Column(nullable = false)
//    private Long productDetailId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productId;


    @Column(nullable = false)
    private Integer quantity;

    @Column(columnDefinition = "DECIMAL(10,2)")
    private BigDecimal unitPrice;

    @CreationTimestamp
    private LocalDateTime addedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cartId", insertable = false, updatable = false)
    private CartEntity cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product", insertable = false, updatable = false)
    private ProductEntity productEntity;


    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
//    public Long getId() { return id; }
//    public Integer getQuantity() { return quantity; }
//    public BigDecimal getTotalPrice() {
//        if (unitPrice == null) return BigDecimal.ZERO;
//        return unitPrice.multiply(BigDecimal.valueOf(quantity));
//    }    public void updateQuantity(Integer quantity) { this.quantity = quantity; }

    // Other getters and setters...
}