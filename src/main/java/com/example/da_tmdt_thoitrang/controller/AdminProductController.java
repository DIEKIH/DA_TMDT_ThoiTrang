package com.example.da_tmdt_thoitrang.controller;

import com.example.da_tmdt_thoitrang.dto.ProductDTO;
import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import com.example.da_tmdt_thoitrang.entity.UserEntity;
import com.example.da_tmdt_thoitrang.service.ProductService;
import com.example.da_tmdt_thoitrang.service.CategoryService;
import com.example.da_tmdt_thoitrang.service.BrandService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

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

    @GetMapping("/vouchers")
    public String viewVoucher(Model model){
        return "admin/products/vouchers";
    }

    @GetMapping("/discounts")
    public String viewDiscount(Model model){
        return "admin/products/discounts";
    }

    @GetMapping("/product_discounts")
    public String viewProductDiscount(Model model){
        return "admin/products/product_discounts";
    }
    // Hiển thị danh sách sản phẩm
    @GetMapping("/products")
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isActive,
            HttpSession session, // <-- THÊM DÒNG NÀY
            Model model) {

        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }

        Sort sort = sortDir.equals("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductEntity> products = productService.getFilteredProducts(
                search, categoryId, isActive, pageable);

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("totalElements", products.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("search", search);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("isActive", isActive);

        return "admin/products/list";
    }


    // Hiển thị chi tiết sản phẩm
    @GetMapping("/products/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        ProductEntity product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/admin/products";
        }

        model.addAttribute("product", product);
        return "admin/products/detail";
    }

    // Trang thêm sản phẩm
    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new ProductDTO());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands());
        return "admin/products/form";
    }

    // Xử lý thêm sản phẩm
    @PostMapping("/products/add")
    public String addProduct(
            @Valid @ModelAttribute("product") ProductDTO productDTO,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrands());
            return "admin/products/form";
        }

        try {
            ProductEntity savedProduct = productService.saveProduct(productDTO, imageFile, imageFiles);
            redirectAttributes.addFlashAttribute("success",
                    "Thêm sản phẩm '" + savedProduct.getName() + "' thành công!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrands());
            return "admin/products/form";
        }
    }

    // Trang sửa sản phẩm
    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        ProductEntity product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/admin/products";
        }

        ProductDTO productDTO = convertToDTO(product);
        model.addAttribute("product", productDTO);
        model.addAttribute("productEntity", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("isEdit", true);

        return "admin/products/form";
    }

    // Xử lý sửa sản phẩm
    @PostMapping("/products/edit/{id}")
    public String editProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("product") ProductDTO productDTO,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            ProductEntity product = productService.getProductById(id);
            model.addAttribute("productEntity", product);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrands());
            model.addAttribute("isEdit", true);
            return "admin/products/form";
        }

        try {
            productDTO.setId(id);
            ProductEntity updatedProduct = productService.updateProduct(productDTO, imageFile, imageFiles);
            redirectAttributes.addFlashAttribute("success",
                    "Cập nhật sản phẩm '" + updatedProduct.getName() + "' thành công!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            ProductEntity product = productService.getProductById(id);
            model.addAttribute("productEntity", product);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrands());
            model.addAttribute("isEdit", true);
            return "admin/products/form";
        }
    }

    // Xóa sản phẩm (soft delete)
    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ProductEntity product = productService.getProductById(id);
            if (product != null) {
                productService.softDeleteProduct(id);
                redirectAttributes.addFlashAttribute("success",
                        "Xóa sản phẩm '" + product.getName() + "' thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // Kích hoạt/vô hiệu hóa sản phẩm
    @PostMapping("/products/toggle-status/{id}")
    public String toggleProductStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ProductEntity product = productService.toggleProductStatus(id);
            String status = product.getIsActive() ? "kích hoạt" : "vô hiệu hóa";
            redirectAttributes.addFlashAttribute("success",
                    "Đã " + status + " sản phẩm '" + product.getName() + "'!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // API để lấy thống kê sản phẩm
    @GetMapping("/products/stats")
    @ResponseBody
    public Map<String, Object> getProductStats() {
        return productService.getProductStats();
    }

    // Chuyển đổi Entity sang DTO
    private ProductDTO convertToDTO(ProductEntity product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .basePrice(product.getBasePrice())
                .categoryId(product.getCategoryId())
                .brandId(product.getBrandId())
                .isActive(product.getIsActive())
                .build();
    }
}
