package com.example.da_tmdt_thoitrang.controller;

import com.example.da_tmdt_thoitrang.entity.CategoryEntity;
import com.example.da_tmdt_thoitrang.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @ModelAttribute
    public void checkAdminAuth(HttpSession session, Model model) {
        if (session.getAttribute("adminLoggedIn") == null ||
                !(Boolean) session.getAttribute("adminLoggedIn")) {
            throw new RuntimeException("Unauthorized access");
        }
        model.addAttribute("adminUsername", session.getAttribute("adminUsername"));
    }

    @GetMapping
    public String listCategories(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "all") String status,
            Model model) {

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("search", search);
        model.addAttribute("status", status);

        return "admin/categories/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new CategoryEntity());
        model.addAttribute("action", "create");
        model.addAttribute("formAction", "/admin/categories/create");

        return "admin/categories/form";
    }

    @PostMapping("/create")
    public String createCategory(
            @ModelAttribute CategoryEntity category,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        try {
            categoryService.saveCategory(category, imageFile);
            redirectAttributes.addFlashAttribute("successMessage", "Danh mục đã được tạo thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CategoryEntity category = categoryService.findById(id);
            model.addAttribute("category", category);
            model.addAttribute("action", "edit");
            model.addAttribute("formAction", "/admin/categories/edit/" + category.getId());

            return "admin/categories/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy danh mục!");
            return "redirect:/admin/categories";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(
            @PathVariable Long id,
            @ModelAttribute CategoryEntity category,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        try {
            category.setId(id);
            categoryService.saveCategory(category, imageFile);
            redirectAttributes.addFlashAttribute("successMessage", "Danh mục đã được cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Danh mục đã được xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa danh mục: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.toggleStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Trạng thái danh mục đã được cập nhật!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }
}