package com.justjava.ecommerce.category;

import com.justjava.ecommerce.category.CategoryDto;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    List<CategoryDto> getRootCategories();

    List<CategoryDto> getSubcategories(UUID parentId);

    List<CategoryDto> getAllActive();

    CategoryDto getById(UUID id);
}
