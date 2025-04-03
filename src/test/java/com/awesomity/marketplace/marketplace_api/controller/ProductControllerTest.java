package com.awesomity.marketplace.marketplace_api.controller;


import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.entity.Product;
import com.awesomity.marketplace.marketplace_api.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldFetchAllProducts() {
        Product p1 = new Product(); p1.setName("Phone");
        Product p2 = new Product(); p2.setName("Laptop");
        when(productService.findAll()).thenReturn(List.of(p1, p2));

        ResponseEntity<ApiResponse<List<Product>>> response = productController.getProducts(null, null, null);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getData()).hasSize(2);
        verify(productService).findAll();
    }

    @Test
    void shouldFetchProductsByCategory() {
        Long categoryId = 10L;
        when(productService.getProductsByCategory(categoryId)).thenReturn(List.of(new Product()));

        ResponseEntity<ApiResponse<List<Product>>> response = productController.getProducts(categoryId, null, null);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(productService).getProductsByCategory(categoryId);
    }

    @Test
    void shouldFetchProductsByTag() {
        String tag = "electronics";
        when(productService.getProductsByTag(tag)).thenReturn(List.of(new Product()));

        ResponseEntity<ApiResponse<List<Product>>> response = productController.getProducts(null, tag, null);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(productService).getProductsByTag(tag);
    }

    @Test
    void shouldFetchProductsBySearch() {
        String search = "phone";
        when(productService.searchProducts(search)).thenReturn(List.of(new Product()));

        ResponseEntity<ApiResponse<List<Product>>> response = productController.getProducts(null, null, search);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(productService).searchProducts(search);
    }

    @Test
    void shouldFetchProductById() {
        Long productId = 99L;
        Product product = new Product(); product.setId(productId);
        when(productService.findById(productId)).thenReturn(product);

        ResponseEntity<ApiResponse<Product>> response = productController.getProductById(productId);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getData().getId()).isEqualTo(productId);
        verify(productService).findById(productId);
    }
}

