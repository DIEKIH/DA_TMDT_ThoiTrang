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
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    @Column(columnDefinition = "DECIMAL(10,2)")
    private BigDecimal shippingFee = BigDecimal.ZERO;



    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void calculateFinalAmount() {
        this.finalAmount = totalAmount.subtract(discountAmount).add(shippingFee);
    }

    @Column(columnDefinition = "DECIMAL(10,2)")
    private BigDecimal totalAmount;

    @Column(columnDefinition = "DECIMAL(10,2)")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(columnDefinition = "DECIMAL(10,2)")
    private BigDecimal finalAmount;

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        REFUNDED
    }


    public enum PaymentStatus {
        PENDING,
        PAID,
        FAILED,
        REFUNDED
    }

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    private LocalDateTime orderDate;

    private LocalDateTime deliveryDate;

    private Long voucherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucherId", insertable = false, updatable = false)
    private VoucherEntity voucher;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public BigDecimal getFinalAmount() { return finalAmount; }
    public void updateStatus(OrderStatus status) { this.status = status; }
    public void updatePaymentStatus(PaymentStatus status) { this.paymentStatus = status; }

    public Boolean canCancel() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }

    public void cancel() { this.status = OrderStatus.CANCELLED; }
    public void confirm() { this.status = OrderStatus.CONFIRMED; }

    // Other getters and setters...
}