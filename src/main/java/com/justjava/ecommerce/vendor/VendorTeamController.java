package com.justjava.ecommerce.vendor;

import com.justjava.ecommerce.auth.KeycloakAdminService;
import com.justjava.ecommerce.order.OrderRepository;
import com.justjava.ecommerce.order.OrderStatus;
import com.justjava.ecommerce.product.ProductRepository;
import com.justjava.ecommerce.product.ProductStatus;
import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/vendor/team")
@PreAuthorize("hasRole('VENDOR')")
@RequiredArgsConstructor
public class VendorTeamController {

    private static final Set<OrderStatus> REVENUE_STATUSES =
            EnumSet.of(OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.SHIPPED, OrderStatus.DELIVERED, OrderStatus.CONFIRMED);
    private static final Set<OrderStatus> SETTLED_STATUSES = EnumSet.of(OrderStatus.CONFIRMED);
    private static final Set<OrderStatus> PENDING_STATUSES =
            EnumSet.of(OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.SHIPPED, OrderStatus.DELIVERED);

    private final UserRepository         userRepository;
    private final VendorRepository       vendorRepository;
    private final VendorMemberRepository vendorMemberRepository;
    private final OrderRepository        orderRepository;
    private final ProductRepository      productRepository;
    private final KeycloakAdminService   keycloakAdminService;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String keycloakClientId;

    @Value("${app.base-url}")
    private String appBaseUrl;

    @Value("${app.invite-link-lifespan-seconds:86400}")
    private int inviteLifespanSeconds;

    @GetMapping
    public String team(@AuthenticationPrincipal OidcUser principal, Model model) {
        User owner = resolveOwner(principal);
        Vendor vendor = resolveVendor(owner);

        List<VendorMember> members = vendorMemberRepository.findByVendorIdWithUser(vendor.getId());

        model.addAttribute("vendor", vendor);
        model.addAttribute("owner", owner);
        model.addAttribute("members", members);
        model.addAttribute("name", principal.getFullName() != null ? principal.getFullName() : "Vendor");
        return "vendor/team";
    }

    @PostMapping("/invite")
    @Transactional
    public String invite(
            @AuthenticationPrincipal OidcUser principal,
            @RequestParam String email,
            @RequestParam String firstName,
            @RequestParam String lastName,
            RedirectAttributes flash
    ) {
        String cleanEmail = email == null ? "" : email.trim().toLowerCase();
        String cleanFirst = firstName == null ? "" : firstName.trim();
        String cleanLast  = lastName == null  ? "" : lastName.trim();

        if (cleanEmail.isBlank() || cleanFirst.isBlank() || cleanLast.isBlank()) {
            flash.addFlashAttribute("inviteError", "Email, first name, and last name are all required.");
            return "redirect:/vendor/team";
        }

        User inviter = resolveOwner(principal);
        Vendor vendor = resolveVendor(inviter);

        if (userRepository.findByEmail(cleanEmail).isPresent()) {
            flash.addFlashAttribute("inviteError", "A user with that email already exists on this platform.");
            return "redirect:/vendor/team";
        }
        if (keycloakAdminService.emailExists(cleanEmail)) {
            flash.addFlashAttribute("inviteError", "That email is already registered in the authentication system.");
            return "redirect:/vendor/team";
        }

        String keycloakId;
        try {
            keycloakId = keycloakAdminService.createInvitedUser(cleanEmail, cleanFirst, cleanLast);
        } catch (Exception e) {
            log.error("Failed to create Keycloak user for invite {}", cleanEmail, e);
            flash.addFlashAttribute("inviteError", "Could not create the account in the authentication system.");
            return "redirect:/vendor/team";
        }

        try {
            keycloakAdminService.assignSubVendorRole(keycloakId);

            keycloakAdminService.updateUserAttributes(keycloakId, Map.of(
                    "invitedBy", inviter.getFullName() != null ? inviter.getFullName() : "",
                    "invitedByCompany", vendor.getName() != null ? vendor.getName() : ""
            ));

            User invited = userRepository.save(User.builder()
                    .keycloakId(keycloakId)
                    .email(cleanEmail)
                    .fullName(cleanFirst + " " + cleanLast)
                    .role("SUB_VENDOR")
                    .status("PENDING_ACTIVATION")
                    .build());

            vendorMemberRepository.save(VendorMember.builder()
                    .vendor(vendor)
                    .user(invited)
                    .memberRole(VendorMemberRole.SUB_VENDOR)
                    .invitedBy(inviter)
                    .build());

            keycloakAdminService.sendInvitationEmail(
                    keycloakId,
                    keycloakClientId,
                    appBaseUrl + "/oauth2/authorization/keycloak",
                    inviteLifespanSeconds
            );

            flash.addFlashAttribute("inviteSuccess",
                    "Invitation sent to " + cleanEmail + ". They will receive an email to set their password.");
        } catch (Exception e) {
            log.error("Invite flow failed after Keycloak user creation for {}, rolling back Keycloak user", cleanEmail, e);
            keycloakAdminService.deleteUser(keycloakId);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            flash.addFlashAttribute("inviteError",
                    "Could not complete the invitation. Please try again or contact support.");
            return "redirect:/vendor/team";
        }

        return "redirect:/vendor/team";
    }

