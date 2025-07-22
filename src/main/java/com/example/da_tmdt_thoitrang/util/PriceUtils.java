package com.example.da_tmdt_thoitrang.util;



import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceUtils {

    public static BigDecimal calculateDiscountedPrice(BigDecimal price, BigDecimal discountValue, String valueType) {
        if (price == null || discountValue == null || discountValue.compareTo(BigDecimal.ZERO) <= 0) {
            return price;
        }

        BigDecimal discountedPrice = price;
        if ("PERCENTAGE".equalsIgnoreCase(valueType)) {
            BigDecimal percentage = discountValue.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal discountAmount = price.multiply(percentage).setScale(0, RoundingMode.HALF_UP);
            discountedPrice = price.subtract(discountAmount);
        } else if ("FIXED_AMOUNT".equalsIgnoreCase(valueType)) {
            discountedPrice = price.subtract(discountValue).setScale(0, RoundingMode.HALF_UP);
        }
        return discountedPrice.max(BigDecimal.ZERO);
    }
}

