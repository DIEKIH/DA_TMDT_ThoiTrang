package com.example.da_tmdt_thoitrang.controller;

import com.example.da_tmdt_thoitrang.entity.*;
import com.example.da_tmdt_thoitrang.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/checkout")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private VoucherService voucherService;

    private Long getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            UserEntity user = (UserEntity) session.getAttribute("user");
            if (user != null) return user.getId();
        }
        return null;
    }

@PostMapping("/save-address")
public String saveAddress(@ModelAttribute AddressEntity address, HttpServletRequest request) {
    Long userId = getCurrentUserId(request);
    if (userId == null) return "redirect:/client/login";

    address.setUserId(userId);

    if (address.getId() != null) {
        // Địa chỉ đã tồn tại → cập nhật
        addressService.updateAddress(address);
    } else {
        // Thêm mới
        addressService.saveAddress(address);
    }

    return "redirect:/checkout";
}


    @GetMapping
    public String showCheckoutPage(Model model, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) return "redirect:/client/login";

        String sessionId = request.getSession().getId();

        CartEntity cart = cartService.getCartWithItems(sessionId, userId);

        model.addAttribute("cartItems", cart != null ? cart.getCartItems() : new ArrayList<>());
        model.addAttribute("cartTotal", cart != null ? cart.getTotalAmount() : BigDecimal.ZERO);

        List<AddressEntity> addresses = addressService.getUserAddresses(userId);
        model.addAttribute("addresses", addresses);
        model.addAttribute("address", new AddressEntity());

        model.addAttribute("user", userService.getUserById(userId)); // hoặc cách lấy user của bạn

        return "client/carts/checkout";
    }

    @GetMapping("/edit/{id}")
    public String editAddress(@PathVariable Long id, HttpServletRequest request, Model model) {
        Long userId = getCurrentUserId(request);
        Optional<AddressEntity> address = addressService.getAddressById(id, userId);
        if (address.isPresent()) {
            model.addAttribute("address", address.get());
        } else {
            return "redirect:/checkout";
        }

        // Bổ sung lại thông tin để trang không bị thiếu
        CartEntity cart = cartService.getCartWithItems(request.getSession().getId(), userId);
        model.addAttribute("cartItems", cart != null ? cart.getCartItems() : new ArrayList<>());
        model.addAttribute("cartTotal", cart != null ? cart.getTotalAmount() : BigDecimal.ZERO);

        List<AddressEntity> addresses = addressService.getUserAddresses(userId);
        model.addAttribute("addresses", addresses);

        return "client/carts/checkout";
    }


    @GetMapping("/delete/{id}")
    public String deleteAddress(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        addressService.deleteAddress(id, userId);
        return "redirect:/checkout";
    }
