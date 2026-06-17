package com.justjava.ecommerce.controller;

import com.justjava.ecommerce.service.ProductApprovalService;
import com.justjava.ecommerce.service.ProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/admin/products")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductQueryService   productQueryService;
    private final ProductApprovalService productApprovalService;

    @GetMapping("/pending")
    public String pendingReview(
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        var products = productQueryService.getPendingProducts(
                PageRequest.of(page, 20, Sort.by("createdAt").ascending()));
        model.addAttribute("products", products);
        return "admin/products/pending";
    }

    @GetMapping("/pending/{id}")
    public String reviewDetail(@PathVariable UUID id, Model model) {
        var product = productQueryService.getProductById(id);
        model.addAttribute("product", product);
        return "admin/products/review";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable UUID id, RedirectAttributes flash) {
        try {
            productApprovalService.approve(id);
            flash.addFlashAttribute("success", "Product approved and published.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/products/pending";
    }

    @PostMapping("/{id}/reject")
    public String reject(
            @PathVariable UUID         id,
            @RequestParam String       reason,
            RedirectAttributes         flash
    ) {
        try {
            productApprovalService.reject(id, reason);
            flash.addFlashAttribute("success", "Product rejected.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/products/pending";
    }
}
