package com.example.da_tmdt_thoitrang.service;

import com.example.da_tmdt_thoitrang.dto.VoucherDto;
import com.example.da_tmdt_thoitrang.entity.UserEntity;
import com.example.da_tmdt_thoitrang.entity.VoucherEntity;
import com.example.da_tmdt_thoitrang.entity.VoucherUsageEntity;
import com.example.da_tmdt_thoitrang.repository.UserRepository;
import com.example.da_tmdt_thoitrang.repository.VoucherRepository;
import com.example.da_tmdt_thoitrang.repository.VoucherUsageRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherUsageRepository voucherUsageRepository;
    private final UserRepository userRepository;

    public List<VoucherEntity> getAllVouchers() {
        return voucherRepository.findAll();
    }

    public List<VoucherEntity> getActiveVouchers() {
        return voucherRepository.findActiveVouchers(LocalDateTime.now());
    }

    public Optional<VoucherEntity> getVoucherById(Long id) {
        return voucherRepository.findById(id);
    }

    public Optional<VoucherEntity> getVoucherByCode(String code) {
        return voucherRepository.findByCode(code);
    }

    @Transactional
    public VoucherEntity createVoucher(VoucherDto voucherDto) {
        if (voucherRepository.existsByCode(voucherDto.getCode())) {
            throw new IllegalArgumentException("Mã voucher đã tồn tại");
        }

        VoucherEntity voucher = VoucherEntity.builder()
                .code(voucherDto.getCode())
                .name(voucherDto.getName())
                .description(voucherDto.getDescription())
                .type(voucherDto.getType())
                .value(voucherDto.getValue())
                .minOrderAmount(voucherDto.getMinOrderAmount())
                .maxDiscountAmount(voucherDto.getMaxDiscountAmount())
                .usageLimit(voucherDto.getUsageLimit())
                .startDate(voucherDto.getStartDate())
                .endDate(voucherDto.getEndDate())
                .isActive(true)
                .usedCount(0)
                .build();

        return voucherRepository.save(voucher);
    }

    @Transactional
    public VoucherEntity updateVoucher(Long id, VoucherDto voucherDto) {
        VoucherEntity voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Voucher không tồn tại"));

        // Kiểm tra code trùng lặp (trừ chính voucher này)
        if (!voucher.getCode().equals(voucherDto.getCode()) &&
                voucherRepository.existsByCode(voucherDto.getCode())) {
            throw new IllegalArgumentException("Mã voucher đã tồn tại");
        }

        voucher.setCode(voucherDto.getCode());
        voucher.setName(voucherDto.getName());
        voucher.setDescription(voucherDto.getDescription());
        voucher.setType(voucherDto.getType());
        voucher.setValue(voucherDto.getValue());
        voucher.setMinOrderAmount(voucherDto.getMinOrderAmount());
        voucher.setMaxDiscountAmount(voucherDto.getMaxDiscountAmount());
        voucher.setUsageLimit(voucherDto.getUsageLimit());
        voucher.setStartDate(voucherDto.getStartDate());
        voucher.setEndDate(voucherDto.getEndDate());

        return voucherRepository.save(voucher);
    }

    @Transactional
    public void deleteVoucher(Long id) {
        VoucherEntity voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Voucher không tồn tại"));

        // Soft delete
        voucher.setIsActive(false);
        voucherRepository.save(voucher);
    }

