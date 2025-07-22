package com.example.da_tmdt_thoitrang.dto;


import com.example.da_tmdt_thoitrang.entity.DiscountEntity;
import com.example.da_tmdt_thoitrang.enums.DiscountType;
import com.example.da_tmdt_thoitrang.enums.ValueType;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDTO {
    private Long id;

    @NotBlank(message = "Tên chương trình không được để trống")
    @Size(max = 255, message = "Tên chương trình không được quá 255 ký tự")
    private String name;

    private String description;

    @NotNull(message = "Loại giảm giá không được để trống")
    private DiscountType type;

    @NotNull(message = "Giá trị giảm giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá trị giảm giá phải lớn hơn 0")
    @DecimalMax(value = "999999999.99", message = "Giá trị giảm giá không được vượt quá 99999.99")
    private BigDecimal value;

    @NotNull(message = "Kiểu giá trị không được để trống")
    private ValueType valueType;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive = true;
}
