package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.dto.CategoryDto;
import com.awesomity.marketplace.marketplace_api.entity.Category;
import com.awesomity.marketplace.marketplace_api.exception.ResourceNotFoundException;
import com.awesomity.marketplace.marketplace_api.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AdminCategoryControllerTest {

    @Mock private CategoryService categoryService;

    @InjectMocks
    private AdminCategoryController adminCategoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateCategorySuccessfully() {
        CategoryDto dto = new CategoryDto("Books", "All kinds of books");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName(dto.getName());
        savedCategory.setDescription(dto.getDescription());

        when(categoryService.createCategory(any(CategoryDto.class))).thenReturn(savedCategory);

        ResponseEntity<ApiResponse<Category>> response = adminCategoryController.createCategory(dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getName()).isEqualTo("Books");
    }

    @Test
    void shouldUpdateCategorySuccessfully() {
        Long id = 1L;
        CategoryDto dto = new CategoryDto("Updated", "Updated desc");

        Category updatedCategory = new Category();
        updatedCategory.setId(id);
        updatedCategory.setName(dto.getName());
        updatedCategory.setDescription(dto.getDescription());

        when(categoryService.updateCategory(eq(id), any(CategoryDto.class))).thenReturn(updatedCategory);

        ResponseEntity<ApiResponse<Category>> response = adminCategoryController.updateCategory(id, dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getName()).isEqualTo("Updated");
    }

    @Test
    void shouldFailUpdateCategoryWhenNotFound() {
        Long id = 100L;
        CategoryDto dto = new CategoryDto("Invalid", "Doesn't exist");

        when(categoryService.updateCategory(eq(id), any(CategoryDto.class)))
                .thenThrow(new ResourceNotFoundException("Category", "id", id));

        assertThrows(ResourceNotFoundException.class, () -> adminCategoryController.updateCategory(id, dto));
    }


    @Test
    void shouldDeleteCategorySuccessfully() {
        Long id = 1L;
        doNothing().when(categoryService).deleteCategory(id);

        ResponseEntity<ApiResponse<Void>> response = adminCategoryController.deleteCategory(id);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("deleted");
    }


    @Test
    void shouldFailDeleteCategoryWhenNotFound() {
        Long id = 999L;
        doThrow(new ResourceNotFoundException("Category", "id", id))
                .when(categoryService).deleteCategory(id);

        assertThrows(ResourceNotFoundException.class, () -> adminCategoryController.deleteCategory(id));
    }
}
