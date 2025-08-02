package com.example.da_tmdt_thoitrang.controller;

import com.example.da_tmdt_thoitrang.entity.VoucherEntity;
import com.example.da_tmdt_thoitrang.service.VoucherService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/voucher")
@RequiredArgsConstructor
class VoucherApiController {

    private final VoucherService voucherService;


    @PostMapping("/apply")
    public ResponseEntity<?> applyVoucher(@RequestBody VoucherApplyRequest request) {
        try {
            VoucherService.VoucherValidationResult result = voucherService.validateVoucher(
                    request.getCode(), request.getUserId(), request.getOrderAmount()
            );

            if (result.isValid()) {
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("discountAmount", result.getDiscountAmount());
                response.put("message", "Mã giảm giá đã được áp dụng.");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "valid", false,
                        "message", result.getMessage()
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "valid", false,
                    "message", "Đã xảy ra lỗi khi áp dụng mã."
            ));
        }
    }

    @Getter
    @Setter
    public static class VoucherApplyRequest {
        private String code;
        private Long userId;
        private BigDecimal orderAmount;
    }

    @PostMapping("/validate")
    public VoucherService.VoucherValidationResult validateVoucher(@RequestBody VoucherValidationRequest request) {
        return voucherService.validateVoucher(request.getCode(), request.getUserId(), request.getOrderAmount());
    }

    @GetMapping("/active")
    public List<VoucherEntity> getActiveVouchers() {
        return voucherService.getActiveVouchers();
    }

    static class VoucherValidationRequest {
        private String code;
        private Long userId;
        private java.math.BigDecimal orderAmount;

        // Getters and setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public java.math.BigDecimal getOrderAmount() { return orderAmount; }
        public void setOrderAmount(java.math.BigDecimal orderAmount) { this.orderAmount = orderAmount; }
    }
}