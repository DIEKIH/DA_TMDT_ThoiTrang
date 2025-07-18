package com.example.da_tmdt_thoitrang.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

//    @GetMapping("/admin/vouchers")
//    public String vc(){
//        return "list1";
//    }

    @GetMapping("/admin/discounts")
    public String dc(){
        return "admin/products/discounts";
    }

    @GetMapping("/admin/product_discounts")
    public String pdc(){
        return "admin/products/product_discounts";
    }
}
