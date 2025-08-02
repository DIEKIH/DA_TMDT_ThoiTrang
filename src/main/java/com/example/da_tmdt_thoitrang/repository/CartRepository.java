package com.example.da_tmdt_thoitrang.repository;

import com.example.da_tmdt_thoitrang.entity.CartEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    Optional<CartEntity> findBySessionId(String sessionId);


    @Query("SELECT DISTINCT c FROM CartEntity c LEFT JOIN FETCH c.cartItems WHERE c.id = :id")
    Optional<CartEntity> findByIdWithItems(Long id);

    Optional<CartEntity> findByUserId(Long userId);

    // Sửa lại query - không cần JOIN FETCH với product vì có thể gây lỗi
    @Query("SELECT DISTINCT c FROM CartEntity c LEFT JOIN FETCH c.cartItems WHERE c.sessionId = :sessionId")
    Optional<CartEntity> findBySessionIdWithItems(String sessionId);

    @Query("SELECT DISTINCT c FROM CartEntity c LEFT JOIN FETCH c.cartItems WHERE c.userId = :userId")
    Optional<CartEntity> findByUserIdWithItems(Long userId);

    @Modifying
    @Transactional
    void deleteBySessionId(String sessionId);

    @Modifying
    @Transactional
    void deleteByUserId(Long userId);
}
