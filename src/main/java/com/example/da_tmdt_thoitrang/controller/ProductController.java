package com.example.da_tmdt_thoitrang.controller;


import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import com.example.da_tmdt_thoitrang.entity.ProductImageEntity;
import com.example.da_tmdt_thoitrang.service.BrandService;
import com.example.da_tmdt_thoitrang.service.CategoryService;
import com.example.da_tmdt_thoitrang.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @ModelAttribute
    public void checkAdminAuth(HttpSession session, Model model) {
        if (session.getAttribute("adminLoggedIn") == null ||
                !(Boolean) session.getAttribute("adminLoggedIn")) {
            throw new RuntimeException("Unauthorized access");
        }
        model.addAttribute("adminUsername", session.getAttribute("adminUsername"));
    }

//    @GetMapping("/list")
//    public String listProducts(Model model) {
//        List<ProductEntity> products = productService.getAllProducts();
//        model.addAttribute("products", products);
//        return "admin/products/list";
//    }

    @GetMapping("/list")
    public String listProducts(Model model) {
        List<ProductEntity> products = productService.getAllProducts();
        model.addAttribute("products", products);

//        List<Product> products = productService.findAll();
//        model.addAttribute("products", products);

// Tính số lượng
        long activeCount = products.stream().filter(p -> p != null && Boolean.TRUE.equals(p.getIsActive())).count();
        long inactiveCount = products.stream().filter(p -> p != null && Boolean.FALSE.equals(p.getIsActive())).count();
        long lowStockCount = products.stream().filter(p -> p != null && p.getQuantity() != null && p.getQuantity() <= 10).count();

        model.addAttribute("activeCount", activeCount);
        model.addAttribute("inactiveCount", inactiveCount);
        model.addAttribute("lowStockCount", lowStockCount);

        return "admin/products/list";
    }


