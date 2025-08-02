package com.example.da_tmdt_thoitrang.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingDTO {
    private Long userId;
    private Long productId;
    private Integer rating;
}

