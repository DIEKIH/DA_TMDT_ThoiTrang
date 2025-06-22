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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sales_statistics")
public class SalesStatisticEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(columnDefinition = "DECIMAL(10,2)")
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer totalOrders = 0;

    @Column(nullable = false)
    private Integer totalProducts = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public Integer getTotalOrders() { return totalOrders; }
    public LocalDate getDate() { return date; }

    public void updateStatistics(BigDecimal revenue, Integer orders, Integer products) {
        this.totalRevenue = revenue;
        this.totalOrders = orders;
        this.totalProducts = products;
    }

    // Other getters and setters...
}
