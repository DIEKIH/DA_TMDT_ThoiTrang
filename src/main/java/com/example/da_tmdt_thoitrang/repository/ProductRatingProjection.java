package com.example.da_tmdt_thoitrang.repository;

public interface ProductRatingProjection {
    Long getProductId();
    Double getAvgRating();
    Long getRatingCount();
}
