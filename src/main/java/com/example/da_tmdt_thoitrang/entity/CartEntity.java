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
@Table(name = "carts")
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItemEntity> cartItems = new ArrayList<>();


    public BigDecimal getTotalAmount() {
        return cartItems.stream()
                .map(CartItemEntity::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Integer getTotalItems() {
        return cartItems.stream()
                .mapToInt(CartItemEntity::getQuantity)
                .sum();
    }
//    public Long getId() { return id; }
//
//    public void addItem(ProductDetailEntity productDetail, Integer quantity) {
//        CartItemEntity existingItem = cartItems.stream()
//                .filter(item -> item.getProductDetail().getId().equals(productDetail.getId()))
//                .findFirst()
//                .orElse(null);
//
//        if (existingItem != null) {
//            existingItem.updateQuantity(existingItem.getQuantity() + quantity);
//        } else {
//            CartItemEntity newItem = CartItemEntity.builder()
//                    .cart(this)
//                    .productDetail(productDetail)
//                    .quantity(quantity)
//                    .build();
//            cartItems.add(newItem);
//        }
//    }
//
//    public void removeItem(Long cartItemId) {
//        cartItems.removeIf(item -> item.getId().equals(cartItemId));
//    }
//
//    public void updateItemQuantity(Long cartItemId, Integer quantity) {
//        cartItems.stream()
//                .filter(item -> item.getId().equals(cartItemId))
//                .findFirst()
//                .ifPresent(item -> item.updateQuantity(quantity));
//    }
//
//    public BigDecimal getTotalAmount() {
//        return cartItems.stream()
//                .map(CartItemEntity::getTotalPrice)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//
//    public void clear() { cartItems.clear(); }

    // Other getters and setters...
}