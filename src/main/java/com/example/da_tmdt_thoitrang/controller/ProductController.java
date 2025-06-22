package com.example.da_tmdt_thoitrang.controller;

import com.example.da_tmdt_thoitrang.dto.CreateProductRequest;
import com.example.da_tmdt_thoitrang.dto.ProductDTO;
import com.example.da_tmdt_thoitrang.dto.ProductImageDTO;
import com.example.da_tmdt_thoitrang.dto.UpdateProductRequest;
import com.example.da_tmdt_thoitrang.service.BrandService;
import com.example.da_tmdt_thoitrang.service.CategoryService;
import com.example.da_tmdt_thoitrang.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;



    @ModelAttribute
    public void checkAdminAuth(HttpSession session, Model model) {
        if (session.getAttribute("adminLoggedIn") == null ||
                !(Boolean) session.getAttribute("adminLoggedIn")) {
            throw new RuntimeException("Unauthorized access");
        }
        model.addAttribute("adminUsername", session.getAttribute("adminUsername"));
    }

    // ===== VIEW CONTROLLERS (Trả về giao diện HTML) =====

//    @GetMapping
//    public String getAllProducts(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "id") String sortBy,
//            @RequestParam(defaultValue = "desc") String sortDir,
//            @RequestParam(required = false) String status,
//            @RequestParam(required = false) String name,
//            @RequestParam(required = false) Long categoryId,
//            @RequestParam(required = false) Long brandId,
//            @RequestParam(required = false) BigDecimal minPrice,
//            @RequestParam(required = false) BigDecimal maxPrice,
//            Model model) {
//
//        Sort sort = sortDir.equalsIgnoreCase("desc") ?
//                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        Page<ProductDTO> products;
//
//        // If search parameters are provided, use search
//        if (name != null || categoryId != null || brandId != null ||
//                minPrice != null || maxPrice != null) {
//            products = productService.searchProducts(name, categoryId, brandId, minPrice, maxPrice, pageable);
//        } else {
//            // Filter by status
//            if ("active".equals(status)) {
//                products = productService.getActiveProducts(pageable);
//            } else if ("inactive".equals(status)) {
//                products = productService.getInactiveProducts(pageable);
//            } else if ("out_of_stock".equals(status)) {
//                products = productService.getOutOfStockProducts(pageable);
//            } else if ("low_stock".equals(status)) {
//                products = productService.getLowStockProducts(pageable);
//            } else {
//                products = productService.getAllProducts(pageable);
//            }
//        }
//
//        model.addAttribute("products", products);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", products.getTotalPages());
//        model.addAttribute("totalElements", products.getTotalElements());
//        model.addAttribute("sortBy", sortBy);
//        model.addAttribute("sortDir", sortDir);
//        model.addAttribute("status", status);
//        model.addAttribute("name", name);
//        model.addAttribute("categoryId", categoryId);
//        model.addAttribute("brandId", brandId);
//        model.addAttribute("minPrice", minPrice);
//        model.addAttribute("maxPrice", maxPrice);
//
//        return "admin/products/list";
//    }

    @GetMapping
    public String getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Model model) {

        // Chuyển các giá trị filter rỗng về null
        if (name != null && name.trim().isEmpty()) name = null;
        if (categoryId != null && categoryId == 0) categoryId = null;
        if (brandId != null && brandId == 0) brandId = null;

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductDTO> products;

        boolean hasSearch = name != null || categoryId != null || brandId != null
                || minPrice != null || maxPrice != null;

        if (hasSearch) {
            products = productService.searchProducts(name, categoryId, brandId, minPrice, maxPrice, pageable);
        } else if ("active".equals(status)) {
            products = productService.getActiveProducts(pageable);
        } else if ("inactive".equals(status)) {
            products = productService.getInactiveProducts(pageable);
        } else if ("out_of_stock".equals(status)) {
            products = productService.getOutOfStockProducts(pageable);
        } else if ("low_stock".equals(status)) {
            products = productService.getLowStockProducts(pageable);
        } else {
            products = productService.getAllProducts(pageable);
        }

        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("totalElements", products.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("status", status);
        model.addAttribute("name", name);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("brandId", brandId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands());

        return "admin/products/list";
    }


    @GetMapping("/{id}")
    public String getProductById(@PathVariable Long id, Model model) {
        try {
            ProductDTO product = productService.getProductById(id);
            model.addAttribute("product", product);
            return "admin/products/detail";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/admin/products";
        }
    }
    @PostMapping("/bulk")
    public String handleBulkAction(
            @RequestParam List<Long> productIds,
            @RequestParam String action,
            RedirectAttributes redirectAttributes) {
        try {
            switch (action) {
                case "activate":
                    productService.bulkActivate(productIds);
                    redirectAttributes.addFlashAttribute("success", "Đã kích hoạt các sản phẩm đã chọn.");
                    break;
                case "deactivate":
                    productService.bulkDeactivate(productIds);
                    redirectAttributes.addFlashAttribute("success", "Đã ngừng hoạt động các sản phẩm đã chọn.");
                    break;
                case "delete":
                    productService.bulkDelete(productIds);
                    redirectAttributes.addFlashAttribute("success", "Đã xóa các sản phẩm đã chọn.");
                    break;
                default:
                    redirectAttributes.addFlashAttribute("error", "Hành động không hợp lệ.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xử lý hàng loạt: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }


    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new CreateProductRequest());
        // Add categories and brands for dropdowns
         model.addAttribute("categories", categoryService.getAllCategories());
         model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("formAction", "/admin/products");
        model.addAttribute("isEdit", false);

        return "admin/products/form";
    }

    @PostMapping("/admin/products/save")
    public String saveProduct(@ModelAttribute CreateProductRequest request) {
        // xử lý thêm sản phẩm
        return "redirect:/admin/products";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            ProductDTO product = productService.getProductById(id);
            UpdateProductRequest updateRequest = new UpdateProductRequest();
            // Map product to updateRequest
            updateRequest.setName(product.getName());
            updateRequest.setDescription(product.getDescription());
            updateRequest.setSize(product.getSize());
            updateRequest.setColor(product.getColor());
            updateRequest.setQuantity(product.getQuantity());
            updateRequest.setPrice(product.getPrice());
            updateRequest.setSku(product.getSku());
            updateRequest.setCategoryId(product.getCategoryId());
            updateRequest.setBrandId(product.getBrandId());

            model.addAttribute("product", updateRequest);
            model.addAttribute("productId", id);

            model.addAttribute("formAction", "/admin/products/update/" + id);
            model.addAttribute("isEdit", true);

            // Add categories and brands for dropdowns
            // model.addAttribute("categories", categoryService.getAllCategories());
            // model.addAttribute("brands", brandService.getAllBrands());
            return "admin/products/form";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/admin/products";
        }
    }

    @PostMapping
    public String createProduct(@Valid @ModelAttribute CreateProductRequest request,
                                RedirectAttributes redirectAttributes) {
        try {
            ProductDTO createdProduct = productService.createProduct(request);
            redirectAttributes.addFlashAttribute("success", "Tạo sản phẩm thành công!");
            return "redirect:/admin/products/" + createdProduct.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/products/create";
        }
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute UpdateProductRequest request,
                                RedirectAttributes redirectAttributes) {
        try {
            ProductDTO updatedProduct = productService.updateProduct(id, request);
            redirectAttributes.addFlashAttribute("success", "Cập nhật sản phẩm thành công!");
            return "redirect:/admin/products/" + updatedProduct.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/products/edit" + id ;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleProductStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ProductDTO product = productService.toggleProductStatus(id);
            String message = product.getIsActive() ? "Kích hoạt sản phẩm thành công!" : "Tạm ngưng sản phẩm thành công!";
            redirectAttributes.addFlashAttribute("success", message);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/update-stock/{id}")
    public String updateStock(@PathVariable Long id,
                              @RequestParam Integer quantity,
                              RedirectAttributes redirectAttributes) {
        try {
            productService.updateStock(id, quantity);
            redirectAttributes.addFlashAttribute("success", "Cập nhật tồn kho thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/products/" + id;
    }

    @PostMapping("/upload-images/{id}")
    public String uploadProductImages(@PathVariable Long id,
                                      @RequestParam("files") List<MultipartFile> files,
                                      RedirectAttributes redirectAttributes) {
        try {
            List<ProductImageDTO> images = productService.uploadProductImages(id, files);
            redirectAttributes.addFlashAttribute("success", "Upload ảnh thành công! Đã thêm " + images.size() + " ảnh.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/products/" + id;
    }

    // ===== API ENDPOINTS (Trả về JSON cho AJAX) =====

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Page<ProductDTO>> getAllProductsApi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductDTO> products;
        if ("active".equals(status)) {
            products = productService.getActiveProducts(pageable);
        } else if ("inactive".equals(status)) {
            products = productService.getInactiveProducts(pageable);
        } else if ("out_of_stock".equals(status)) {
            products = productService.getOutOfStockProducts(pageable);
        } else if ("low_stock".equals(status)) {
            products = productService.getLowStockProducts(pageable);
        } else {
            products = productService.getAllProducts(pageable);
        }

        return ResponseEntity.ok(products);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<ProductDTO> getProductByIdApi(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<Page<ProductDTO>> searchProductsApi(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductDTO> products = productService.searchProducts(
                name, categoryId, brandId, minPrice, maxPrice, pageable);

        return ResponseEntity.ok(products);
    }
}