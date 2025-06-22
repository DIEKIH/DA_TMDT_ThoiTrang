package com.example.da_tmdt_thoitrang.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class UserController {


//    @GetMapping("/index1")
//    public String admin_index(){
//        return "index";
//    }

    @GetMapping("/navbar")
    public String navbar() {
        return "test/test_navbar_ad";
    }

}
