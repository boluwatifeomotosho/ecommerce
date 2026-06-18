package com.justjava.ecommerce.product;

import com.justjava.ecommerce.product.ProductApprovalService;
import com.justjava.ecommerce.product.ProductQueryService;
import com.justjava.ecommerce.product.dto.ProductFilter;
import com.justjava.ecommerce.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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

    private final ProductQueryService    productQueryService;
    private final ProductApprovalService productApprovalService;

    @GetMapping("/catalog")
    public String catalog(
            @ModelAttribute ProductFilter filter,
            @RequestParam(defaultValue = "0")      int    page,
            @RequestParam(defaultValue = "newest") String sort,
            Authentication auth,
            Model model
    ) {
        var sortObj  = switch (sort) {
            case "price-asc"  -> Sort.by("price").ascending();
            case "price-desc" -> Sort.by("price").descending();
            case "name"       -> Sort.by("name").ascending();
            default           -> Sort.by("createdAt").descending();
        };
        var products = productQueryService.getPublishedProducts(filter, PageRequest.of(page, 20, sortObj));
        model.addAttribute("products", products);
        model.addAttribute("filter",   filter);
        model.addAttribute("sort",     sort);
        enrichModel(auth, model);
        return "admin/products/catalog";
    }

    @GetMapping("/pending")
    public String pendingReview(
            @RequestParam(defaultValue = "0") int page,
            Authentication auth,
            Model model
    ) {
        var products = productQueryService.getPendingProducts(
                PageRequest.of(page, 20, Sort.by("createdAt").ascending()));
        model.addAttribute("products", products);
        enrichModel(auth, model);
        return "admin/products/pending";
    }

    @GetMapping("/pending/{id}")
    public String reviewDetail(@PathVariable UUID id, Authentication auth, Model model) {
        var product = productQueryService.getProductById(id);
        model.addAttribute("product", product);
        enrichModel(auth, model);
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

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void enrichModel(Authentication auth, Model model) {
        Object principal = auth.getPrincipal();
        if (principal instanceof OidcUser u) {
            model.addAttribute("name",  u.getFullName() != null ? u.getFullName() : u.getEmail());
            model.addAttribute("email", u.getEmail());
        } else if (principal instanceof User u) {
            model.addAttribute("name",  u.getFullName() != null ? u.getFullName() : "Admin");
            model.addAttribute("email", u.getEmail() != null ? u.getEmail() : u.getPhone());
        }
    }
}
