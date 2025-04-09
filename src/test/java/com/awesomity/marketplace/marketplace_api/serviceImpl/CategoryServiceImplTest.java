package com.awesomity.marketplace.marketplace_api.serviceImpl;

import com.awesomity.marketplace.marketplace_api.dto.CategoryDto;
import com.awesomity.marketplace.marketplace_api.entity.Category;
import com.awesomity.marketplace.marketplace_api.exception.BadRequestException;
import com.awesomity.marketplace.marketplace_api.exception.ResourceNotFoundException;
import com.awesomity.marketplace.marketplace_api.repository.CategoryRepository;
import com.awesomity.marketplace.marketplace_api.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateCategorySuccessfully() {
        CategoryDto dto = new CategoryDto("Books", "Books Category");
        when(categoryRepository.existsByNameIgnoreCase("Books")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> {
            Category cat = i.getArgument(0);
            cat.setId(1L);
            return cat;
        });

        Category result = categoryService.createCategory(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void shouldThrowIfCategoryNameExists() {
        CategoryDto dto = new CategoryDto("Books", "Books Category");
        when(categoryRepository.existsByNameIgnoreCase("Books")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory(dto))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldUpdateCategorySuccessfully() {
        CategoryDto dto = new CategoryDto("New Name", "Updated Description");
        Category category = new Category();
        category.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category updated = categoryService.updateCategory(1L, dto);

        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void shouldThrowWhenUpdatingNonExistingCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        CategoryDto dto = new CategoryDto("Name", "Desc");

        assertThatThrownBy(() -> categoryService.updateCategory(1L, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldDeleteCategorySuccessfully() {
        Category category = new Category();
        category.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void shouldThrowWhenDeletingNonExistingCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldReturnAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(new Category(), new Category()));
        assertThat(categoryService.findAll()).hasSize(2);
    }

    @Test
    void shouldReturnCategoryById() {
        Category category = new Category();
        category.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        assertThat(categoryService.findById(1L)).isEqualTo(category);
    }

    @Test
    void shouldThrowWhenCategoryByIdNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}