    @GetMapping("/{memberId}")
    public String memberDetails(
            @AuthenticationPrincipal OidcUser principal,
            @PathVariable UUID memberId,
            Model model
    ) {
        User owner = resolveOwner(principal);
        Vendor vendor = resolveVendor(owner);

        VendorMember member = vendorMemberRepository.findByIdWithUser(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team member not found"));

        if (!member.getVendor().getId().equals(vendor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member of your team");
        }

        UUID vendorId    = vendor.getId();
        UUID creatorId   = member.getUser().getId();

        // Orders
        model.addAttribute("totalOrders",      orderRepository.countByVendorIdAndCreator(vendorId, creatorId));
        model.addAttribute("paidOrders",       orderRepository.countByVendorIdAndCreatorAndStatus(vendorId, creatorId, OrderStatus.PAID));
        model.addAttribute("processingOrders", orderRepository.countByVendorIdAndCreatorAndStatus(vendorId, creatorId, OrderStatus.PROCESSING));
        model.addAttribute("shippedOrders",    orderRepository.countByVendorIdAndCreatorAndStatus(vendorId, creatorId, OrderStatus.SHIPPED));
        model.addAttribute("deliveredOrders",  orderRepository.countByVendorIdAndCreatorAndStatus(vendorId, creatorId, OrderStatus.DELIVERED));
        model.addAttribute("cancelledOrders",  orderRepository.countByVendorIdAndCreatorAndStatus(vendorId, creatorId, OrderStatus.CANCELLED));

        // Earnings
        model.addAttribute("totalEarned",     orderRepository.sumRevenueByVendorIdAndCreatorAndStatusIn(vendorId, creatorId, REVENUE_STATUSES));
        model.addAttribute("settledEarnings", orderRepository.sumRevenueByVendorIdAndCreatorAndStatusIn(vendorId, creatorId, SETTLED_STATUSES));
        model.addAttribute("pendingEarnings", orderRepository.sumRevenueByVendorIdAndCreatorAndStatusIn(vendorId, creatorId, PENDING_STATUSES));

        // Products
        model.addAttribute("publishedProducts", productRepository.countByVendorIdAndCreatedByIdAndStatus(vendorId, creatorId, ProductStatus.PUBLISHED));
        model.addAttribute("draftProducts",     productRepository.countByVendorIdAndCreatedByIdAndStatus(vendorId, creatorId, ProductStatus.DRAFT));
        model.addAttribute("pendingProducts",
                productRepository.countByVendorIdAndCreatedByIdAndStatus(vendorId, creatorId, ProductStatus.PENDING_REVIEW)
              + productRepository.countByVendorIdAndCreatedByIdAndStatus(vendorId, creatorId, ProductStatus.PENDING_EDIT));
        model.addAttribute("rejectedProducts",  productRepository.countByVendorIdAndCreatedByIdAndStatus(vendorId, creatorId, ProductStatus.REJECTED));
        model.addAttribute("archivedProducts",  productRepository.countByVendorIdAndCreatedByIdAndStatus(vendorId, creatorId, ProductStatus.ARCHIVED));
        model.addAttribute("lowStockProducts",  productRepository.countByVendorIdAndCreatedByIdAndStockQuantityLessThanEqual(vendorId, creatorId, 5));

        // Top products & recent orders
        model.addAttribute("topProducts",
                orderRepository.findTopProductsByVendorAndCreator(vendorId, creatorId, REVENUE_STATUSES, PageRequest.of(0, 5)));
        model.addAttribute("recentOrders",
                orderRepository.findRecentByVendorIdAndCreator(vendorId, creatorId, PageRequest.of(0, 10)));

        model.addAttribute("vendor", vendor);
        model.addAttribute("member", member);
        model.addAttribute("teamUser", member.getUser());
        model.addAttribute("name", principal.getFullName() != null ? principal.getFullName() : "Vendor");
        return "vendor/team-member";
    }

    @PostMapping("/{memberId}/revoke")
    @Transactional
    public String revoke(
            @AuthenticationPrincipal OidcUser principal,
            @PathVariable UUID memberId,
            RedirectAttributes flash
    ) {
        User owner = resolveOwner(principal);
        Vendor vendor = resolveVendor(owner);

        VendorMember member = vendorMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team member not found"));

        if (!member.getVendor().getId().equals(vendor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member of your team");
        }
        if (member.getMemberRole() == VendorMemberRole.VENDOR_ADMIN) {
            flash.addFlashAttribute("inviteError", "You cannot revoke the vendor admin.");
            return "redirect:/vendor/team";
        }

        User target = member.getUser();
        String targetKeycloakId = target.getKeycloakId();

        vendorMemberRepository.delete(member);
        userRepository.delete(target);
        keycloakAdminService.deleteUser(targetKeycloakId);

        flash.addFlashAttribute("inviteSuccess", "Team member removed.");
        return "redirect:/vendor/team";
    }

    private User resolveOwner(OidcUser principal) {
        return userRepository.findByKeycloakId(principal.getSubject())
                .orElseThrow(() -> new IllegalStateException("Vendor user not found in local DB"));
    }

    private Vendor resolveVendor(User owner) {
        return vendorRepository.findByOwnerId(owner.getId())
                .orElseThrow(() -> new IllegalStateException("Vendor company not found for user " + owner.getId()));
    }
}