//    @GetMapping("/add")
//    public String showAddForm(Model model) {
//        model.addAttribute("product", new ProductEntity());
//        model.addAttribute("categories", categoryService.getAllCategories());
//        model.addAttribute("brands", brandService.getAllBrands());
//        model.addAttribute("sizes", getSizeList());
//        model.addAttribute("colors", getColorList());
//        return "admin/products/add";
//    }
//
//    @PostMapping("/add")
//    public String addProduct(@ModelAttribute ProductEntity product,
//                             @RequestParam("mainImage") MultipartFile mainImage,
//                             @RequestParam("additionalImages") MultipartFile[] additionalImages,
//                             @RequestParam(value = "selectedSizes", required = false) String[] selectedSizes,
//                             @RequestParam(value = "selectedColors", required = false) String[] selectedColors,
//                             RedirectAttributes redirectAttributes) {
//        try {
//            // Xử lý size và color
//            if (selectedSizes != null) {
//                product.setSize(String.join(",", selectedSizes));
//            }
//            if (selectedColors != null) {
//                product.setColor(String.join(",", selectedColors));
//            }
//
//            // Lưu ảnh chính
//            if (!mainImage.isEmpty()) {
//                String mainImageUrl = saveImage(mainImage);
//                product.setImageUrl(mainImageUrl);
//            }
//
//            // Lưu sản phẩm trước
//            ProductEntity savedProduct = productService.saveProduct(product);
//
//            // Xử lý ảnh phụ
//            if (additionalImages != null && additionalImages.length > 0) {
//                List<ProductImageEntity> productImages = new ArrayList<>();
//                int sortOrder = 1;
//
//                for (MultipartFile image : additionalImages) {
//                    if (!image.isEmpty()) {
//                        String imageUrl = saveImage(image);
//                        ProductImageEntity productImage = ProductImageEntity.builder()
//                                .productId(savedProduct.getId())
//                                .imageUrl(imageUrl)
//                                .isPrimary(false)
//                                .sortOrder(sortOrder++)
//                                .build();
//                        productImages.add(productImage);
//                    }
//                }
//
//                if (!productImages.isEmpty()) {
//                    productService.saveProductImages(productImages);
//                }
//            }
//
//            redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công!");
//            return "redirect:/admin/products/list";
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
//            return "redirect:/admin/products/add";
//        }
//    }
//
//    @GetMapping("/edit/{id}")
//    public String showEditForm(@PathVariable Long id, Model model) {
//        ProductEntity product = productService.getProductById(id);
//        if (product == null) {
//            return "redirect:/admin/products/list";
//        }
//
//        model.addAttribute("product", product);
//        model.addAttribute("categories", categoryService.getAllCategories());
//        model.addAttribute("brands", brandService.getAllBrands());
//        model.addAttribute("sizes", getSizeList());
//        model.addAttribute("colors", getColorList());
//
//        // Xử lý size và color hiện tại
//        if (product.getSize() != null) {
//            model.addAttribute("currentSizes", product.getSize().split(","));
//        }
//        if (product.getColor() != null) {
//            model.addAttribute("currentColors", product.getColor().split(","));
//        }
//
//        return "admin/products/add";
//    }
//
//    @PostMapping("/edit/{id}")
//    public String updateProduct(@PathVariable Long id,
//                                @ModelAttribute ProductEntity product,
//                                @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
//                                @RequestParam(value = "additionalImages", required = false) MultipartFile[] additionalImages,
//                                @RequestParam(value = "selectedSizes", required = false) String[] selectedSizes,
//                                @RequestParam(value = "selectedColors", required = false) String[] selectedColors,
//                                RedirectAttributes redirectAttributes) {
//        try {
//            ProductEntity existingProduct = productService.getProductById(id);
//            if (existingProduct == null) {
//                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm!");
//                return "redirect:/admin/products/list";
//            }
//
//            // Cập nhật thông tin cơ bản
//            existingProduct.setName(product.getName());
//            existingProduct.setDescription(product.getDescription());
//            existingProduct.setPrice(product.getPrice());
//            existingProduct.setQuantity(product.getQuantity());
//            existingProduct.setSku(product.getSku());
//            existingProduct.setCategoryId(product.getCategoryId());
//            existingProduct.setBrandId(product.getBrandId());
//            existingProduct.setIsActive(product.getIsActive());
//
//            // Xử lý size và color
//            if (selectedSizes != null) {
//                existingProduct.setSize(String.join(",", selectedSizes));
//            }
//            if (selectedColors != null) {
//                existingProduct.setColor(String.join(",", selectedColors));
//            }
//
//            // Cập nhật ảnh chính nếu có
//            if (mainImage != null && !mainImage.isEmpty()) {
//                String mainImageUrl = saveImage(mainImage);
//                existingProduct.setImageUrl(mainImageUrl);
//            }
//
//            // Lưu sản phẩm
//            productService.saveProduct(existingProduct);
//
//            // Xử lý ảnh phụ mới
//            if (additionalImages != null && additionalImages.length > 0) {
//                List<ProductImageEntity> newImages = new ArrayList<>();
//                int maxSortOrder = productService.getMaxSortOrderForProduct(id);
//
//                for (MultipartFile image : additionalImages) {
//                    if (!image.isEmpty()) {
//                        String imageUrl = saveImage(image);
//                        ProductImageEntity productImage = ProductImageEntity.builder()
//                                .productId(id)
//                                .imageUrl(imageUrl)
//                                .isPrimary(false)
//                                .sortOrder(++maxSortOrder)
//                                .build();
//                        newImages.add(productImage);
//                    }
//                }
//
//                if (!newImages.isEmpty()) {
//                    productService.saveProductImages(newImages);
//                }
//            }
//
//            redirectAttributes.addFlashAttribute("success", "Cập nhật sản phẩm thành công!");
//            return "redirect:/admin/products/list";
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
//            return "redirect:/admin/products/edit/" + id;
//        }
//    }
// Thêm các field cần thiết
@Value("${app.upload.dir:uploads}")
private String uploadDir;


    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new ProductEntity());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("sizes", getSizeList());
        model.addAttribute("colors", getColorList());
        return "admin/products/form"; // Đổi tên view thành form
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute ProductEntity product,
                             @RequestParam("mainImage") MultipartFile mainImage,
                             @RequestParam("additionalImages") MultipartFile[] additionalImages,
                             @RequestParam(value = "selectedSizes", required = false) String[] selectedSizes,
                             @RequestParam(value = "selectedColors", required = false) String[] selectedColors,
                             RedirectAttributes redirectAttributes) {
        try {
            // Xử lý size và color
            if (selectedSizes != null) {
                product.setSize(String.join(",", selectedSizes));
            }
            if (selectedColors != null) {
                product.setColor(String.join(",", selectedColors));
            }

            // Lưu ảnh chính
            if (!mainImage.isEmpty()) {
                String mainImageUrl = saveImage(mainImage);
                product.setImageUrl(mainImageUrl);
            }

            // Lưu sản phẩm trước
            ProductEntity savedProduct = productService.saveProduct(product);

            // Xử lý ảnh phụ
            if (additionalImages != null && additionalImages.length > 0) {
                List<ProductImageEntity> productImages = new ArrayList<>();
                int sortOrder = 1;

                for (MultipartFile image : additionalImages) {
                    if (!image.isEmpty()) {
                        String imageUrl = saveImage(image);
                        ProductImageEntity productImage = ProductImageEntity.builder()
                                .productId(savedProduct.getId())
                                .imageUrl(imageUrl)
                                .isPrimary(false)
                                .sortOrder(sortOrder++)
                                .build();
                        productImages.add(productImage);
                    }
                }

                if (!productImages.isEmpty()) {
                    productService.saveProductImages(productImages);
                }
            }

            redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công!");
            return "redirect:/admin/products/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/products/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProductEntity product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/admin/products/list";
        }

        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("sizes", getSizeList());
        model.addAttribute("colors", getColorList());

        // Xử lý size và color hiện tại
        if (product.getSize() != null) {
            model.addAttribute("currentSizes", product.getSize().split(","));
        }
        if (product.getColor() != null) {
            model.addAttribute("currentColors", product.getColor().split(","));
        }

        // Lấy danh sách ảnh phụ của sản phẩm
        List<ProductImageEntity> productImages = productService.getProductImagesByProductId(id);
        model.addAttribute("productImages", productImages);

        return "admin/products/form"; // Sử dụng cùng view với add
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute ProductEntity product,
                                @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
                                @RequestParam(value = "additionalImages", required = false) MultipartFile[] additionalImages,
                                @RequestParam(value = "selectedSizes", required = false) String[] selectedSizes,
                                @RequestParam(value = "selectedColors", required = false) String[] selectedColors,
                                RedirectAttributes redirectAttributes) {
        try {
            ProductEntity existingProduct = productService.getProductById(id);
            if (existingProduct == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm!");
                return "redirect:/admin/products/list";
            }

            // Cập nhật thông tin cơ bản
            existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setQuantity(product.getQuantity());
            existingProduct.setSku(product.getSku());
            existingProduct.setCategoryId(product.getCategoryId());
            existingProduct.setBrandId(product.getBrandId());
            existingProduct.setIsActive(product.getIsActive());

            // Xử lý size và color
            if (selectedSizes != null) {
                existingProduct.setSize(String.join(",", selectedSizes));
            } else {
                existingProduct.setSize(null);
            }
            if (selectedColors != null) {
                existingProduct.setColor(String.join(",", selectedColors));
            } else {
                existingProduct.setColor(null);
            }

            // Cập nhật ảnh chính nếu có
            if (mainImage != null && !mainImage.isEmpty()) {
                String mainImageUrl = saveImage(mainImage);
                existingProduct.setImageUrl(mainImageUrl);
            }

            // Lưu sản phẩm
            productService.saveProduct(existingProduct);

            // Xử lý ảnh phụ mới
            if (additionalImages != null && additionalImages.length > 0) {
                List<ProductImageEntity> newImages = new ArrayList<>();
                int maxSortOrder = productService.getMaxSortOrderForProduct(id);

                for (MultipartFile image : additionalImages) {
                    if (!image.isEmpty()) {
                        String imageUrl = saveImage(image);
                        ProductImageEntity productImage = ProductImageEntity.builder()
                                .productId(id)
                                .imageUrl(imageUrl)
                                .isPrimary(false)
                                .sortOrder(++maxSortOrder)
                                .build();
                        newImages.add(productImage);
                    }
                }

                if (!newImages.isEmpty()) {
                    productService.saveProductImages(newImages);
                }
            }

            redirectAttributes.addFlashAttribute("success", "Cập nhật sản phẩm thành công!");
            return "redirect:/admin/products/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/products/edit/" + id;
        }
    }

    // Thêm API để xóa ảnh phụ (dùng cho AJAX)
    @DeleteMapping("/images/delete/{imageId}")
    @ResponseBody
    public ResponseEntity<?> deleteProductImage(@PathVariable Long imageId) {
        try {
            ProductImageEntity productImage = productService.getProductImageById(imageId);
            if (productImage == null) {
                return ResponseEntity.notFound().build();
            }

            // Xóa file ảnh khỏi hệ thống
            deleteImageFile(productImage.getImageUrl());

            // Xóa record khỏi database
            productService.deleteProductImage(imageId);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi xóa ảnh: " + e.getMessage());
        }
    }

    // Phương thức helper để xóa file ảnh
    private void deleteImageFile(String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Lấy tên file từ URL
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                Path filePath = Paths.get(uploadDir, fileName);
                Files.deleteIfExists(filePath);
            }
        } catch (Exception e) {
            // Log lỗi nhưng không throw exception để không ảnh hưởng đến quá trình xóa record
            System.err.println("Không thể xóa file ảnh: " + e.getMessage());
        }
    }

    // Phương thức helper để lưu ảnh (nếu chưa có)
    private String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        // Tạo thư mục upload nếu chưa tồn tại
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Tạo tên file unique
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension;

        // Lưu file
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Trả về URL của file
        return "/uploads/" + fileName;
    }

