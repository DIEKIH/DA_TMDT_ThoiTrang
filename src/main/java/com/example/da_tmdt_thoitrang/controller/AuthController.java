package com.example.da_tmdt_thoitrang.controller;

import com.example.da_tmdt_thoitrang.dto.UserRegistrationDto;
import com.example.da_tmdt_thoitrang.dto.UserLoginDto;
import com.example.da_tmdt_thoitrang.entity.UserEntity;
import com.example.da_tmdt_thoitrang.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/navbar")
    public String navbar() {
        return "test/test_navbar_ad";
    }

    // Trang chủ
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // Trang admin
    @GetMapping("/admin/admin_main")
    public String adminIndex(HttpSession session, Model model) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/admin/login";
        }
        model.addAttribute("user", user);
        return "admin/admin_main";
    }

    // Admin Login Page
    @GetMapping("/admin/login")
    public String adminLoginPage(Model model) {
        model.addAttribute("loginDto", new UserLoginDto());
        return "admin/login";
    }

    // Admin Login Process
//    @PostMapping("/admin/login")
//    public String adminLogin(@ModelAttribute UserLoginDto loginDto,
//                             HttpSession session,
//                             RedirectAttributes redirectAttributes) {
//        try {
//            UserEntity user = userService.authenticateAdmin(loginDto.getUsername(), loginDto.getPassword());
//            if (user != null) {
//                session.setAttribute("user", user); // ✅ Tự lưu session
//                return "redirect:/admin/admin_main";
//            } else {
//                redirectAttributes.addFlashAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
//                return "redirect:/admin/login";
//            }
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
//            return "redirect:/admin/login";
//        }
//    }

    @PostMapping("/admin/login")
    public String adminLogin(@ModelAttribute UserLoginDto loginDto,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            UserEntity user = userService.authenticateAdmin(loginDto.getUsername(), loginDto.getPassword());
            if (user != null) {
                session.setAttribute("user", user); // ✅
                session.setAttribute("adminLoggedIn", true); // ✅ Cần thêm
                session.setAttribute("adminUsername", user.getUsername()); // ✅ Cần thêm
                return "redirect:/admin/admin_main";
            } else {
                redirectAttributes.addFlashAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
                return "redirect:/admin/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "redirect:/admin/login";
        }
    }



    // Admin Register Page
    @GetMapping("/admin/register")
    public String adminRegisterPage(Model model) {
        model.addAttribute("registrationDto", new UserRegistrationDto());
        return "admin/register";
    }

    // Admin Register Process
    @PostMapping("/admin/register")
    public String adminRegister(@ModelAttribute UserRegistrationDto registrationDto,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.registerAdmin(registrationDto);
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/admin/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đăng ký thất bại: " + e.getMessage());
            return "redirect:/admin/register";
        }
    }

//    @GetMapping("/admin/logout")
//    public String adminLogout(HttpSession session) {
//        session.invalidate(); // Xóa toàn bộ session
//        return "redirect:/admin/login";
//    }


    // Client Login Page
    @GetMapping("/client/login")
    public String clientLoginPage(Model model) {
        model.addAttribute("loginDto", new UserLoginDto());
        return "client/login";
    }

    // Client Login Process
    @PostMapping("/client/login")
    public String clientLogin(@ModelAttribute UserLoginDto loginDto,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
            UserEntity user = userService.authenticateClient(loginDto.getUsername(), loginDto.getPassword());
            if (user != null) {
                session.setAttribute("user", user);
                return "redirect:/";
            } else {
                redirectAttributes.addFlashAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
                return "redirect:/client/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "redirect:/client/login";
        }
    }

    // Client Register Page
    @GetMapping("/client/register")
    public String clientRegisterPage(Model model) {
        model.addAttribute("registrationDto", new UserRegistrationDto());
        return "client/register";
    }

    // Client Register Process
    @PostMapping("/client/register")
    public String clientRegister(@ModelAttribute UserRegistrationDto registrationDto,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.registerClient(registrationDto);
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/client/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đăng ký thất bại: " + e.getMessage());
            return "redirect:/client/register";
        }
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Đăng xuất thành công!");

        if (user != null && user.isAdmin()) {
            return "redirect:/admin/login";
        }
        return "redirect:/";
    }
}