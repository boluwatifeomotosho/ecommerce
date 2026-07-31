package com.justjava.ecommerce.product;

import com.justjava.ecommerce.category.CategoryDto;
import com.justjava.ecommerce.category.CategoryService;
import com.justjava.ecommerce.product.dto.SaveProductRequest;
import com.justjava.ecommerce.vendor.VendorMemberService;
import com.justjava.ecommerce.vendor.VendorScope;
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
@PreAuthorize("hasAnyRole('VENDOR', 'SUB_VENDOR')")
@RequiredArgsConstructor
public class VendorProductController {

    private final ProductQueryService   productQueryService;
    private final ProductCommandService productCommandService;
    private final CategoryService       categoryService;
    private final VendorMemberService   vendorMemberService;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal OidcUser principal,
            Model model
    ) {
        VendorScope scope = vendorMemberService.currentScope(principal);
        var pageable = PageRequest.of(page, 20, Sort.by("createdAt").descending());
        var products = scope.isCreatorScoped()
                ? productQueryService.getVendorProductsByCreator(scope.vendorId(), scope.creatorUserId(), pageable)
                : productQueryService.getVendorProducts(scope.vendorId(), pageable);

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
            VendorScope scope = vendorMemberService.currentScope(principal);
            UUID userId = vendorMemberService.currentUserId(principal);
            var product = productCommandService.create(scope.vendorId(), userId, request);
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
        VendorScope scope = vendorMemberService.currentScope(principal);
        var product = scope.isCreatorScoped()
                ? productQueryService.getVendorProductByIdForCreator(id, scope.vendorId(), scope.creatorUserId())
                : productQueryService.getVendorProductById(id, scope.vendorId());

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
            VendorScope scope = vendorMemberService.currentScope(principal);
            productCommandService.update(id, scope.vendorId(), scope.creatorUserId(), request);
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
        VendorScope scope = vendorMemberService.currentScope(principal);
        try {
            productCommandService.submitForReview(id, scope.vendorId(), scope.creatorUserId());
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
        VendorScope scope = vendorMemberService.currentScope(principal);
        productCommandService.archive(id, scope.vendorId(), scope.creatorUserId());
        flash.addFlashAttribute("success", "Product archived.");
        return "redirect:/vendor/products";
    }

    @PostMapping("/{id}/unarchive")
    public String unarchive(
            @PathVariable UUID               id,
            @AuthenticationPrincipal OidcUser principal,
            RedirectAttributes               flash
    ) {
        VendorScope scope = vendorMemberService.currentScope(principal);
        try {
            productCommandService.unarchive(id, scope.vendorId(), scope.creatorUserId());
            flash.addFlashAttribute("success", "Product restored to draft. You can now edit and resubmit it.");
        } catch (IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
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
}
