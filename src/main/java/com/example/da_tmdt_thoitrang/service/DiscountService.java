package com.example.da_tmdt_thoitrang.service;


import com.example.da_tmdt_thoitrang.dto.DiscountDTO;
import com.example.da_tmdt_thoitrang.entity.DiscountEntity;
import com.example.da_tmdt_thoitrang.entity.ProductDiscountEntity;
import com.example.da_tmdt_thoitrang.enums.ValueType;
import com.example.da_tmdt_thoitrang.repository.DiscountRepository;
import com.example.da_tmdt_thoitrang.repository.ProductDiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final ProductDiscountRepository productDiscountRepository;

    public Page<DiscountEntity> getAllDiscounts(Pageable pageable) {
        return discountRepository.findAll(pageable);
    }

    public DiscountEntity getDiscountById(Long id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương trình giảm giá"));
    }

    @Transactional
    public DiscountEntity createDiscount(DiscountDTO discountDTO) {
        validateDiscount(discountDTO);

        DiscountEntity discount = DiscountEntity.builder()
                .name(discountDTO.getName())
                .description(discountDTO.getDescription())
                .type(discountDTO.getType())
                .value(discountDTO.getValue())
                .valueType(discountDTO.getValueType())
                .startDate(discountDTO.getStartDate())
                .endDate(discountDTO.getEndDate())
                .isActive(discountDTO.getIsActive())
                .build();

        return discountRepository.save(discount);
    }

    @Transactional
    public DiscountEntity updateDiscount(Long id, DiscountDTO discountDTO) {
        validateDiscount(discountDTO);

        DiscountEntity discount = getDiscountById(id);
        discount.setName(discountDTO.getName());
        discount.setDescription(discountDTO.getDescription());
        discount.setType(discountDTO.getType());
        discount.setValue(discountDTO.getValue());
        discount.setValueType(discountDTO.getValueType());
        discount.setStartDate(discountDTO.getStartDate());
        discount.setEndDate(discountDTO.getEndDate());
        discount.setIsActive(discountDTO.getIsActive());

        return discountRepository.save(discount);
    }

    @Transactional
    public void softDeleteDiscount(Long id) {
        DiscountEntity discount = getDiscountById(id);
        discount.setIsActive(false);
        discountRepository.save(discount);
    }

    @Transactional
    public void deleteDiscount(Long id) {
        // Xóa tất cả liên kết với sản phẩm trước
        productDiscountRepository.deleteByDiscountId(id);
        discountRepository.deleteById(id);
    }

    @Transactional
    public void assignProductToDiscount(Long discountId, Long productId) {
        if (!discountRepository.existsById(discountId)) {
            throw new RuntimeException("Không tìm thấy chương trình giảm giá");
        }

        if (productDiscountRepository.existsByProductIdAndDiscountId(productId, discountId)) {
            throw new RuntimeException("Sản phẩm đã có trong chương trình giảm giá này");
        }

        ProductDiscountEntity productDiscount = ProductDiscountEntity.builder()
                .productId(productId)
                .discountId(discountId)
                .build();

        productDiscountRepository.save(productDiscount);
    }

    @Transactional
    public void removeProductFromDiscount(Long discountId, Long productId) {
        productDiscountRepository.deleteByProductIdAndDiscountId(productId, discountId);
    }

    public List<ProductDiscountEntity> getProductsInDiscount(Long discountId) {
        return productDiscountRepository.findByDiscountId(discountId);
    }

    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, DiscountEntity discount) {
        if (discount == null || !isDiscountValid(discount)) {
            return originalPrice;
        }

        BigDecimal discountAmount;
        if (discount.getValueType() == ValueType.PERCENTAGE) {
            discountAmount = originalPrice.multiply(discount.getValue()).divide(BigDecimal.valueOf(100));
        } else {
            discountAmount = discount.getValue();
        }

        BigDecimal finalPrice = originalPrice.subtract(discountAmount);
        return finalPrice.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : finalPrice;
    }

    public boolean isDiscountValid(DiscountEntity discount) {
        if (discount == null || !discount.getIsActive()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        return (discount.getStartDate() == null || now.isAfter(discount.getStartDate())) &&
                (discount.getEndDate() == null || now.isBefore(discount.getEndDate()));
    }

    private void validateDiscount(DiscountDTO discountDTO) {
        if (discountDTO.getStartDate() != null && discountDTO.getEndDate() != null) {
            if (discountDTO.getStartDate().isAfter(discountDTO.getEndDate())) {
                throw new RuntimeException("Ngày bắt đầu không được sau ngày kết thúc");
            }
        }

        if (discountDTO.getValueType() == ValueType.PERCENTAGE) {
            if (discountDTO.getValue().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new RuntimeException("Giá trị giảm giá theo % không được vượt quá 100%");
            }
        }
    }
}
