package com.justjava.ecommerce.home;

import com.justjava.ecommerce.product.dto.ProductFilter;
import com.justjava.ecommerce.category.CategoryService;
import com.justjava.ecommerce.product.ProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductQueryService productQueryService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String landing(Model model) {
        var featuredProducts = productQueryService.getPublishedProducts(
                ProductFilter.empty(),
                PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "publishedAt"))
        );
        model.addAttribute("featuredProducts", featuredProducts.getContent());
        model.addAttribute("categories", categoryService.getRootCategories());
        return "landing";
    }

    @GetMapping("/customer/dashboard")
    public String customerDashboard(@AuthenticationPrincipal OidcUser user, Model model) {
        model.addAttribute("name", user.getFullName());
        model.addAttribute("email", user.getEmail());
        return "customer/dashboard";
    }

    @GetMapping("/vendor/dashboard")
    public String vendorDashboard(@AuthenticationPrincipal OidcUser user, Model model) {
        model.addAttribute("name", user.getFullName());
        model.addAttribute("email", user.getEmail());
        return "vendor/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(@AuthenticationPrincipal OidcUser user, Model model) {
        model.addAttribute("name", user.getFullName());
        model.addAttribute("email", user.getEmail());
        return "admin/dashboard";
    }
}
