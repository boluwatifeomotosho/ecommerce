package com.justjava.ecommerce.product;

import com.justjava.ecommerce.product.dto.ProductDetailDto;
import com.justjava.ecommerce.product.dto.SaveProductRequest;
import com.justjava.ecommerce.product.ProductMapper;
import com.justjava.ecommerce.product.Product;
import com.justjava.ecommerce.product.ProductImage;
import com.justjava.ecommerce.product.ProductStatus;
import com.justjava.ecommerce.category.CategoryRepository;
import com.justjava.ecommerce.product.ProductRepository;
import com.justjava.ecommerce.user.UserRepository;
import com.justjava.ecommerce.product.ProductCommandService;
import com.justjava.ecommerce.util.SlugUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandServiceImpl implements ProductCommandService {

    private final ProductRepository  productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository     userRepository;
    private final ProductMapper      mapper;
    private final SlugUtils          slugUtils;

    @Override
    public ProductDetailDto create(UUID vendorId, SaveProductRequest request) {
        var vendor   = userRepository.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found: " + vendorId));
        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + request.getCategoryId()));

        String sku = blankToNull(request.getSku());
        validateSkuUniqueness(sku, null);

        String slug = slugUtils.generateUnique(request.getName(), productRepository::existsBySlug);

        Product product = Product.builder()
                .vendor(vendor)
                .category(category)
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .price(request.getPrice())
                .compareAtPrice(request.getCompareAtPrice())
                .stockQuantity(request.getStockQuantity())
                .sku(sku)
                .weightGrams(request.getWeightGrams())
                .status(ProductStatus.DRAFT)
                .images(buildImages(request.getImageUrls()))
                .build();

        linkImages(product);
        return mapper.toDetail(productRepository.save(product));
    }

    @Override
    public ProductDetailDto update(UUID productId, UUID vendorId, SaveProductRequest request) {
        Product product = productRepository.findByIdAndVendorId(productId, vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found or not owned by vendor"));

        ProductStatus originalStatus = product.getStatus();
        if (!originalStatus.isEditable()) {
            throw new IllegalStateException("Product in status " + originalStatus + " cannot be edited");
        }

        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + request.getCategoryId()));

        String sku = blankToNull(request.getSku());
        validateSkuUniqueness(sku, productId);

        if (!product.getName().equalsIgnoreCase(request.getName())) {
            product.setSlug(slugUtils.generateUnique(request.getName(), productRepository::existsBySlug));
        }

        product.setCategory(category);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCompareAtPrice(request.getCompareAtPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setSku(sku);
        product.setWeightGrams(request.getWeightGrams());

        product.getImages().clear();
        product.getImages().addAll(buildImages(request.getImageUrls()));
        linkImages(product);

        // Editing a live product re-opens admin review and hides it from customers
        // until the edit is approved or rejected.
        if (originalStatus == ProductStatus.PUBLISHED || originalStatus == ProductStatus.PENDING_EDIT) {
            product.setStatus(ProductStatus.PENDING_EDIT);
            product.setRejectionReason(null);
        }

        return mapper.toDetail(productRepository.save(product));
    }

    @Override
    public void submitForReview(UUID productId, UUID vendorId) {
        Product product = productRepository.findByIdAndVendorId(productId, vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found or not owned by vendor"));

        if (!product.getStatus().canSubmitForReview()) {
            throw new IllegalStateException("Cannot submit product with status " + product.getStatus());
        }

        product.setStatus(ProductStatus.PENDING_REVIEW);
        product.setRejectionReason(null);
        productRepository.save(product);
    }

    @Override
    public void archive(UUID productId, UUID vendorId) {
        Product product = productRepository.findByIdAndVendorId(productId, vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found or not owned by vendor"));

        product.setStatus(ProductStatus.ARCHIVED);
        productRepository.save(product);
    }

    @Override
    public void unarchive(UUID productId, UUID vendorId) {
        Product product = productRepository.findByIdAndVendorId(productId, vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found or not owned by vendor"));

        if (product.getStatus() != ProductStatus.ARCHIVED) {
            throw new IllegalStateException("Only archived products can be unarchived");
        }

        product.setStatus(ProductStatus.DRAFT);
        productRepository.save(product);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private List<ProductImage> buildImages(List<String> urls) {
        if (urls == null || urls.isEmpty()) return new ArrayList<>();
        AtomicInteger order = new AtomicInteger(0);
        return urls.stream()
                .filter(url -> url != null && !url.isBlank())
                .map(url -> ProductImage.builder()
                        .url(url)
                        .sortOrder(order.getAndIncrement())
                        .primary(order.get() == 1)
                        .build())
                .toList();
    }

    private void linkImages(Product product) {
        product.getImages().forEach(img -> img.setProduct(product));
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private void validateSkuUniqueness(String sku, UUID excludeId) {
        if (sku == null || sku.isBlank()) return;
        boolean conflict = excludeId == null
                ? productRepository.existsBySku(sku)
                : productRepository.existsBySkuAndIdNot(sku, excludeId);
        if (conflict) {
            throw new IllegalArgumentException("SKU '" + sku + "' is already in use");
        }
    }
}