//    @PostMapping("/confirm")
//    public String confirmOrder(@RequestParam("paymentMethod") String paymentMethod,
//                               @RequestParam("selectedAddressId") Long selectedAddressId,
//                               @RequestParam(value = "voucherCode", required = false) String voucherCode,
//                               HttpServletRequest request,
//                               RedirectAttributes redirectAttributes) {
//
//        Long userId = getCurrentUserId(request);
//        if (userId == null) return "redirect:/client/login";
//
//        List<CartItemEntity> cartItems = cartService.getCartItems(userId);
//        if (cartItems.isEmpty()) {
//            redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống.");
//            return "redirect:/checkout";
//        }
//
//        Optional<AddressEntity> selectedAddressOpt = addressService.getAddressById(selectedAddressId, userId);
//        if (selectedAddressOpt.isEmpty()) {
//            redirectAttributes.addFlashAttribute("error", "Không tìm thấy địa chỉ giao hàng.");
//            return "redirect:/checkout";
//        }
//
//        AddressEntity selectedAddress = selectedAddressOpt.get();
//        String shippingAddress = selectedAddress.getStreet() + ", " +
//                selectedAddress.getWard() + ", " +
//                selectedAddress.getDistrict() + ", " +
//                selectedAddress.getCity();
//
//        BigDecimal totalAmount = cartItems.stream()
//                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        BigDecimal shippingFee = new BigDecimal("30000");
//
//        BigDecimal discountAmount = BigDecimal.ZERO;
//        VoucherEntity appliedVoucher = null; // ✅ Khai báo biến ở đây để dùng sau
//
//        // Xử lý mã giảm giá
//        if (voucherCode != null && !voucherCode.isEmpty()) {
//            VoucherService.VoucherValidationResult result = voucherService.validateVoucher(voucherCode, userId, totalAmount);
//            if (result.isValid()) {
//                appliedVoucher = voucherService.findByCode(voucherCode); // bạn cần chắc chắn hàm này trả về đúng
//                if (appliedVoucher != null) {
//                    discountAmount = result.getDiscountAmount();
//                    voucherService.markVoucherUsed(voucherCode, userId);
//                } else {
//                    redirectAttributes.addFlashAttribute("error", "Voucher không tồn tại.");
//                    return "redirect:/checkout";
//                }
//            } else {
//                redirectAttributes.addFlashAttribute("error", "Voucher không hợp lệ: " + result.getMessage());
//                return "redirect:/checkout";
//            }
//        }
//
//        BigDecimal finalAmount = totalAmount.add(shippingFee).subtract(discountAmount);
//
//        OrderEntity order = OrderEntity.builder()
//                .userId(userId)
//                .voucherId(appliedVoucher != null ? appliedVoucher.getId() : null)
//                .orderNumber(UUID.randomUUID().toString())
//                .totalAmount(totalAmount)
//                .discountAmount(discountAmount)
//                .shippingFee(shippingFee)
//                .finalAmount(finalAmount)
//                .paymentStatus(OrderEntity.PaymentStatus.PENDING)
//                .status(OrderEntity.OrderStatus.PENDING)
//                .shippingAddress(shippingAddress)
//                .notes("Phương thức: " + paymentMethod + (voucherCode != null ? " | Voucher: " + voucherCode : ""))
//                .build();
//        orderService.save(order);
//
//        for (CartItemEntity item : cartItems) {
//            OrderItemEntity orderItem = new OrderItemEntity();
//            orderItem.setOrderId(order.getId());
//            orderItem.setProduct(item.getProduct());
//            orderItem.setQuantity(item.getQuantity());
//            orderItem.setUnitPrice(item.getProduct().getPrice());
//            orderItemService.save(orderItem);
//
//            int newStock = item.getProduct().getQuantity() - item.getQuantity();
//            item.getProduct().setQuantity(Math.max(newStock, 0));
//            productService.saveProductAfterPurchase(item.getProduct().getId(), item.getQuantity());
//        }
//
//        cartService.clearCart(userId);
//        return "redirect:/checkout/order";
//    }

