package com.example.da_tmdt_thoitrang.controller;


import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import com.example.da_tmdt_thoitrang.entity.ReviewEntity;
import com.example.da_tmdt_thoitrang.entity.UserEntity;
import com.example.da_tmdt_thoitrang.service.Client_ProductService;
import com.example.da_tmdt_thoitrang.service.ProductService;
import com.example.da_tmdt_thoitrang.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

//@Controller
//@RequestMapping("/")
//public class Client_ProductController {
//
//    @Autowired
//    private Client_ProductService clientProductService;
//    @GetMapping("")
//    public String listProducts(Model model) {
//        List<ProductEntity> products = clientProductService.getAllActiveProducts();
//        model.addAttribute("products", products);
//        return "index";
//    }
//
////    @GetMapping("/product/{id}")
////    public String productDetail(@PathVariable Long id, Model model) {
////        ProductEntity product = clientProductService.getProductById(id);
////        model.addAttribute("product", product);
////        return "product_detail";
////    }
//    @GetMapping("/product/{id}")
//    public String productDetail(@PathVariable Long id, Model model) {
//        ProductEntity product = clientProductService.getProductById(id);
//
//        // Tăng view count
//        clientProductService.incrementViewCount(id);
//
//        model.addAttribute("product", product);
//        return "product_detail";
//    }
//}

@Controller
@RequestMapping("/")
public class Client_ProductController {

    @Autowired
    private Client_ProductService clientProductService;

    @Autowired
    private ReviewService reviewService;

//    @GetMapping("")
//    public String listProducts(Model model) {
//        List<ProductEntity> products = clientProductService.getAllActiveProducts();
//        model.addAttribute("products", products);
//        return "index";
//    }

    @GetMapping("")
    public String listProducts(HttpSession session, Model model) {
        List<ProductEntity> products = clientProductService.getAllActiveProducts();


        UserEntity user = (UserEntity) session.getAttribute("user");
        List<ProductEntity> recommendedProducts;

        if (user != null) {
            recommendedProducts = reviewService.recommendForUser(user.getId(), 8);

            if (recommendedProducts == null || recommendedProducts.isEmpty()) {
                // nếu không có sản phẩm được gợi ý thì gọi top 8 sản phẩm phổ biến
                recommendedProducts = reviewService.getTop8RecommendedProducts();
            }
        } else {
            recommendedProducts = reviewService.getTop8RecommendedProducts();
        }

        // nếu vẫn không có thì set list rỗng để không lỗi Thymeleaf
        if (recommendedProducts == null) {
            recommendedProducts = List.of();
        }

        model.addAttribute("products", products);
        model.addAttribute("recommendedProducts", recommendedProducts);

        return "index";
    }




//    @GetMapping("/product/{id}")
//    public String productDetail(@PathVariable Long id, Model model) {
//        ProductEntity product = clientProductService.getProductById(id);
//
//        // Tăng lượt xem
//        clientProductService.incrementViewCount(id);
//
//        // Lấy sản phẩm liên quan
//        List<ProductEntity> relatedProducts = clientProductService
//                .getRelatedProducts(id, product.getCategoryId(), 4);
//
//        model.addAttribute("colors", product.getColor());
//        model.addAttribute("sizes", product.getSize());
//        model.addAttribute("product", product);
//        model.addAttribute("relatedProducts", relatedProducts);
//        return "client/product_client/product_detail";
//    }
//@GetMapping("/product/{id}")
//public String productDetail(@PathVariable Long id, Model model) {
//    ProductEntity product = clientProductService.getProductById(id);
//
//    // Tăng lượt xem
//    clientProductService.incrementViewCount(id);
//
//    // Lấy sản phẩm liên quan
//    List<ProductEntity> relatedProducts = clientProductService
//            .getRelatedProducts(id, product.getCategoryId(), 4);
//
//    // 👉 Lấy đánh giá đã duyệt của sản phẩm
//    List<ReviewEntity> reviews = reviewService.getApprovedReviewsByProduct(id);
////    List<ReviewDTO> reviews = reviewService.getReviewDTOs(id);
//
//    model.addAttribute("colors", product.getColor());
//    model.addAttribute("sizes", product.getSize());
//    model.addAttribute("product", product);
//    model.addAttribute("relatedProducts", relatedProducts);
////    model.addAttribute("reviews", reviewService.getReviewDTOs(id));
//    model.addAttribute("reviews", reviews); // ✅ Gửi đánh giá sang view
//
//    return "client/product_client/product_detail";
//}

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        ProductEntity product = clientProductService.getProductById(id);
        clientProductService.incrementViewCount(id);

        List<ProductEntity> relatedProducts = clientProductService
                .getRelatedProducts(id, product.getCategoryId(), 4);

        List<ReviewEntity> reviews = reviewService.getApprovedReviewsByProduct(id);

        model.addAttribute("colors", product.getColor());
        model.addAttribute("sizes", product.getSize());
        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("reviews", reviews); // dùng entity gốc, có sẵn user

        return "client/product_client/product_detail";
    }


//        @GetMapping("/product/{id}")
//        public String productDetail(@PathVariable Long id, Model model) {
//            ProductEntity product = clientProductService.getProductById(id);
//
//            // Debug dữ liệu ra log để chắc chắn
//            System.out.println("Colors: " + product.getColor());
//            System.out.println("Sizes: " + product.getSize());
//
//            model.addAttribute("product", product);
//            model.addAttribute("colors", product.getAvailableColors());
//            model.addAttribute("sizes", product.getAvailableSizes());
//
//            return "client/product_client/product_detail";
//        }


}
