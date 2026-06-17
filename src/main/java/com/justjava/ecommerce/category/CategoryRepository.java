package com.justjava.ecommerce.category;

import com.justjava.ecommerce.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findBySlug(String slug);

    List<Category> findByParentIsNullAndActiveTrueOrderBySortOrderAscNameAsc();

    List<Category> findByParentIdAndActiveTrueOrderBySortOrderAscNameAsc(UUID parentId);

    boolean existsBySlug(String slug);

    @Query("SELECT c FROM Category c WHERE c.active = true ORDER BY c.sortOrder ASC, c.name ASC")
    List<Category> findAllActive();
}