//    @PostMapping("/confirm")
//    public String confirmOrder(@RequestParam("paymentMethod") String paymentMethod,
//                               @RequestParam("selectedAddressId") Long selectedAddressId,
//                               @RequestParam(value = "voucherCode", required = false) String voucherCode,
//                               HttpServletRequest request,
//                               RedirectAttributes redirectAttributes) {
//
//        Long userId = getCurrentUserId(request);
//        if (userId == null) return "redirect:/client/login";
//
//        // Debug log
//        System.out.println("=== CONFIRM ORDER DEBUG ===");
//        System.out.println("User ID: " + userId);
//        System.out.println("Voucher Code: " + voucherCode);
//        System.out.println("Payment Method: " + paymentMethod);
//        System.out.println("Selected Address ID: " + selectedAddressId);
//
//        List<CartItemEntity> cartItems = cartService.getCartItems(userId);
//        if (cartItems.isEmpty()) {
//            redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống.");
//            return "redirect:/checkout";
//        }
//
//        Optional<AddressEntity> selectedAddressOpt = addressService.getAddressById(selectedAddressId, userId);
//        if (selectedAddressOpt.isEmpty()) {
//            redirectAttributes.addFlashAttribute("error", "Không tìm thấy địa chỉ giao hàng.");
//            return "redirect:/checkout";
//        }
//
//        AddressEntity selectedAddress = selectedAddressOpt.get();
//        String shippingAddress = selectedAddress.getStreet() + ", " +
//                selectedAddress.getWard() + ", " +
//                selectedAddress.getDistrict() + ", " +
//                selectedAddress.getCity();
//
//        // Tính tổng tiền sản phẩm
//        BigDecimal totalAmount = cartItems.stream()
//                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        BigDecimal shippingFee = new BigDecimal("30000");
//        BigDecimal discountAmount = BigDecimal.ZERO;
//        VoucherEntity appliedVoucher = null;
//
//        // Xử lý voucher
//        if (voucherCode != null && !voucherCode.trim().isEmpty()) {
//            System.out.println("Processing voucher: " + voucherCode);
//
//            try {
//                // Validate voucher
//                VoucherService.VoucherValidationResult result = voucherService.validateVoucher(
//                        voucherCode.trim(), userId, totalAmount.add(shippingFee)
//                );
//
//                System.out.println("Voucher validation result: " + result.isValid());
//                System.out.println("Voucher message: " + result.getMessage());
//
//                if (result.isValid()) {
//                    appliedVoucher = result.getVoucher();
//                    discountAmount = result.getDiscountAmount();
//
//                    System.out.println("Applied voucher: " + appliedVoucher.getCode());
//                    System.out.println("Discount amount: " + discountAmount);
//
//                    // Đánh dấu voucher đã sử dụng
//                    voucherService.markVoucherUsed(voucherCode.trim(), userId);
//                    System.out.println("Voucher marked as used");
//
//                } else {
//                    System.out.println("Voucher validation failed: " + result.getMessage());
//                    redirectAttributes.addFlashAttribute("error", "Voucher không hợp lệ: " + result.getMessage());
//                    return "redirect:/checkout";
//                }
//            } catch (Exception e) {
//                System.out.println("Error processing voucher: " + e.getMessage());
//                e.printStackTrace();
//                redirectAttributes.addFlashAttribute("error", "Lỗi xử lý voucher: " + e.getMessage());
//                return "redirect:/checkout";
//            }
//        }
//
//        // Tính tổng tiền cuối cùng
//        BigDecimal finalAmount = totalAmount.add(shippingFee).subtract(discountAmount);
//
//        System.out.println("Final calculation:");
//        System.out.println("Total Amount: " + totalAmount);
//        System.out.println("Shipping Fee: " + shippingFee);
//        System.out.println("Discount Amount: " + discountAmount);
//        System.out.println("Final Amount: " + finalAmount);
//
//        // Tạo đơn hàng
//        OrderEntity order = OrderEntity.builder()
//                .userId(userId)
//                .voucher(appliedVoucher) // Quan trọng: Set voucher entity
//                .orderNumber("ORD" + System.currentTimeMillis())
//                .totalAmount(totalAmount)
//                .discountAmount(discountAmount)
//                .shippingFee(shippingFee)
//                .finalAmount(finalAmount)
//                .paymentStatus(OrderEntity.PaymentStatus.PENDING)
//                .status(OrderEntity.OrderStatus.PENDING)
//                .shippingAddress(shippingAddress)
//                .notes("Phương thức: " + paymentMethod +
//                        (appliedVoucher != null ? " | Voucher: " + appliedVoucher.getCode() : ""))
//                .build();
//
//        // Lưu đơn hàng
//        OrderEntity savedOrder = orderService.save(order);
//        System.out.println("Order saved with ID: " + savedOrder.getId());
//
//        // Tạo order items
//        for (CartItemEntity item : cartItems) {
//            OrderItemEntity orderItem = new OrderItemEntity();
//            orderItem.setOrderId(savedOrder.getId());
//            orderItem.setProduct(item.getProduct());
//            orderItem.setQuantity(item.getQuantity());
//            orderItem.setUnitPrice(item.getProduct().getPrice());
//            orderItemService.save(orderItem);
//
//            // Cập nhật stock
//            int newStock = item.getProduct().getQuantity() - item.getQuantity();
//            item.getProduct().setQuantity(Math.max(newStock, 0));
//            productService.saveProductAfterPurchase(item.getProduct().getId(), item.getQuantity());
//        }
//
//        // Xóa giỏ hàng
//        cartService.clearCart(userId);
//
//        System.out.println("Order process completed successfully");
//        return "redirect:/checkout/order";
//    }
@PostMapping("/confirm")
public String confirmOrder(@RequestParam("paymentMethod") String paymentMethod,
                           @RequestParam("selectedAddressId") Long selectedAddressId,
                           @RequestParam(value = "voucherCode", required = false) String voucherCode,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {

    Long userId = getCurrentUserId(request);
    if (userId == null) return "redirect:/client/login";

    List<CartItemEntity> cartItems = cartService.getCartItems(userId);
    if (cartItems.isEmpty()) {
        redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống.");
        return "redirect:/checkout";
    }

    Optional<AddressEntity> selectedAddressOpt = addressService.getAddressById(selectedAddressId, userId);
    if (selectedAddressOpt.isEmpty()) {
        redirectAttributes.addFlashAttribute("error", "Không tìm thấy địa chỉ giao hàng.");
        return "redirect:/checkout";
    }

    AddressEntity selectedAddress = selectedAddressOpt.get();
    String shippingAddress = selectedAddress.getStreet() + ", " +
            selectedAddress.getWard() + ", " +
            selectedAddress.getDistrict() + ", " +
            selectedAddress.getCity();

    BigDecimal totalAmount = cartItems.stream()
            .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal shippingFee = new BigDecimal("30000");
    BigDecimal discountAmount = BigDecimal.ZERO;
    VoucherEntity appliedVoucher = null;

    // Xử lý voucher nếu có
    if (voucherCode != null && !voucherCode.trim().isEmpty()) {
        System.out.println("Processing voucher: " + voucherCode);

        try {
            // Validate voucher
            VoucherService.VoucherValidationResult result = voucherService.validateVoucher(
                    voucherCode.trim(), userId, totalAmount.add(shippingFee)
            );

            if (result.isValid()) {
                appliedVoucher = result.getVoucher();
                discountAmount = result.getDiscountAmount();

                System.out.println("Voucher OK: " + appliedVoucher.getCode());
                System.out.println("Discount: " + discountAmount);
            } else {
                redirectAttributes.addFlashAttribute("error", "Voucher không hợp lệ: " + result.getMessage());
                return "redirect:/checkout";
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi xử lý voucher.");
            return "redirect:/checkout";
        }
    }

// Tính tổng cuối cùng
    BigDecimal finalAmount = totalAmount.add(shippingFee).subtract(discountAmount);

// Tạo đơn hàng (bao gồm voucher)
    OrderEntity order = OrderEntity.builder()
            .userId(userId)
            .voucher(appliedVoucher)  // <- Đảm bảo không null
            .orderNumber("ORD" + System.currentTimeMillis())
            .totalAmount(totalAmount)
            .discountAmount(discountAmount)
            .shippingFee(shippingFee)
            .finalAmount(finalAmount)
            .paymentStatus(OrderEntity.PaymentStatus.PENDING)
            .status(OrderEntity.OrderStatus.PENDING)
            .shippingAddress(shippingAddress)
            .notes("Phương thức: " + paymentMethod +
                    (appliedVoucher != null ? " | Voucher: " + appliedVoucher.getCode() : ""))
            .build();

    OrderEntity savedOrder = orderService.save(order);

// 👇 Sau khi lưu xong, GHI NHẬN voucher usage nếu có
    if (appliedVoucher != null) {
        voucherService.markVoucherUsed(appliedVoucher.getCode(), userId);
        System.out.println("Voucher marked as used and saved to voucher_usages");
    }


    for (CartItemEntity item : cartItems) {
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrderId(savedOrder.getId());
        orderItem.setProduct(item.getProduct());
        orderItem.setQuantity(item.getQuantity());
        orderItem.setUnitPrice(item.getProduct().getPrice());
        orderItemService.save(orderItem);

        // Cập nhật tồn kho
        int newStock = item.getProduct().getQuantity() - item.getQuantity();
        item.getProduct().setQuantity(Math.max(newStock, 0));
        productService.saveProductAfterPurchase(item.getProduct().getId(), item.getQuantity());
    }

    // Xóa giỏ hàng
    cartService.clearCart(userId);

    return "redirect:/checkout/order";
}



    @GetMapping("/order")
    public String viewOrders(HttpServletRequest request, Model model) {
        Long userId = getCurrentUserId(request);
        System.out.println("==> User ID hiện tại: " + userId); // THÊM DÒNG NÀY

        if (userId == null) return "redirect:/client/login";

        List<OrderEntity> orders = orderService.findByUserId(userId);
        System.out.println("==> Số đơn hàng tìm được: " + orders.size()); // THÊM DÒNG NÀY

        model.addAttribute("orders", orders);
        return "client/orders/list";
    }

    @GetMapping("/order/detail/{id}")
    public String viewOrderDetail(@PathVariable Long id, Model model) {
        OrderEntity order = orderService.findById(id);
        model.addAttribute("order", order);
        return "client/orders/detail";
    }

}

