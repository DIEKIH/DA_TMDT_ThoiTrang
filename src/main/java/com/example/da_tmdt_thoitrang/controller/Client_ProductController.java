package com.example.da_tmdt_thoitrang.controller;

import com.example.da_tmdt_thoitrang.entity.ProductEntity;
import com.example.da_tmdt_thoitrang.service.Client_ProductService;
import com.example.da_tmdt_thoitrang.service.ProductService;
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

    @GetMapping("")
    public String listProducts(Model model) {
        List<ProductEntity> products = clientProductService.getAllActiveProducts();
        model.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        ProductEntity product = clientProductService.getProductById(id);

        // Tăng lượt xem
        clientProductService.incrementViewCount(id);

        // Lấy sản phẩm liên quan
        List<ProductEntity> relatedProducts = clientProductService
                .getRelatedProducts(id, product.getCategoryId(), 4);

        model.addAttribute("colors", product.getColor());
        model.addAttribute("sizes", product.getSize());
        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);
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
