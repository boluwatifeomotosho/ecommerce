package com.justjava.ecommerce.product;

import com.justjava.ecommerce.category.CategoryDto;
import com.justjava.ecommerce.category.CategoryService;
import com.justjava.ecommerce.product.ProductCommandService;
import com.justjava.ecommerce.product.ProductQueryService;
import com.justjava.ecommerce.product.dto.SaveProductRequest;
import com.justjava.ecommerce.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/vendor/products")
@PreAuthorize("hasRole('VENDOR')")
@RequiredArgsConstructor
public class VendorProductController {

    private final ProductQueryService   productQueryService;
    private final ProductCommandService productCommandService;
    private final CategoryService       categoryService;
    private final UserRepository        userRepository;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal OidcUser principal,
            Model model
    ) {
        UUID vendorId = resolveVendorId(principal);
        var products  = productQueryService.getVendorProducts(
                vendorId, PageRequest.of(page, 20, Sort.by("createdAt").descending()));

        model.addAttribute("products", products);
        return "vendor/products/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("productForm", new SaveProductRequest(null, null, null, null, null, 0, null, null, null));
        addCategoryModel(model);
        model.addAttribute("formAction",  "/vendor/products");
        model.addAttribute("editMode",    false);
        return "vendor/products/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("productForm") SaveProductRequest request,
            BindingResult                     bindingResult,
            @AuthenticationPrincipal OidcUser principal,
            Model                             model,
            RedirectAttributes                flash
    ) {
        if (bindingResult.hasErrors()) {
            addCategoryModel(model);
            model.addAttribute("formAction",  "/vendor/products");
            model.addAttribute("editMode",    false);
            return "vendor/products/form";
        }

        try {
            UUID vendorId = resolveVendorId(principal);
            var  product  = productCommandService.create(vendorId, request);
            flash.addFlashAttribute("success", "Product '" + product.name() + "' saved as draft.");
            return "redirect:/vendor/products";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error",       e.getMessage());
            addCategoryModel(model);
            model.addAttribute("formAction",  "/vendor/products");
            model.addAttribute("editMode",    false);
            return "vendor/products/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable UUID               id,
            @AuthenticationPrincipal OidcUser principal,
            Model                            model
    ) {
        UUID vendorId = resolveVendorId(principal);
        var  product  = productQueryService.getVendorProductById(id, vendorId);

        var request = new SaveProductRequest(
                product.name(),
                product.category().id(),
                product.description(),
                product.price(),
                product.compareAtPrice(),
                product.stockQuantity(),
                product.sku(),
                product.weightGrams(),
                product.images().stream().map(img -> img.url()).toList()
        );

        model.addAttribute("productForm", request);
        model.addAttribute("product",     product);
        addCategoryModel(model);
        model.addAttribute("formAction",  "/vendor/products/" + product.id());
        model.addAttribute("editMode",    true);
        return "vendor/products/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable UUID               id,
            @Valid @ModelAttribute("productForm") SaveProductRequest request,
            BindingResult                     bindingResult,
            @AuthenticationPrincipal OidcUser principal,
            Model                             model,
            RedirectAttributes                flash
    ) {
        if (bindingResult.hasErrors()) {
            addCategoryModel(model);
            model.addAttribute("formAction",  "/vendor/products/" + id);
            model.addAttribute("editMode",    true);
            return "vendor/products/form";
        }

        try {
            UUID vendorId = resolveVendorId(principal);
            productCommandService.update(id, vendorId, request);
            flash.addFlashAttribute("success", "Product updated.");
            return "redirect:/vendor/products";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error",       e.getMessage());
            addCategoryModel(model);
            model.addAttribute("formAction",  "/vendor/products/" + id);
            model.addAttribute("editMode",    true);
            return "vendor/products/form";
        }
    }

    @PostMapping("/{id}/submit")
    public String submitForReview(
            @PathVariable UUID               id,
            @AuthenticationPrincipal OidcUser principal,
            RedirectAttributes               flash
    ) {
        UUID vendorId = resolveVendorId(principal);
        try {
            productCommandService.submitForReview(id, vendorId);
            flash.addFlashAttribute("success", "Product submitted for admin review.");
        } catch (IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vendor/products";
    }

    @PostMapping("/{id}/archive")
    public String archive(
            @PathVariable UUID               id,
            @AuthenticationPrincipal OidcUser principal,
            RedirectAttributes               flash
    ) {
        UUID vendorId = resolveVendorId(principal);
        productCommandService.archive(id, vendorId);
        flash.addFlashAttribute("success", "Product archived.");
        return "redirect:/vendor/products";
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void addCategoryModel(Model model) {
        List<CategoryDto> all   = categoryService.getAllActive();
        List<CategoryDto> roots = all.stream().filter(c -> c.parentId() == null).toList();
        Map<UUID, List<CategoryDto>> subMap = all.stream()
                .filter(c -> c.parentId() != null)
                .collect(Collectors.groupingBy(CategoryDto::parentId));
        model.addAttribute("rootCategories", roots);
        model.addAttribute("subCategoryMap", subMap);
    }

    private UUID resolveVendorId(OidcUser principal) {
        return userRepository.findByKeycloakId(principal.getSubject())
                .orElseThrow(() -> new IllegalStateException("Vendor user not found in local DB"))
                .getId();
    }
}
