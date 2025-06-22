package com.example.da_tmdt_thoitrang.controller;

import com.example.da_tmdt_thoitrang.entity.BrandEntity;
import com.example.da_tmdt_thoitrang.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    // Interceptor để kiểm tra đăng nhập admin
    @ModelAttribute
    public void checkAdminAuth(HttpSession session, Model model) {
        if (session.getAttribute("adminLoggedIn") == null ||
                !(Boolean) session.getAttribute("adminLoggedIn")) {
            throw new RuntimeException("Unauthorized access");
        }
        model.addAttribute("adminUsername", session.getAttribute("adminUsername"));
    }

    // Hiển thị danh sách thương hiệu
    @GetMapping
    public String listBrands(Model model, @RequestParam(required = false) String search) {
        List<BrandEntity> brands;
        if (search != null && !search.trim().isEmpty()) {
            brands = brandService.searchBrands(search);
            model.addAttribute("search", search);
        } else {
            brands = brandService.getAllBrands();
        }
        model.addAttribute("brands", brands);
        return "admin/brand/list";
    }

    // Hiển thị form thêm thương hiệu
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("brand", new BrandEntity());
        model.addAttribute("isEdit", false);
        return "admin/brand/form";
    }

    // Xử lý thêm thương hiệu
    @PostMapping("/add")
    public String addBrand(@Valid @ModelAttribute BrandEntity brand,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/brand/form";
        }

        try {
            brandService.saveBrand(brand);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm thương hiệu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/brands";
    }
//     @PostMapping("/add")
// public String addBrand(@ModelAttribute Brand brand, 
//                       @RequestParam("logoFile") MultipartFile logoFile) {
//     if (!logoFile.isEmpty()) {
//         String logoUrl = fileStorageService.store(logoFile);
//         brand.setLogoUrl(logoUrl);
//     }
//     brandService.saveBrand(brand);
//     return "redirect:/admin/brands";
//     }

    // Hiển thị form sửa thương hiệu
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<BrandEntity> brand = brandService.getBrandById(id);
        if (brand.isPresent()) {
            model.addAttribute("brand", brand.get());
            model.addAttribute("isEdit", true);
            return "admin/brand/form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thương hiệu!");
            return "redirect:/admin/brands";
        }
    }

    // Xử lý cập nhật thương hiệu
    @PostMapping("/edit/{id}")
    public String updateBrand(@PathVariable Long id,
                              @Valid @ModelAttribute BrandEntity brand,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/brand/form";
        }

        try {
            brand.setId(id);
            brandService.saveBrand(brand);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thương hiệu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/brands";
    }

    // Xóa thương hiệu (soft delete)
    @PostMapping("/delete/{id}")
    public String deleteBrand(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            brandService.deleteBrand(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa thương hiệu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/brands";
    }

    // Kích hoạt thương hiệu
    @PostMapping("/activate/{id}")
    public String activateBrand(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            brandService.activateBrand(id);
            redirectAttributes.addFlashAttribute("successMessage", "Kích hoạt thương hiệu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/brands";
    }

    // Vô hiệu hóa thương hiệu
    @PostMapping("/deactivate/{id}")
    public String deactivateBrand(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            brandService.deactivateBrand(id);
            redirectAttributes.addFlashAttribute("successMessage", "Vô hiệu hóa thương hiệu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/brands";
    }
}