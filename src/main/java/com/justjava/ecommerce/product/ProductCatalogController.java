package com.justjava.ecommerce.product;

import com.justjava.ecommerce.category.CategoryDto;
import com.justjava.ecommerce.product.dto.ProductFilter;
import com.justjava.ecommerce.category.CategoryService;
import com.justjava.ecommerce.product.ProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductCatalogController {

    private static final int PAGE_SIZE = 20;

    private final ProductQueryService productQueryService;
    private final CategoryService     categoryService;

    @GetMapping
    public String listProducts(
            @ModelAttribute ProductFilter filter,
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "newest") String sort,
            Model model
    ) {
        var pageable = PageRequest.of(page, PAGE_SIZE, resolveSort(sort));
        var products = productQueryService.getPublishedProducts(filter, pageable);

        List<CategoryDto> allCategories = categoryService.getAllActive();
        List<CategoryDto> rootCategories = allCategories.stream()
                .filter(c -> c.parentId() == null)
                .toList();
        Map<UUID, List<CategoryDto>> subCategoryMap = allCategories.stream()
                .filter(c -> c.parentId() != null)
                .collect(Collectors.groupingBy(CategoryDto::parentId));

        UUID selectedRootId = null;
        if (filter.categoryId() != null) {
            if (rootCategories.stream().anyMatch(c -> c.id().equals(filter.categoryId()))) {
                selectedRootId = filter.categoryId();
            } else {
                CategoryDto selected = allCategories.stream()
                        .filter(c -> c.id().equals(filter.categoryId()))
                        .findFirst().orElse(null);
                if (selected != null && selected.parentId() != null) {
                    selectedRootId = selected.parentId();
                }
            }
        }

        model.addAttribute("products",         products);
        model.addAttribute("filter",           filter);
        model.addAttribute("sort",             sort);
        model.addAttribute("rootCategories",   rootCategories);
        model.addAttribute("subCategoryMap",   subCategoryMap);
        model.addAttribute("selectedRootId",   selectedRootId);
        return "products/list";
    }

    @GetMapping("/{slug}")
    public String productDetail(@PathVariable String slug, Model model) {
        var product = productQueryService.getPublishedProductBySlug(slug);
        model.addAttribute("product", product);
        return "products/detail";
    }

    private Sort resolveSort(String sort) {
        return switch (sort) {
            case "price-asc"  -> Sort.by("price").ascending();
            case "price-desc" -> Sort.by("price").descending();
            case "name"       -> Sort.by("name").ascending();
            default           -> Sort.by("createdAt").descending();
        };
    }
}
