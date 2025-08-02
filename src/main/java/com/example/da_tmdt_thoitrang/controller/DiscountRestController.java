package com.example.da_tmdt_thoitrang.controller;


import com.example.da_tmdt_thoitrang.entity.DiscountEntity;
import com.example.da_tmdt_thoitrang.service.DiscountCalculationService;
import com.example.da_tmdt_thoitrang.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountRestController {

    private final DiscountService discountService;
    private final DiscountCalculationService calculationService;

    /**
     * API tính giá sau giảm giá cho sản phẩm
     */
    @GetMapping("/product/{productId}/price")
    public ResponseEntity<Map<String, Object>> getDiscountedPrice(@PathVariable Long productId) {
        try {
            // Giả sử có ProductService để lấy product
            // ProductEntity product = productService.getProductById(productId);
            // BigDecimal finalPrice = calculationService.calculateFinalPrice(product);

            Map<String, Object> response = new HashMap<>();
            // response.put("originalPrice", product.getPrice());
            // response.put("finalPrice", finalPrice);
            // response.put("discount", calculationService.getBestDiscountForProduct(productId).orElse(null));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * API kiểm tra discount có hợp lệ không
     */
    @GetMapping("/{id}/valid")
    public ResponseEntity<Map<String, Boolean>> checkDiscountValid(@PathVariable Long id) {
        try {
            DiscountEntity discount = discountService.getDiscountById(id);
            boolean isValid = discountService.isDiscountValid(discount);

            Map<String, Boolean> response = new HashMap<>();
            response.put("valid", isValid);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Boolean> error = new HashMap<>();
            error.put("valid", false);
            return ResponseEntity.ok(error);
        }
    }
}
