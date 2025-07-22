package com.example.da_tmdt_thoitrang.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String size;
    private String color;
    private Integer quantity;
    private BigDecimal price;
    private String sku;
    private Boolean isActive;
    private BigDecimal discountedPrice;
    private BigDecimal discountValue;
    private String discountType;
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private Integer viewCount;
    private BigDecimal averageRating;
    private List<ProductImageDTO> productImages;

    // Status helpers
    public String getStockStatus() {
        if (quantity == null || quantity <= 0) return "OUT_OF_STOCK";
        if (quantity <= 10) return "LOW_STOCK";
        return "IN_STOCK";
    }

    public Boolean isOutOfStock() {
        return quantity == null || quantity <= 0;
    }
}