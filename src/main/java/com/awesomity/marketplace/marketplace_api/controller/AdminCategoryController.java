package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.dto.CategoryDto;
import com.awesomity.marketplace.marketplace_api.entity.Category;
import com.awesomity.marketplace.marketplace_api.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody CategoryDto dto) {
        Category category = categoryService.createCategory(dto);
        return ApiResponse.created("Category created successfully", category);
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable Long categoryId,
                                                                @Valid @RequestBody CategoryDto dto) {
        Category updated = categoryService.updateCategory(categoryId, dto);
        return ApiResponse.ok("Category updated", updated);
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.ok("Category deleted", null);
    }

}
