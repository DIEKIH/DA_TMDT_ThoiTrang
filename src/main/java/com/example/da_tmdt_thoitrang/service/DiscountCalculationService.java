package com.example.da_tmdt_thoitrang.service;


import com.example.da_tmdt_thoitrang.entity.DiscountEntity;
import com.example.da_tmdt_thoitrang.entity.ProductDiscountEntity;
import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import com.example.da_tmdt_thoitrang.enums.ValueType;
import com.example.da_tmdt_thoitrang.repository.ProductDiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiscountCalculationService {

    private final ProductDiscountRepository productDiscountRepository;
    private final DiscountService discountService;

    /**
     * Tính giá sau khi áp dụng giảm giá cho sản phẩm
     */
    public BigDecimal calculateFinalPrice(ProductEntity product) {
        if (product == null || product.getPrice() == null) {
            return BigDecimal.ZERO;
        }

        // Lấy discount tốt nhất cho sản phẩm
        Optional<DiscountEntity> bestDiscount = getBestDiscountForProduct(product.getId());

        if (bestDiscount.isPresent()) {
            return discountService.calculateDiscountedPrice(product.getPrice(), bestDiscount.get());
        }

        return product.getPrice();
    }

    /**
     * Lấy thông tin giảm giá tốt nhất cho sản phẩm
     */
    public Optional<DiscountEntity> getBestDiscountForProduct(Long productId) {
        List<ProductDiscountEntity> productDiscounts =
                productDiscountRepository.findByProductId(productId);

        return productDiscounts.stream()
                .map(pd -> pd.getDiscount())
                .filter(discount -> discountService.isDiscountValid(discount))
                .max((d1, d2) -> {
                    // So sánh để tìm discount tốt nhất (có thể custom logic này)
                    BigDecimal value1 = d1.getValueType() == ValueType.PERCENTAGE ?
                            d1.getValue() : BigDecimal.valueOf(100); // Tạm thời
                    BigDecimal value2 = d2.getValueType() == ValueType.PERCENTAGE ?
                            d2.getValue() : BigDecimal.valueOf(100);
                    return value1.compareTo(value2);
                });
    }

    /**
     * Tính tổng tiền giỏ hàng sau khi áp dụng giảm giá
     */
    public BigDecimal calculateCartTotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> calculateFinalPrice(item.getProduct())
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Inner class cho CartItem (có thể tách ra file riêng)
    public static class CartItem {
        private ProductEntity product;
        private int quantity;

        public CartItem(ProductEntity product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public ProductEntity getProduct() { return product; }
        public int getQuantity() { return quantity; }
    }
}
