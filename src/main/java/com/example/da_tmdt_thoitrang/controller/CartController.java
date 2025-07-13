package com.example.da_tmdt_thoitrang.controller;

import com.example.da_tmdt_thoitrang.entity.CartEntity;
import com.example.da_tmdt_thoitrang.entity.CartItemEntity;
import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import com.example.da_tmdt_thoitrang.entity.UserEntity;
import com.example.da_tmdt_thoitrang.service.CartService;
import com.example.da_tmdt_thoitrang.service.ProductService;
import com.example.da_tmdt_thoitrang.service.ProductSuggestionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductSuggestionService productSuggestionService;



    private Long getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            UserEntity user = (UserEntity) session.getAttribute("user");
            if (user != null) {
                return user.getId();
            }
        }
        return null;
    }

//    @GetMapping
//    public List<CartItemEntity> list(@PathVariable Long cartId) {
//        return cartService.getItems(cartId);
//    }


    /**
     * Thêm sản phẩm vào giỏ hàng (AJAX)
     */
//    @PostMapping("/add")
//    @ResponseBody
//    public ResponseEntity<?> addToCart(
//            @RequestParam Long productId,
//            @RequestParam(defaultValue = "1") Integer quantity,
//            HttpServletRequest request) {
//
//        try {
//            String sessionId = request.getSession().getId();
//            Long userId = getCurrentUserId(request); // Implement method này để lấy user ID từ session
//
//            cartService.addToCart(sessionId, userId, productId, quantity);
//
//            // Trả về thông tin giỏ hàng mới
//            Integer cartCount = cartService.getCartItemCount(sessionId, userId);
//            BigDecimal cartTotal = cartService.getCartTotal(sessionId, userId);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("message", "Đã thêm sản phẩm vào giỏ hàng");
//            response.put("cartCount", cartCount);
//            response.put("cartTotal", cartTotal);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", false);
//            response.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }

//    @PostMapping("/add")
//    @ResponseBody
//    public ResponseEntity<?> addToCart(
//            @RequestParam Long productId,
//            @RequestParam(defaultValue = "1") Integer quantity,
//            HttpServletRequest request) {
//
//        String sessionId = request.getSession().getId();
//        Long userId = getCurrentUserId(request);
//
//        if (userId == null) {
//            // Chưa đăng nhập
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", false);
//            response.put("requireLogin", true);
//            response.put("message", "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng.");
//            return ResponseEntity.status(401).body(response);
//        }
//
//        try {
//            cartService.addToCart(sessionId, userId, productId, quantity);
//
//            Integer cartCount = cartService.getCartItemCount(sessionId, userId);
//            BigDecimal cartTotal = cartService.getCartTotal(sessionId, userId);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("message", "Đã thêm sản phẩm vào giỏ hàng");
//            response.put("cartCount", cartCount);
//            response.put("cartTotal", cartTotal);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", false);
//            response.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addToCart(
            @RequestParam Long productId,
            @RequestParam String color,
            @RequestParam String size,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpServletRequest request) {

        String sessionId = request.getSession().getId();
        Long userId = getCurrentUserId(request);

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "requireLogin", true,
                    "message", "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng."
            ));
        }

        try {
            cartService.addToCart(sessionId, userId, productId, color, size, quantity);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã thêm sản phẩm vào giỏ hàng",
                    "cartCount", cartService.getCartItemCount(sessionId, userId),
                    "cartTotal", cartService.getCartTotal(sessionId, userId)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }



    /**
     * Hiển thị trang giỏ hàng
     */
//    @GetMapping("")
//    public String viewCart(Model model, HttpServletRequest request) {
//        String sessionId = request.getSession().getId();
//        Long userId = getCurrentUserId(request);
//
//        CartEntity cart = cartService.getCartWithItems(sessionId, userId);
//
//
//        model.addAttribute("cart", cart);
//        model.addAttribute("cartItems", cart != null ? cart.getCartItems() : new ArrayList<>());
//        model.addAttribute("cartTotal", cart != null ? cart.getTotalAmount() : BigDecimal.ZERO);
//
//        return "client/carts/client_cart";
//    }
    @GetMapping("")
    public String viewCart(Model model, HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        Long userId = getCurrentUserId(request);

        CartEntity cart = cartService.getCartWithItems(sessionId, userId);

        List<CartItemEntity> cartItems = cart != null ? cart.getCartItems() : new ArrayList<>();

        // 👉 Gợi ý sản phẩm tương tự
        List<ProductEntity> suggestedProducts = productSuggestionService.getSuggestedProductsFromCart(cartItems);

        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cart != null ? cart.getTotalAmount() : BigDecimal.ZERO);
        model.addAttribute("suggestedProducts", suggestedProducts); // ✅ thêm vào

        return "client/carts/client_cart";
    }


