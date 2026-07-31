package com.justjava.ecommerce.product;

import com.justjava.ecommerce.vendor.VendorMemberRole;
import com.justjava.ecommerce.vendor.VendorMemberService;
import com.justjava.ecommerce.vendor.VendorScope;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * Product approval queue for the vendor admin (store owner). Approving is
 * scoped to the caller's own vendor company — sub-vendors can submit for
 * review but only the vendor admin decides.
 */
@Controller
@RequestMapping("/vendor/approvals")
@PreAuthorize("hasAnyRole('VENDOR', 'SUB_VENDOR')")
@RequiredArgsConstructor
public class VendorApprovalsController {

    private final ProductQueryService    productQueryService;
    private final ProductApprovalService productApprovalService;
    private final VendorMemberService    vendorMemberService;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal OidcUser principal,
            Model model
    ) {
        VendorScope scope = requireVendorAdmin(principal);
        var products = productQueryService.getPendingProductsByVendor(
                scope.vendorId(),
                PageRequest.of(page, 20, Sort.by("createdAt").ascending()));
        model.addAttribute("products", products);
        return "vendor/approvals/list";
    }

    @GetMapping("/{id}")
    public String reviewDetail(
            @PathVariable UUID id,
            @AuthenticationPrincipal OidcUser principal,
            Model model
    ) {
        VendorScope scope = requireVendorAdmin(principal);
        var product = productQueryService.getVendorProductById(id, scope.vendorId());
        model.addAttribute("product", product);
        return "vendor/approvals/review";
    }

    @PostMapping("/{id}/approve")
    public String approve(
            @PathVariable UUID id,
            @AuthenticationPrincipal OidcUser principal,
            RedirectAttributes flash
    ) {
        try {
            VendorScope scope = requireVendorAdmin(principal);
            productApprovalService.approve(id, scope.vendorId());
            flash.addFlashAttribute("success", "Product approved and published.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vendor/approvals";
    }

    @PostMapping("/{id}/reject")
    public String reject(
            @PathVariable UUID id,
            @RequestParam String reason,
            @AuthenticationPrincipal OidcUser principal,
            RedirectAttributes flash
    ) {
        try {
            VendorScope scope = requireVendorAdmin(principal);
            productApprovalService.reject(id, scope.vendorId(), reason);
            flash.addFlashAttribute("success", "Product rejected.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vendor/approvals";
    }

    private VendorScope requireVendorAdmin(OidcUser principal) {
        VendorScope scope = vendorMemberService.currentScope(principal);
        if (scope.role() != VendorMemberRole.VENDOR_ADMIN) {
            throw new AccessDeniedException("Only vendor admins can approve products");
        }
        return scope;
    }
}