//    public VoucherValidationResult validateVoucher(String code, Long userId, BigDecimal orderAmount) {
//        // Tìm voucher
//        Optional<VoucherEntity> voucherOpt = voucherRepository.findValidVoucherByCode(code, LocalDateTime.now());
//        if (voucherOpt.isEmpty()) {
//            return new VoucherValidationResult(false, "Voucher không tồn tại hoặc đã hết hạn", null, BigDecimal.ZERO);
//        }
//
//        VoucherEntity voucher = voucherOpt.get();
//
//        // Kiểm tra user đã sử dụng voucher chưa
//        if (voucherUsageRepository.existsByUserIdAndVoucherId(userId, voucher.getId())) {
//            return new VoucherValidationResult(false, "Bạn đã sử dụng voucher này rồi", null, BigDecimal.ZERO);
//        }
//
//        // Kiểm tra số lượt sử dụng
//        if (voucher.getUsageLimit() != null && voucher.getUsedCount() >= voucher.getUsageLimit()) {
//            return new VoucherValidationResult(false, "Voucher đã hết lượt sử dụng", null, BigDecimal.ZERO);
//        }
//
//        // Kiểm tra đơn hàng tối thiểu
//        if (voucher.getMinOrderAmount() != null && orderAmount.compareTo(voucher.getMinOrderAmount()) < 0) {
//            return new VoucherValidationResult(false,
//                    "Đơn hàng tối thiểu " + voucher.getMinOrderAmount() + " để sử dụng voucher này",
//                    null, BigDecimal.ZERO);
//        }
//
//        // Tính toán discount amount
//        BigDecimal discountAmount = calculateDiscountAmount(voucher, orderAmount);
//
//        return new VoucherValidationResult(true, "Voucher hợp lệ", voucher, discountAmount);
//    }

    private BigDecimal calculateDiscountAmount(VoucherEntity voucher, BigDecimal orderAmount) {
        BigDecimal discountAmount;

        if (voucher.getType() == VoucherEntity.VoucherType.PERCENTAGE) {
            // Discount theo phần trăm
            discountAmount = orderAmount.multiply(voucher.getValue()).divide(BigDecimal.valueOf(100));

            // Giới hạn discount tối đa nếu có
            if (voucher.getMaxDiscountAmount() != null &&
                    discountAmount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
                discountAmount = voucher.getMaxDiscountAmount();
            }
        } else {
            // Discount cố định
            discountAmount = voucher.getValue();

            // Không được vượt quá giá trị đơn hàng
            if (discountAmount.compareTo(orderAmount) > 0) {
                discountAmount = orderAmount;
            }
        }

        return discountAmount;
    }

    @Transactional
    public void useVoucher(Long voucherId, Long userId) {
        VoucherEntity voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new IllegalArgumentException("Voucher không tồn tại"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

        // Tạo bản ghi sử dụng voucher
        VoucherUsageEntity usage = VoucherUsageEntity.builder()
                .voucher(voucher)
                .user(user)
                .usedAt(LocalDateTime.now())
                .build();

        voucherUsageRepository.save(usage);

        // Tăng số lượt sử dụng
        voucher.setUsedCount(voucher.getUsedCount() + 1);
        voucherRepository.save(voucher);
    }

//    public static class VoucherValidationResult {
//        private final boolean valid;
//        private final String message;
//        private final VoucherEntity voucher;
//        private final BigDecimal discountAmount;
//
//        public VoucherValidationResult(boolean valid, String message, VoucherEntity voucher, BigDecimal discountAmount) {
//            this.valid = valid;
//            this.message = message;
//            this.voucher = voucher;
//            this.discountAmount = discountAmount;
//        }
//
//        // Getters
//        public boolean isValid() { return valid; }
//        public String getMessage() { return message; }
//        public VoucherEntity getVoucher() { return voucher; }
//        public BigDecimal getDiscountAmount() { return discountAmount; }
//    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class VoucherValidationResult {
        private boolean valid;
        private String message;
        private VoucherEntity voucher;
        private BigDecimal discountAmount;
    }

    public VoucherValidationResult validateVoucher(String code, Long userId, BigDecimal orderAmount) {
        Optional<VoucherEntity> optionalVoucher = voucherRepository.findByCode(code);

        if (optionalVoucher.isEmpty()) {
            return new VoucherValidationResult(false, "Mã giảm giá không tồn tại.", null, BigDecimal.ZERO);
        }

        VoucherEntity voucher = optionalVoucher.get();

        if (!Boolean.TRUE.equals(voucher.getIsActive())) {
            return new VoucherValidationResult(false, "Mã giảm giá không còn hoạt động.", voucher, BigDecimal.ZERO);
        }

        if (voucher.getStartDate() != null && voucher.getStartDate().isAfter(LocalDateTime.now())) {
            return new VoucherValidationResult(false, "Mã giảm giá chưa bắt đầu.", voucher, BigDecimal.ZERO);
        }

        if (voucher.getEndDate() != null && voucher.getEndDate().isBefore(LocalDateTime.now())) {
            return new VoucherValidationResult(false, "Mã giảm giá đã hết hạn.", voucher, BigDecimal.ZERO);
        }

        if (voucher.getUsageLimit() != null && voucher.getUsedCount() >= voucher.getUsageLimit()) {
            return new VoucherValidationResult(false, "Mã giảm giá đã hết lượt sử dụng.", voucher, BigDecimal.ZERO);
        }

        boolean alreadyUsed = voucherUsageRepository.existsByUserIdAndVoucherId(userId, voucher.getId());
        if (alreadyUsed) {
            return new VoucherValidationResult(false, "Bạn đã sử dụng mã này rồi.", voucher, BigDecimal.ZERO);
        }

        if (voucher.getMinOrderAmount() != null && orderAmount.compareTo(voucher.getMinOrderAmount()) < 0) {
            return new VoucherValidationResult(false, "Đơn hàng chưa đạt tối thiểu " +
                    voucher.getMinOrderAmount() + "đ để dùng mã.", voucher, BigDecimal.ZERO);
        }

        BigDecimal discount = BigDecimal.ZERO;

        if (voucher.getType() == VoucherEntity.VoucherType.PERCENTAGE) {
            discount = orderAmount.multiply(voucher.getValue()).divide(BigDecimal.valueOf(100));

            if (voucher.getMaxDiscountAmount() != null && discount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
                discount = voucher.getMaxDiscountAmount();
            }

        } else if (voucher.getType() == VoucherEntity.VoucherType.FIXED_AMOUNT) {
            discount = voucher.getValue();
        }

        return new VoucherValidationResult(true, "Áp dụng mã thành công.", voucher, discount);
    }

    public void markVoucherUsed(String code, Long userId) {
        VoucherEntity voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));

        // Tăng used count
        voucher.setUsedCount(voucher.getUsedCount() + 1);
        voucherRepository.save(voucher);

        // Ghi nhận người dùng đã sử dụng
        VoucherUsageEntity usage = VoucherUsageEntity.builder()
                .user(UserEntity.builder().id(userId).build()) // tránh load cả User
                .voucher(voucher)
                .usedAt(LocalDateTime.now())
                .build();

        voucherUsageRepository.save(usage);
    }



