package com.justjava.ecommerce.category;

import com.justjava.ecommerce.category.CategoryDto;
import com.justjava.ecommerce.product.ProductMapper;
import com.justjava.ecommerce.category.CategoryRepository;
import com.justjava.ecommerce.category.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductMapper       mapper;

    @Override
    public List<CategoryDto> getRootCategories() {
        return categoryRepository
                .findByParentIsNullAndActiveTrueOrderBySortOrderAscNameAsc()
                .stream()
                .map(mapper::toCategoryDto)
                .toList();
    }

    @Override
    public List<CategoryDto> getSubcategories(UUID parentId) {
        return categoryRepository
                .findByParentIdAndActiveTrueOrderBySortOrderAscNameAsc(parentId)
                .stream()
                .map(mapper::toCategoryDto)
                .toList();
    }

    @Override
    public List<CategoryDto> getAllActive() {
        return categoryRepository.findAllActive()
                .stream()
                .map(mapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getById(UUID id) {
        return categoryRepository.findById(id)
                .map(mapper::toCategoryDto)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
    }
}
