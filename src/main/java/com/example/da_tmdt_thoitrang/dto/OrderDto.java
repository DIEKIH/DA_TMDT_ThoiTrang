package com.example.da_tmdt_thoitrang.dto;



import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {

    @NotNull(message = "User ID không được để trống")
    private Long userId;

    @NotNull(message = "Tổng tiền không được để trống")
    @Positive(message = "Tổng tiền phải lớn hơn 0")
    private BigDecimal totalAmount;

    private BigDecimal shippingFee = BigDecimal.ZERO;

    private String shippingAddress;

    private String notes;

    private String voucherCode;
}