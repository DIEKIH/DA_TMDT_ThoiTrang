package com.example.da_tmdt_thoitrang.service;

import com.example.da_tmdt_thoitrang.entity.OrderItemEntity;
import com.example.da_tmdt_thoitrang.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;

    public void save(OrderItemEntity orderItem) {
        orderItemRepository.save(orderItem);
    }

    public void saveAll(List<OrderItemEntity> items) {
        orderItemRepository.saveAll(items);
    }

    public List<OrderItemEntity> findByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }


}