//    @Transactional
//    public void markVoucherUsed(String voucherCode, Long userId) {
//        System.out.println("=== MARK VOUCHER USED ===");
//        System.out.println("Voucher Code: " + voucherCode);
//        System.out.println("User ID: " + userId);
//
//        // Tìm voucher
//        VoucherEntity voucher = voucherRepository.findByCode(voucherCode)
//                .orElseThrow(() -> new RuntimeException("Voucher not found: " + voucherCode));
//
//        System.out.println("Found voucher: " + voucher.getId());
//
//        // Tìm user
//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
//
//        System.out.println("Found user: " + user.getId());
//
//        // Kiểm tra đã sử dụng chưa
//        boolean alreadyUsed = voucherUsageRepository.existsByUserIdAndVoucherId(userId, voucher.getId());
//        System.out.println("Already used: " + alreadyUsed);
//
//        if (alreadyUsed) {
//            throw new RuntimeException("Voucher đã được sử dụng bởi user này");
//        }
//
//        // Tạo bản ghi sử dụng voucher
//        VoucherUsageEntity usage = VoucherUsageEntity.builder()
//                .user(user)
//                .voucher(voucher)
//                .usedAt(LocalDateTime.now())
//                .build();
//
//        VoucherUsageEntity savedUsage = voucherUsageRepository.save(usage);
//        System.out.println("Saved voucher usage: " + savedUsage.getId());
//
//        // Tăng lượt sử dụng của voucher
//        voucher.setUsedCount(voucher.getUsedCount() + 1);
//        VoucherEntity updatedVoucher = voucherRepository.save(voucher);
//        System.out.println("Updated voucher used count: " + updatedVoucher.getUsedCount());
//    }

