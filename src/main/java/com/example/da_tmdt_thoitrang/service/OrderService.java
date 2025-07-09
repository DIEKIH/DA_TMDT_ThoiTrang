package com.example.da_tmdt_thoitrang.service;

import com.example.da_tmdt_thoitrang.entity.OrderEntity;
import com.example.da_tmdt_thoitrang.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public OrderEntity save(OrderEntity order) {
        return orderRepository.save(order);
    }

//    public Optional<OrderEntity> findById(Long id) {
//        return orderRepository.findById(id);
//    }

    public List<OrderEntity> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public OrderEntity findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id = " + id));
    }


}