package com.example.da_tmdt_thoitrang.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", true);
        response.put("message", ex.getMessage());
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", true);
        response.put("message", "Có lỗi xảy ra trong hệ thống");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

//    @ExceptionHandler(RuntimeException.class)
//    public String handleRuntimeException(RuntimeException e, RedirectAttributes redirectAttributes) {
//        redirectAttributes.addFlashAttribute("error", e.getMessage());
//        return "redirect:/admin/discounts";
//    }
//
//    @ExceptionHandler(Exception.class)
//    public String handleGeneralException(Exception e, Model model) {
//        model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
//        return "error/general";
//    }
}