//    public VoucherValidationResult validateVoucher(String code, Long userId, BigDecimal orderAmount) {
//        System.out.println("=== VALIDATE VOUCHER ===");
//        System.out.println("Code: " + code);
//        System.out.println("User ID: " + userId);
//        System.out.println("Order Amount: " + orderAmount);
//
//        // Tìm voucher
//        Optional<VoucherEntity> voucherOpt = voucherRepository.findValidVoucherByCode(code, LocalDateTime.now());
//        if (voucherOpt.isEmpty()) {
//            System.out.println("Voucher not found or expired");
//            return new VoucherValidationResult(false, "Voucher không tồn tại hoặc đã hết hạn", null, BigDecimal.ZERO);
//        }
//
//        VoucherEntity voucher = voucherOpt.get();
//        System.out.println("Found voucher: " + voucher.getId());
//
//        // Kiểm tra user đã sử dụng voucher chưa
//        boolean alreadyUsed = voucherUsageRepository.existsByUserIdAndVoucherId(userId, voucher.getId());
//        System.out.println("Already used by user: " + alreadyUsed);
//
//        if (alreadyUsed) {
//            return new VoucherValidationResult(false, "Bạn đã sử dụng voucher này rồi", null, BigDecimal.ZERO);
//        }
//
//        // Kiểm tra số lượt sử dụng
//        if (voucher.getUsageLimit() != null && voucher.getUsedCount() >= voucher.getUsageLimit()) {
//            System.out.println("Voucher usage limit exceeded");
//            return new VoucherValidationResult(false, "Voucher đã hết lượt sử dụng", null, BigDecimal.ZERO);
//        }
//
//        // Kiểm tra đơn hàng tối thiểu
//        if (voucher.getMinOrderAmount() != null && orderAmount.compareTo(voucher.getMinOrderAmount()) < 0) {
//            System.out.println("Order amount below minimum");
//            return new VoucherValidationResult(false,
//                    "Đơn hàng tối thiểu " + voucher.getMinOrderAmount() + "đ để áp dụng voucher",
//                    null,
//                    BigDecimal.ZERO);
//        }
//
//        // Tính toán giảm giá
//        BigDecimal discount;
//        if (voucher.getType() == VoucherEntity.VoucherType.PERCENTAGE) {
//            discount = orderAmount.multiply(voucher.getValue()).divide(BigDecimal.valueOf(100));
//            if (voucher.getMaxDiscountAmount() != null && discount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
//                discount = voucher.getMaxDiscountAmount();
//            }
//        } else {
//            discount = voucher.getValue();
//        }
//
//        // Không cho giảm nhiều hơn tổng đơn hàng
//        if (discount.compareTo(orderAmount) > 0) {
//            discount = orderAmount;
//        }
//
//        System.out.println("Calculated discount: " + discount);
//
//        return new VoucherValidationResult(true, "Voucher hợp lệ", voucher, discount);
//    }



//    public VoucherEntity findByCode(String code) {
//        return voucherRepository.findByCode(code).orElse(null);
//    }

//    public BigDecimal calculateDiscount(VoucherEntity voucher, BigDecimal totalAmount) {
//        if (voucher.getType() == VoucherEntity.VoucherType.PERCENTAGE) {
//            BigDecimal percent = voucher.getValue().divide(BigDecimal.valueOf(100));
//            BigDecimal discount = totalAmount.multiply(percent);
//            return discount.min(voucher.getMaxDiscountAmount() != null ? voucher.getMaxDiscountAmount() : discount);
//        } else if (voucher.getType() == VoucherEntity.VoucherType.FIXED_AMOUNT) {
//            return voucher.getValue();
//        }
//        return BigDecimal.ZERO;
//    }
//
//    public VoucherEntity save(VoucherEntity voucher) {
//        return voucherRepository.save(voucher);
//    }
}
