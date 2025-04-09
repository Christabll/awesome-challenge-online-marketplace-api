package com.awesomity.marketplace.marketplace_api.controller;


import com.awesomity.marketplace.marketplace_api.entity.Category;
import com.awesomity.marketplace.marketplace_api.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllCategories() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryService.findAll()).thenReturn(List.of(category));

        ResponseEntity<?> response = categoryController.getAllCategories();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).toString().contains("Electronics");
        verify(categoryService).findAll();
    }

    @Test
    void shouldReturnCategoryById() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryService.findById(1L)).thenReturn(category);

        ResponseEntity<?> response = categoryController.getCategoryById(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).toString().contains("Electronics");
        verify(categoryService).findById(1L);
    }
}