//    // Phương thức helper để lấy danh sách size
//    private List<String> getSizeList() {
//        return Arrays.asList("XS", "S", "M", "L", "XL", "XXL", "XXXL");
//    }
//
//    // Phương thức helper để lấy danh sách màu sắc
//    private List<String> getColorList() {
//        return Arrays.asList("Đỏ", "Xanh dương", "Xanh lá", "Vàng", "Cam", "Tím", "Hồng",
//                "Đen", "Trắng", "Xám", "Nâu", "Be", "Xanh navy", "Xanh mint");
//    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa sản phẩm!");
        }
        return "redirect:/admin/products/list";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleProductStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ProductEntity product = productService.getProductById(id);
            if (product != null) {
                product.setIsActive(!product.getIsActive());
                productService.saveProduct(product);
                redirectAttributes.addFlashAttribute("success",
                        product.getIsActive() ? "Kích hoạt sản phẩm thành công!" : "Tạm ngưng sản phẩm thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra!");
        }
        return "redirect:/admin/products/list";
    }

//    @DeleteMapping("/image/{imageId}")
//    @ResponseBody
//    public String deleteProductImage(@PathVariable Long imageId) {
//        try {
//            productService.deleteProductImage(imageId);
//            return "success";
//        } catch (Exception e) {
//            return "error";
//        }
//    }

//    private String saveImage(MultipartFile file) throws IOException {
//        String uploadDir = "uploads/products/";
//        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
//        Path uploadPath = Paths.get(uploadDir);
//
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//
//        Path filePath = uploadPath.resolve(fileName);
//        Files.copy(file.getInputStream(), filePath);
//
//        return "/uploads/products/" + fileName;
//    }

    private List<String> getSizeList() {
        List<String> sizes = new ArrayList<>();
        sizes.add("XS");
        sizes.add("S");
        sizes.add("M");
        sizes.add("L");
        sizes.add("XL");
        sizes.add("XXL");
        sizes.add("XXXL");
        return sizes;
    }

    private List<String> getColorList() {
        List<String> colors = new ArrayList<>();
        colors.add("Đỏ");
        colors.add("Xanh dương");
        colors.add("Xanh lá");
        colors.add("Vàng");
        colors.add("Cam");
        colors.add("Tím");
        colors.add("Hồng");
        colors.add("Trắng");
        colors.add("Đen");
        colors.add("Xám");
        colors.add("Nâu");
        colors.add("Be");
        return colors;
    }
}