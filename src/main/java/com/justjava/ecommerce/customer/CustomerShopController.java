package com.justjava.ecommerce.customer;

import com.justjava.ecommerce.category.CategoryDto;
import com.justjava.ecommerce.category.CategoryService;
import com.justjava.ecommerce.product.ProductQueryService;
import com.justjava.ecommerce.product.dto.ProductFilter;
import com.justjava.ecommerce.review.ReviewService;
import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import com.justjava.ecommerce.wishlist.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/customer/shop")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class CustomerShopController {

    private static final int PAGE_SIZE = 20;

    private final ProductQueryService productQueryService;
    private final CategoryService     categoryService;
    private final ReviewService       reviewService;
    private final WishlistService     wishlistService;
    private final UserRepository      userRepository;

    @GetMapping
    public String list(
            @ModelAttribute ProductFilter filter,
            @RequestParam(defaultValue = "0")      int    page,
            @RequestParam(defaultValue = "newest") String sort,
            Authentication auth,
            Model model
    ) {
        var pageable  = PageRequest.of(page, PAGE_SIZE, resolveSort(sort));
        var products  = productQueryService.getPublishedProducts(filter, pageable);

        List<CategoryDto> allCategories  = categoryService.getAllActive();
        List<CategoryDto> rootCategories = allCategories.stream()
                .filter(c -> c.parentId() == null).toList();
        Map<UUID, List<CategoryDto>> subCategoryMap = allCategories.stream()
                .filter(c -> c.parentId() != null)
                .collect(Collectors.groupingBy(CategoryDto::parentId));

        UUID selectedRootId = null;
        if (filter.categoryId() != null) {
            if (rootCategories.stream().anyMatch(c -> c.id().equals(filter.categoryId()))) {
                selectedRootId = filter.categoryId();
            } else {
                CategoryDto selected = allCategories.stream()
                        .filter(c -> c.id().equals(filter.categoryId())).findFirst().orElse(null);
                if (selected != null) selectedRootId = selected.parentId();
            }
        }

        model.addAttribute("products",       products);
        model.addAttribute("filter",         filter);
        model.addAttribute("sort",           sort);
        model.addAttribute("rootCategories", rootCategories);
        model.addAttribute("subCategoryMap", subCategoryMap);
        model.addAttribute("selectedRootId", selectedRootId);
        model.addAttribute("name",           resolveName(auth));
        return "customer/shop/list";
    }

    @GetMapping("/{slug}")
    public String detail(@PathVariable String slug, Authentication auth, Model model) {
        var product = productQueryService.getPublishedProductBySlug(slug);
        UUID customerId = resolveId(auth);
        model.addAttribute("product",      product);
        model.addAttribute("reviews",      reviewService.getReviewsForProduct(product.id()));
        model.addAttribute("isWishlisted", wishlistService.isWishlisted(customerId, product.id()));
        model.addAttribute("name",         resolveName(auth));
        return "customer/shop/detail";
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Sort resolveSort(String sort) {
        return switch (sort) {
            case "price-asc"  -> Sort.by("price").ascending();
            case "price-desc" -> Sort.by("price").descending();
            case "name"       -> Sort.by("name").ascending();
            default           -> Sort.by("createdAt").descending();
        };
    }

    private UUID resolveId(Authentication auth) {
        Object p = auth.getPrincipal();
        if (p instanceof OidcUser u) {
            return userRepository.findByKeycloakId(u.getSubject())
                    .orElseThrow(() -> new IllegalStateException("User not found"))
                    .getId();
        }
        if (p instanceof User u) return u.getId();
        throw new IllegalStateException("Unsupported principal");
    }

    private String resolveName(Authentication auth) {
        Object p = auth.getPrincipal();
        if (p instanceof OidcUser u) return u.getFullName() != null ? u.getFullName() : u.getEmail();
        if (p instanceof User u)     return u.getFullName() != null ? u.getFullName() : "Customer";
        return "Customer";
    }
}
