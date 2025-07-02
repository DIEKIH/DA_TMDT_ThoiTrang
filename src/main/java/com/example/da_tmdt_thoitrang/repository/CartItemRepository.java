package com.example.da_tmdt_thoitrang.repository;

import com.example.da_tmdt_thoitrang.entity.CartEntity;
import com.example.da_tmdt_thoitrang.entity.CartItemEntity;
import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {

    List<CartItemEntity> findByCartId(Long cartId);

    // ✅ Đổi sang ProductEntity
    Optional<CartItemEntity> findByCartIdAndProduct(Long cartId, ProductEntity product);

    void deleteByCartId(Long cartId);

    // ✅ Đổi sang ProductEntity
    void deleteByCartIdAndProduct(Long cartId, ProductEntity product);

    // ✅ Query giữ nguyên
    @Query("SELECT ci FROM CartItemEntity ci JOIN FETCH ci.product WHERE ci.cartId = :cartId")
    List<CartItemEntity> findByCartIdWithProduct(Long cartId);

    // ✅ Sửa lại phần so sánh product.id trong JPQL
    @Modifying
    @Query("UPDATE CartItemEntity ci SET ci.quantity = :quantity WHERE ci.cartId = :cartId AND ci.product.id = :productId")
    int updateQuantity(@Param("cartId") Long cartId, @Param("productId") Long productId, @Param("quantity") Integer quantity);

    // CartItemRepository.java
    Optional<CartItemEntity> findByCartAndProduct(CartEntity cart, ProductEntity product);




}
