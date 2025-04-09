package com.awesomity.marketplace.marketplace_api.service.impl;

import com.awesomity.marketplace.marketplace_api.dto.CategoryDto;
import com.awesomity.marketplace.marketplace_api.entity.Category;
import com.awesomity.marketplace.marketplace_api.exception.BadRequestException;
import com.awesomity.marketplace.marketplace_api.exception.ResourceNotFoundException;
import com.awesomity.marketplace.marketplace_api.repository.CategoryRepository;
import com.awesomity.marketplace.marketplace_api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryDto dto) {
        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new BadRequestException("Category with name '" + dto.getName() + "' already exists");
        }

        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());

        Category saved = categoryRepository.save(category);
        log.info("Created new category with ID: {}", saved.getId());

        return saved;
    }

    @Override
    public Category updateCategory(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());

        Category updated = categoryRepository.save(category);
        log.info("Updated category with ID: {}", updated.getId());

        return updated;
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        categoryRepository.delete(category);
        log.info("Deleted category with ID: {}", categoryId);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }
}

