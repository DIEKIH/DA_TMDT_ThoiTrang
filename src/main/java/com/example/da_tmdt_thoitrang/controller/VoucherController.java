package com.example.da_tmdt_thoitrang.controller;


import com.example.da_tmdt_thoitrang.dto.VoucherDto;
import com.example.da_tmdt_thoitrang.entity.VoucherEntity;
import com.example.da_tmdt_thoitrang.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping
    public String listVouchers(Model model) {
        List<VoucherEntity> vouchers = voucherService.getAllVouchers();
        model.addAttribute("vouchers", vouchers);
        return "admin/vouchers/list";
    }

    @GetMapping("/create")
    public String createVoucherForm(Model model) {
        model.addAttribute("voucherDto", new VoucherDto());
        model.addAttribute("voucherTypes", VoucherEntity.VoucherType.values());
        model.addAttribute("isEdit", false);
        return "admin/vouchers/form";
    }

    @PostMapping("/create")
    public String createVoucher(@Valid @ModelAttribute VoucherDto voucherDto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("voucherTypes", VoucherEntity.VoucherType.values());
            return "admin/vouchers/form";
        }

        try {
            voucherService.createVoucher(voucherDto);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo voucher thành công!");
            return "redirect:/admin/vouchers";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("voucherTypes", VoucherEntity.VoucherType.values());
            return "admin/vouchers/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editVoucherForm(@PathVariable Long id, Model model) {
        Optional<VoucherEntity> voucher = voucherService.getVoucherById(id);
        if (voucher.isEmpty()) {
            return "redirect:/admin/vouchers";
        }

        model.addAttribute("voucherDto", new VoucherDto(voucher.get()));
        model.addAttribute("voucherTypes", VoucherEntity.VoucherType.values());
        model.addAttribute("isEdit", true);
        return "admin/vouchers/form";
    }

    @PostMapping("/edit/{id}")
    public String editVoucher(@PathVariable Long id,
                              @Valid @ModelAttribute VoucherDto voucherDto,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("voucherTypes", VoucherEntity.VoucherType.values());
            model.addAttribute("isEdit", true);
            return "admin/vouchers/form";
        }

        try {
            voucherService.updateVoucher(id, voucherDto);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật voucher thành công!");
            return "redirect:/admin/vouchers";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("voucherTypes", VoucherEntity.VoucherType.values());
            model.addAttribute("isEdit", true);
            return "admin/vouchers/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteVoucher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            voucherService.deleteVoucher(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa voucher thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/vouchers";
    }
}


