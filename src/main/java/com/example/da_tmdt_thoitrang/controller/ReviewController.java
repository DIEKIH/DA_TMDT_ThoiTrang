package com.example.da_tmdt_thoitrang.controller;

import com.example.da_tmdt_thoitrang.entity.UserEntity;
import com.example.da_tmdt_thoitrang.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

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

    @PostMapping("/submit")
    public String submitReview(@RequestParam("productId") Long productId,
                               @RequestParam("rating") Integer rating,
                               @RequestParam("comment") String comment,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {

        Long userId = getCurrentUserId(request); // 🔁 tuỳ vào hệ thống đăng nhập

        // 👉 Kiểm tra nếu chưa đăng nhập
        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để đánh giá sản phẩm.");
            return "redirect:/client/login"; // hoặc quay lại trang sản phẩm nếu muốn
        }

        // ✅ Nếu đã đăng nhập thì xử lý đánh giá
        reviewService.submitReview(userId, productId, rating, comment);

        redirectAttributes.addFlashAttribute("successMessage", "Đánh giá của bạn đã được gửi và đang chờ duyệt.");
        return "redirect:/product/" + productId;
    }

//    private Long getCurrentUserId(HttpServletRequest request) {
//        // Tuỳ vào cách bạn lấy user đang login
//        return 1L;
//    }
}