//    @GetMapping("/checkout")
//    public String checkout(Model model, HttpServletRequest request) {
//
//        return "client/carts/checkout";
//    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ (AJAX)
     */
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateCartItem(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            HttpServletRequest request) {

        try {
            String sessionId = request.getSession().getId();
            Long userId = getCurrentUserId(request);


            // ✅ Kiểm tra sản phẩm có tồn tại không
            ProductEntity product = productService.getProductById(productId);
            if (product == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Sản phẩm không tồn tại"));
            }

            // ✅ Kiểm tra số lượng hợp lệ
            if (quantity < 1) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Số lượng phải lớn hơn 0"));
            }

            // ✅ Kiểm tra tồn kho
            if (quantity > product.getQuantity()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "Chỉ còn " + product.getQuantity() + " sản phẩm trong kho",
                                "maxQuantity", product.getQuantity()
                        ));
            }

            // ✅ Cập nhật giỏ hàng
            CartEntity cart = cartService.updateCartItem(sessionId, userId, productId, quantity);

            // ✅ Tính subtotal cho item này
            BigDecimal itemSubtotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));

            // ✅ Tính tổng giỏ hàng
            BigDecimal cartTotal = cartService.getCartTotal(sessionId, userId);
            int cartCount = cartService.getCartItemCount(sessionId, userId);

            cartService.updateCartItemQuantity(sessionId, userId, productId, quantity);

            // Trả về thông tin giỏ hàng mới
//            Integer cartCount = cartService.getCartItemCount(sessionId, userId);
//            BigDecimal cartTotal = cartService.getCartTotal(sessionId, userId);

//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("cartCount", cartCount);
//            response.put("cartTotal", cartTotal);
//
//            return ResponseEntity.ok(response);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã cập nhật số lượng sản phẩm");
            response.put("cartTotal", cartTotal);
            response.put("cartCount", cartCount);
            response.put("itemSubtotal", itemSubtotal);
            response.put("newQuantity", quantity);
            response.put("productStock", product.getQuantity());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(response);
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Có lỗi xảy ra: " + e.getMessage()));
        }
    }
    // ✅ API kiểm tra tồn kho trước khi thêm vào giỏ
    @GetMapping("/check-stock")
    @ResponseBody
    public ResponseEntity<?> checkProductStock(@RequestParam Long productId) {
        try {
            ProductEntity product = productService.getProductById(productId);
            if (product == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Sản phẩm không tồn tại"));
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("productId", productId);
            result.put("stockQuantity", product.getQuantity());
            result.put("isAvailable", product.getQuantity() > 0);
            result.put("price", product.getPrice());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng (AJAX)
     */
    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<?> removeFromCart(
            @RequestParam Long productId,
            HttpServletRequest request) {

        try {
            String sessionId = request.getSession().getId();
            Long userId = getCurrentUserId(request);

            cartService.removeFromCart(sessionId, userId, productId);

            // Trả về thông tin giỏ hàng mới
            Integer cartCount = cartService.getCartItemCount(sessionId, userId);
            BigDecimal cartTotal = cartService.getCartTotal(sessionId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã xóa sản phẩm khỏi giỏ hàng");
            response.put("cartCount", cartCount);
            response.put("cartTotal", cartTotal);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Xóa toàn bộ giỏ hàng
     */
    @PostMapping("/clear")
    @ResponseBody
    public ResponseEntity<?> clearCart(HttpServletRequest request) {
        try {
            String sessionId = request.getSession().getId();
            Long userId = getCurrentUserId(request);

            cartService.clearCart(sessionId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã xóa toàn bộ giỏ hàng");
            response.put("cartCount", 0);
            response.put("cartTotal", BigDecimal.ZERO);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Lấy số lượng items trong giỏ (AJAX)
     */
    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Integer> getCartCount(HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        Long userId = getCurrentUserId(request);

        Integer count = cartService.getCartItemCount(sessionId, userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/item-subtotal")
    @ResponseBody
    public ResponseEntity<?> getItemSubtotal(@RequestParam Long productId,
                                             @RequestParam Integer quantity,
                                             HttpServletRequest request) {
        try {
            String sessionId = request.getSession().getId();
            Long userId = getCurrentUserId(request);

            ProductEntity product = productService.getProductById(productId);
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));

            Map<String, Object> result = new HashMap<>();
            result.put("subtotal", subtotal);
            result.put("subtotalFormatted", NumberFormat.getNumberInstance().format(subtotal) + "đ");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    // Helper method - implement theo authentication system của bạn
//    private Long getCurrentUserId(HttpServletRequest request) {
//        // Implement logic để lấy user ID từ session/authentication
//        // Ví dụ:
//        // HttpSession session = request.getSession();
//        // UserEntity user = (UserEntity) session.getAttribute("user");
//        // return user != null ? user.getId() : null;
//        return null; // Trả về null nếu chưa đăng nhập
//    }
}
