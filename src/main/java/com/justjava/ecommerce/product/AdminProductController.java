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
            case "vendor"     -> Sort.by("vendor.name").ascending().and(Sort.by("name").ascending());
            default           -> Sort.by("createdAt").descending();
        };
        var products = productQueryService.getPublishedProducts(filter, PageRequest.of(page, 20, sortObj));
        model.addAttribute("products", products);
        model.addAttribute("filter",   filter);
        model.addAttribute("sort",     sort);
        enrichModel(auth, model);
        return "admin/products/catalog";
    }

    @PostMapping("/{id}/takedown")
    public String takedown(
            @PathVariable UUID   id,
            @RequestParam String reason,
            RedirectAttributes   flash
    ) {
        try {
            productApprovalService.takedown(id, reason);
            flash.addFlashAttribute("success", "Product taken down from the catalog.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/products/catalog";
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
