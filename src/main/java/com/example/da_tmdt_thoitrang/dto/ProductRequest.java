package com.example.da_tmdt_thoitrang.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private String size;
    private String color;
    private Integer quantity;
    private MultipartFile mainImage;
    private List<MultipartFile> extraImages;
    private Long categoryId;
    private Long brandId;
}
