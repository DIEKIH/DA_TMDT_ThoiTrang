package com.example.da_tmdt_thoitrang.service;

import com.example.da_tmdt_thoitrang.dto.OrderDto;
import com.example.da_tmdt_thoitrang.entity.OrderEntity;
import com.example.da_tmdt_thoitrang.entity.VoucherEntity;
import com.example.da_tmdt_thoitrang.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private VoucherService voucherService;

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





//    @Transactional
//    public OrderEntity createOrder(OrderDto orderDto) {
//        // Tạo order number unique
//        String orderNumber = generateOrderNumber();
//
//        OrderEntity order = OrderEntity.builder()
//                .userId(orderDto.getUserId())
//                .orderNumber(orderNumber)
//                .totalAmount(orderDto.getTotalAmount())
//                .shippingFee(orderDto.getShippingFee())
//                .shippingAddress(orderDto.getShippingAddress())
//                .notes(orderDto.getNotes())
//                .orderDate(LocalDateTime.now())
//                .status(OrderEntity.OrderStatus.PENDING)
//                .paymentStatus(OrderEntity.PaymentStatus.PENDING)
//                .discountAmount(BigDecimal.ZERO)
//                .build();
//
//        // Xử lý voucher nếu có
//        if (orderDto.getVoucherCode() != null && !orderDto.getVoucherCode().trim().isEmpty()) {
//            VoucherService.VoucherValidationResult validationResult = voucherService.validateVoucher(
//                    orderDto.getVoucherCode(),
//                    orderDto.getUserId(),
//                    orderDto.getTotalAmount()
//            );
//
//            if (!validationResult.isValid()) {
//                throw new IllegalArgumentException(validationResult.getMessage());
//            }
//
//            // Áp dụng voucher
//            VoucherEntity voucher = validationResult.getVoucher();
//            order.setVoucherId(voucher.getId());
//            order.setDiscountAmount(validationResult.getDiscountAmount());
//
//            // Đánh dấu voucher đã được sử dụng
//            voucherService.useVoucher(voucher.getId(), orderDto.getUserId());
//        }
//
//        // Tính final amount
//        order.calculateFinalAmount();
//
//        return orderRepository.save(order);
//    }

    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public OrderEntity getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order không tồn tại"));
    }

    @Transactional
    public OrderEntity updateOrderStatus(Long orderId, OrderEntity.OrderStatus status) {
        OrderEntity order = getOrderById(orderId);
        order.updateStatus(status);
        return orderRepository.save(order);
    }

    @Transactional
    public OrderEntity cancelOrder(Long orderId) {
        OrderEntity order = getOrderById(orderId);
        if (!order.canCancel()) {
            throw new IllegalArgumentException("Không thể hủy đơn hàng ở trạng thái này");
        }
        order.cancel();
        return orderRepository.save(order);
    }


}