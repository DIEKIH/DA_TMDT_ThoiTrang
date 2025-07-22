package com.example.da_tmdt_thoitrang.controller;



import com.example.da_tmdt_thoitrang.dto.DiscountDTO;
import com.example.da_tmdt_thoitrang.entity.DiscountEntity;
import com.example.da_tmdt_thoitrang.entity.ProductDiscountEntity;
import com.example.da_tmdt_thoitrang.enums.DiscountType;
import com.example.da_tmdt_thoitrang.enums.ValueType;
import com.example.da_tmdt_thoitrang.service.DiscountService;
import com.example.da_tmdt_thoitrang.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;

@Controller
@RequestMapping("/admin/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;
    private final ProductService productService;

    @GetMapping
    public String listDiscounts(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<DiscountEntity> discounts = discountService.getAllDiscounts(pageable);

        model.addAttribute("discounts", discounts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", discounts.getTotalPages());

        return "admin/discount/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("discountDTO", new DiscountDTO());
        model.addAttribute("discountTypes", DiscountType.values());
        model.addAttribute("valueTypes", ValueType.values());


        return "admin/discount/create";
    }

    @PostMapping("/create")
    public String createDiscount(@Valid @ModelAttribute DiscountDTO discountDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("discountTypes", DiscountType.values());
            model.addAttribute("valueTypes", ValueType.values());
            return "admin/discount/create";
        }

        try {
            discountService.createDiscount(discountDTO);
            redirectAttributes.addFlashAttribute("success", "Tạo chương trình giảm giá thành công!");
            return "redirect:/admin/discounts";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("discountTypes", DiscountType.values());
            model.addAttribute("valueTypes", ValueType.values());
            return "admin/discount/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            DiscountEntity discount = discountService.getDiscountById(id);
            DiscountDTO discountDTO = DiscountDTO.builder()
                    .id(discount.getId())
                    .name(discount.getName())
                    .description(discount.getDescription())
                    .type(discount.getType())
                    .value(discount.getValue())
                    .valueType(discount.getValueType())
                    .startDate(discount.getStartDate())
                    .endDate(discount.getEndDate())
                    .isActive(discount.getIsActive())
                    .build();

            model.addAttribute("discountDTO", discountDTO);
            model.addAttribute("discountTypes", DiscountType.values());
            model.addAttribute("valueTypes", ValueType.values());
            return "admin/discount/edit";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/admin/discounts";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateDiscount(@PathVariable Long id,
                                 @Valid @ModelAttribute DiscountDTO discountDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("discountTypes", DiscountType.values());
            model.addAttribute("valueTypes", ValueType.values());
            return "admin/discount/edit";
        }

        try {
            discountService.updateDiscount(id, discountDTO);
            redirectAttributes.addFlashAttribute("success", "Cập nhật chương trình giảm giá thành công!");
            return "redirect:/admin/discounts";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("discountTypes", DiscountType.values());
            model.addAttribute("valueTypes", ValueType.values());
            return "admin/discount/edit";
        }
    }

    @PostMapping("/{id}/soft-delete")
    public String softDeleteDiscount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            discountService.softDeleteDiscount(id);
            redirectAttributes.addFlashAttribute("success", "Đã ngưng chương trình giảm giá!");
            return "redirect:/admin/discounts";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/discounts";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteDiscount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            discountService.deleteDiscount(id);
            redirectAttributes.addFlashAttribute("success", "Xóa chương trình giảm giá thành công!");
            return "redirect:/admin/discounts";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/discounts";
        }
    }

    @GetMapping("/{id}/products")
    public String manageProducts(@PathVariable Long id, Model model) {
        try {
            DiscountEntity discount = discountService.getDiscountById(id);
            List<ProductDiscountEntity> productsInDiscount = discountService.getProductsInDiscount(id);

            model.addAttribute("discount", discount);
            model.addAttribute("productsInDiscount", productsInDiscount);
            model.addAttribute("allProducts", productService.getAllActiveProducts());

            return "admin/discount/products";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/admin/discounts";
        }
    }

    @PostMapping("/{discountId}/products/{productId}/assign")
    public String assignProduct(@PathVariable Long discountId,
                                @PathVariable Long productId,
                                RedirectAttributes redirectAttributes) {
        try {
            discountService.assignProductToDiscount(discountId, productId);
            redirectAttributes.addFlashAttribute("success", "Gán sản phẩm vào chương trình thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/discounts/" + discountId + "/products";
    }

    @PostMapping("/{discountId}/products/{productId}/remove")
    public String removeProduct(@PathVariable Long discountId,
                                @PathVariable Long productId,
                                RedirectAttributes redirectAttributes) {
        try {
            discountService.removeProductFromDiscount(discountId, productId);
            redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm khỏi chương trình thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/discounts/" + discountId + "/products";
    }
}
