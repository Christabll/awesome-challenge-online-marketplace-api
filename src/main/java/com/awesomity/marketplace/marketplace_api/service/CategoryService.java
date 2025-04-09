package com.awesomity.marketplace.marketplace_api.service;

import com.awesomity.marketplace.marketplace_api.dto.CategoryDto;
import com.awesomity.marketplace.marketplace_api.entity.Category;
import java.util.List;

public interface CategoryService {
    Category createCategory(CategoryDto dto);
    Category updateCategory(Long categoryId, CategoryDto dto);
    void deleteCategory(Long categoryId);
    List<Category> findAll();
    Category findById(Long id);

}
