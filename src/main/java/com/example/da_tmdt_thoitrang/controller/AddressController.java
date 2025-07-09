package com.example.da_tmdt_thoitrang.controller;

import com.example.da_tmdt_thoitrang.entity.*;
import com.example.da_tmdt_thoitrang.service.AddressService;
import com.example.da_tmdt_thoitrang.service.CartService;
import com.example.da_tmdt_thoitrang.service.OrderItemService;
import com.example.da_tmdt_thoitrang.service.OrderService;
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
        addressService.saveAddress(address);
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
        return "client/carts/checkout";
    }

    @GetMapping("/edit/{id}")
    public String editAddress(@PathVariable Long id, HttpServletRequest request, Model model) {
        Long userId = getCurrentUserId(request);
        Optional<AddressEntity> address = addressService.getAddressById(id, userId);
        address.ifPresent(value -> model.addAttribute("address", value));
        return "client/carts/checkout";
    }

    @GetMapping("/delete/{id}")
    public String deleteAddress(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        addressService.deleteAddress(id, userId);
        return "redirect:/checkout";
    }

//    @PostMapping("/checkout/confirm")
//    public String confirmOrder(HttpServletRequest request, RedirectAttributes redirectAttributes) {
//        Long userId = getCurrentUserId(request);
//        if (userId == null) return "redirect:/login";
//
//        List<CartItemEntity> cartItems = cartService.getCartItems(userId);
//        if (cartItems.isEmpty()) {
//            redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống.");
//            return "redirect:/checkout";
//        }
//
//        // Tính tổng đơn hàng
//        BigDecimal totalAmount = cartItems.stream()
//                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        BigDecimal shippingFee = new BigDecimal("30000"); // ví dụ ship 30k
//        BigDecimal discountAmount = BigDecimal.ZERO; // Chưa có giảm giá
//        BigDecimal finalAmount = totalAmount.add(shippingFee).subtract(discountAmount);
//
//        // Tạo đơn hàng
//        OrderEntity order = OrderEntity.builder()
//                .userId(userId)
//                .orderNumber(UUID.randomUUID().toString())
//                .totalAmount(totalAmount)
//                .discountAmount(discountAmount)
//                .shippingFee(shippingFee)
//                .finalAmount(finalAmount)
//                .paymentStatus(OrderEntity.PaymentStatus.PENDING)
//                .status(OrderEntity.OrderStatus.PENDING)
//                .shippingAddress("Lấy từ địa chỉ đã chọn hoặc mặc định")
//                .build();
//
//        orderService.save(order);
//
//        // Thêm các sản phẩm vào đơn hàng
//        for (CartItemEntity item : cartItems) {
//            OrderItemEntity orderItem = new OrderItemEntity();
//            orderItem.setOrderId(order.getId());
//            orderItem.setProduct(item.getProduct());
//            orderItem.setQuantity(item.getQuantity());
//            orderItem.setUnitPrice(item.getProduct().getPrice());
//            // Không cần setTotalPrice nếu đã có phương thức tính
//
//            orderItemService.save(orderItem);
//        }
//
//        // Xóa giỏ hàng
//        cartService.clearCart(request.getSession().getId(), userId);
//
//        // Chuyển đến trang cảm ơn hoặc xem đơn hàng
//        return "redirect:/order/thank-you";
//    }
//
//    @PostMapping("/checkout/confirm")
//    public String confirmOrder(@RequestParam("paymentMethod") String paymentMethod, HttpServletRequest request) {
//        // Ví dụ xử lý
//        if (paymentMethod.equals("BANK")) {
//            // check nếu có yêu cầu chuyển khoản
//        } else if (paymentMethod.equals("CARD")) {
//            // kiểm tra thông tin thẻ nếu cần
//        }
//
//        // Lưu order với:
//        OrderEntity order = OrderEntity.builder()
//                .paymentStatus(OrderEntity.PaymentStatus.PENDING)
//                .notes("Phương thức: " + paymentMethod)
//                // ...
//                .build();
//
//        return "redirect:/order/thank-you";
//    }

    @PostMapping("/confirm")
    public String confirmOrder(@RequestParam("paymentMethod") String paymentMethod,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        Long userId = getCurrentUserId(request);
        if (userId == null) return "redirect:/client/login";

        List<CartItemEntity> cartItems = cartService.getCartItems(userId);
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống.");
            return "redirect:/checkout";
        }

        // Tính tổng tiền
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingFee = new BigDecimal("30000");
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = totalAmount.add(shippingFee).subtract(discountAmount);

        // TODO: lấy địa chỉ mặc định hoặc địa chỉ chọn
        String shippingAddress = "Địa chỉ giao hàng mặc định hoặc đã chọn";

        OrderEntity order = OrderEntity.builder()
                .userId(userId)
                .orderNumber(UUID.randomUUID().toString())
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .shippingFee(shippingFee)
                .finalAmount(finalAmount)
                .paymentStatus(OrderEntity.PaymentStatus.PENDING)
                .status(OrderEntity.OrderStatus.PENDING)
                .shippingAddress(shippingAddress)
                .notes("Phương thức: " + paymentMethod)
                .build();

        orderService.save(order);

        for (CartItemEntity item : cartItems) {
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrderId(order.getId());
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(item.getProduct().getPrice());

            orderItemService.save(orderItem);
        }

        cartService.clearCart(userId); // truyền userId nếu CartEntity theo userId

        return "redirect:/checkout/order"; // ✔ chuyển đến trang danh sách đơn hàng
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

