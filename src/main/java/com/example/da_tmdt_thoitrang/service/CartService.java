package com.example.da_tmdt_thoitrang.service;

import com.example.da_tmdt_thoitrang.entity.CartEntity;
import com.example.da_tmdt_thoitrang.entity.CartItemEntity;
import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import com.example.da_tmdt_thoitrang.repository.CartItemRepository;
import com.example.da_tmdt_thoitrang.repository.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private Client_ProductService productService;

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);


    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    public void addToCart(String sessionId, Long userId, Long productId,
                          String color, String size, Integer quantity) {
        // Kiểm tra sản phẩm tồn tại và còn hàng
        ProductEntity product = productService.getProductById(productId);
        if (product == null || !product.getIsActive()) {
            throw new RuntimeException("Sản phẩm không tồn tại hoặc đã bị vô hiệu hóa");
        }

        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Số lượng sản phẩm không đủ trong kho");
        }

        // Tìm hoặc tạo giỏ hàng
        CartEntity cart = getOrCreateCart(sessionId, userId);

        // Kiểm tra xem sản phẩm đã có trong giỏ chưa
        Optional<CartItemEntity> existingItem =
        cartItemRepository.findByCartIdAndProduct(cart.getId(), product);

        if (existingItem.isPresent()) {
            // Cập nhật số lượng
            CartItemEntity item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            if (product.getQuantity() < newQuantity) {
                throw new RuntimeException("Số lượng sản phẩm không đủ trong kho");
            }

            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            // Tạo item mới
            CartItemEntity newItem = CartItemEntity.builder()
                    .cartId(cart.getId())
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(product.getPrice())
                    .color(color)   // 🟢 không còn lỗi
                    .size(size)     // 🟢 không còn lỗi
                    .build();


            cartItemRepository.save(newItem);
        }
    }

    public List<CartItemEntity> getItems(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ
     */
    public void updateCartItemQuantity(String sessionId, Long userId, Long productId, Integer quantity) {
        CartEntity cart = getCart(sessionId, userId);
        if (cart == null) {
            throw new RuntimeException("Giỏ hàng không tồn tại");
        }

        if (quantity <= 0) {
            removeFromCart(sessionId, userId, productId);
            return;
        }

        // Kiểm tra số lượng trong kho
        ProductEntity product = productService.getProductById(productId);
        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Số lượng sản phẩm không đủ trong kho");
        }

        cartItemRepository.updateQuantity(cart.getId(), productId, quantity);
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    public void removeFromCart(String sessionId, Long userId, Long productId) {
        CartEntity cart = getCart(sessionId, userId);
        if (cart != null) {
            ProductEntity product = productService.getProductById(productId);
            cartItemRepository.deleteByCartIdAndProduct(cart.getId(), product);
        }
    }


    /**
     * Lấy giỏ hàng với đầy đủ thông tin
     */
//    public CartEntity getCartWithItems(String sessionId, Long userId) {
//        if (userId != null) {
//            return cartRepository.findByUserIdWithItems(userId).orElse(null);
//        } else {
//            return cartRepository.findBySessionIdWithItems(sessionId).orElse(null);
//        }
//    }

    public CartEntity getCartWithItems(String sessionId, Long userId) {
        CartEntity cart;
        if (userId != null) {
            cart = cartRepository.findByUserIdWithItems(userId).orElse(null);
        } else {
            cart = cartRepository.findBySessionIdWithItems(sessionId).orElse(null);
        }

        // Load product information cho từng cart item (nếu chưa có)
        if (cart != null && cart.getCartItems() != null) {
            for (CartItemEntity item : cart.getCartItems()) {
                if (item.getProduct() == null && item.getProductId() != null) {
                    ProductEntity product = productService.getProductById(item.getProductId());
                    item.setProduct(product);
                }
            }
        }

        return cart;
    }


    /**
     * Xóa toàn bộ giỏ hàng
     */
    public void clearCart(String sessionId, Long userId) {
        CartEntity cart = getCart(sessionId, userId);
        if (cart != null) {
            cartItemRepository.deleteByCartId(cart.getId());
            if (userId != null) {
                cartRepository.deleteByUserId(userId);
            } else {
                cartRepository.deleteBySessionId(sessionId);
            }
        }
    }

    /**
     * Đếm số lượng items trong giỏ
     */
    public Integer getCartItemCount(String sessionId, Long userId) {
        CartEntity cart = getCartWithItems(sessionId, userId);
        return cart != null ? cart.getTotalItems() : 0;
    }

    /**
     * Tính tổng tiền giỏ hàng
     */
    public BigDecimal getCartTotal(String sessionId, Long userId) {
        CartEntity cart = getCartWithItems(sessionId, userId);
        return cart != null ? cart.getTotalAmount() : BigDecimal.ZERO;
    }

    // Helper methods
//    private CartEntity getOrCreateCart(String sessionId, Long userId) {
//        CartEntity cart = getCart(sessionId, userId);
//        if (cart == null) {
//            cart = CartEntity.builder()
//                    .sessionId(sessionId)
//                    .userId(userId)
//                    .build();
//            cart = cartRepository.save(cart);
//        }
//        return cart;
//    }
    private CartEntity getOrCreateCart(String sessionId, Long userId) {
        if (userId == null) {
            throw new RuntimeException("Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng.");
        }

        CartEntity cart = getCart(sessionId, userId);
        if (cart == null) {
            cart = CartEntity.builder()
                    .sessionId(sessionId)
                    .userId(userId)
                    .cartItems(new ArrayList<>())
                    .build();
            cart = cartRepository.save(cart);
        }
        return cart;
    }


    private CartEntity getCart(String sessionId, Long userId) {
        if (userId != null) {
            return cartRepository.findByUserId(userId).orElse(null);
        } else {
            return cartRepository.findBySessionId(sessionId).orElse(null);
        }
    }
    public void updateCartTotals(CartEntity cart) {
        List<CartItemEntity> items = cartItemRepository.findByCartId(cart.getId());
        BigDecimal total = items.stream()
                .map(CartItemEntity::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(total);
        cart.setTotalItems(items.size());
        cartRepository.save(cart);
    }



    public CartEntity updateCartItem(String sessionId, Long userId, Long productId, Integer quantity) {
        try {
            ProductEntity product = productService.getProductById(productId);
            if (product == null) {
                throw new RuntimeException("Sản phẩm không tồn tại");
            }

            if (quantity > product.getQuantity()) {
                throw new RuntimeException("Chỉ còn " + product.getQuantity() + " sản phẩm trong kho");
            }

            CartEntity cart = getOrCreateCart(sessionId, userId);

            Optional<CartItemEntity> optionalItem = cartItemRepository.findByCartAndProduct(cart, product);
            CartItemEntity cartItem;

            if (optionalItem.isPresent()) {
                cartItem = optionalItem.get();
                cartItem.setQuantity(quantity);
                cartItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
                cartItem.setUpdatedAt(LocalDateTime.now());
            } else {
                cartItem = new CartItemEntity();
                cartItem.setCart(cart);
                cartItem.setProduct(product);
                cartItem.setQuantity(quantity);
                cartItem.setUnitPrice(product.getPrice());
                cartItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
                cartItem.setCreatedAt(LocalDateTime.now());
                cartItem.setUpdatedAt(LocalDateTime.now());
            }

            cartItemRepository.save(cartItem);

            updateCartTotals(cart);
            return cart;

        } catch (Exception e) {
            logger.error("Error updating cart item: ", e);
            throw new RuntimeException("Có lỗi khi cập nhật giỏ hàng: " + e.getMessage());
        }
    }

}