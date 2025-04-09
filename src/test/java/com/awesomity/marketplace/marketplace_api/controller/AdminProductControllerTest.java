package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.dto.ProductDto;
import com.awesomity.marketplace.marketplace_api.entity.Product;
import com.awesomity.marketplace.marketplace_api.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AdminProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private AdminProductController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ProductDto sampleDto() {
        ProductDto dto = new ProductDto();
        dto.setName("MacBook Air");
        dto.setDescription("M2 Chip, 8GB RAM");
        dto.setPrice(1499.99);
        dto.setQuantity(5);
        dto.setCurrency("USD");
        dto.setCategoryId(1L);
        dto.setTags(Set.of("laptop", "apple"));
        return dto;
    }

    @Test
    void shouldCreateProductSuccessfully() {
        ProductDto dto = sampleDto();
        Product product = new Product();
        product.setId(1L);

        when(productService.createProduct(dto)).thenReturn(product);

        ResponseEntity<ApiResponse<Product>> response = controller.createProduct(dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getId()).isEqualTo(1L);
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        ProductDto dto = sampleDto();
        Product product = new Product();
        product.setId(1L);

        when(productService.updateProduct(1L, dto)).thenReturn(product);

        var response = controller.updateProduct(1L, dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getData().getId()).isEqualTo(1L);
    }

    @Test
    void shouldMarkProductAsFeatured() {
        Product product = new Product();
        product.setId(1L);
        product.setFeatured(true);

        when(productService.markAsFeatured(1L)).thenReturn(product);

        var response = controller.markProductAsFeatured(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getData().isFeatured()).isTrue();
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        doNothing().when(productService).deleteProduct(1L);

        var response = controller.deleteProduct(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(productService).deleteProduct(1L);
    }
}