package com.example.da_tmdt_thoitrang.dto;




import com.example.da_tmdt_thoitrang.entity.VoucherEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherDto {

    private Long id;

    @NotBlank(message = "Mã voucher không được để trống")
    private String code;

    @NotBlank(message = "Tên voucher không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Loại voucher không được để trống")
    private VoucherEntity.VoucherType type;

    @NotNull(message = "Giá trị voucher không được để trống")
    @Positive(message = "Giá trị voucher phải lớn hơn 0")
    private BigDecimal value;

    @Min(value = 0, message = "Giá trị đơn hàng tối thiểu phải >= 0")
    private BigDecimal minOrderAmount;

    @Min(value = 0, message = "Giá trị giảm tối đa phải >= 0")
    private BigDecimal maxDiscountAmount;

    @Min(value = 1, message = "Số lượt sử dụng phải >= 1")
    private Integer usageLimit;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // Constructor từ Entity
    public VoucherDto(VoucherEntity entity) {
        this.id = entity.getId();
        this.code = entity.getCode();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.type = entity.getType();
        this.value = entity.getValue();
        this.minOrderAmount = entity.getMinOrderAmount();
        this.maxDiscountAmount = entity.getMaxDiscountAmount();
        this.usageLimit = entity.getUsageLimit();
        this.startDate = entity.getStartDate();
        this.endDate = entity.getEndDate();
    }
}