package com.awesomity.marketplace.marketplace_api.serviceImpl;

import com.awesomity.marketplace.marketplace_api.dto.ProductDto;
import com.awesomity.marketplace.marketplace_api.entity.Category;
import com.awesomity.marketplace.marketplace_api.entity.Product;
import com.awesomity.marketplace.marketplace_api.exception.ResourceNotFoundException;
import com.awesomity.marketplace.marketplace_api.repository.CategoryRepository;
import com.awesomity.marketplace.marketplace_api.repository.ProductRepository;
import com.awesomity.marketplace.marketplace_api.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ProductDto sampleDto() {
        ProductDto dto = new ProductDto();
        dto.setName("Test Product");
        dto.setDescription("Test Description");
        dto.setPrice(99.99);
        dto.setQuantity(10);
        dto.setCurrency("USD");
        dto.setCategoryId(1L);
        dto.setTags(Set.of("tech", "gadget"));
        return dto;
    }

    @Test
    void shouldCreateProduct() {
        ProductDto dto = sampleDto();
        Category category = new Category();
        category.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product saved = inv.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        Product result = productService.createProduct(dto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(productRepository).save(any());
    }

    @Test
    void shouldUpdateProduct() {
        ProductDto dto = sampleDto();
        Product existing = new Product();
        existing.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any())).thenReturn(existing);

        Product result = productService.updateProduct(1L, dto);

        assertThat(result.getName()).isEqualTo(dto.getName());
        verify(productRepository).save(existing);
    }

    @Test
    void shouldDeleteProduct() {
        Product product = new Product();
        product.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        productService.deleteProduct(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void shouldMarkProductAsFeatured() {
        Product product = new Product();
        product.setId(1L);
        product.setFeatured(false);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);

        Product result = productService.markAsFeatured(1L);

        assertThat(result.isFeatured()).isTrue();
        verify(productRepository).save(product);
    }

    @Test
    void shouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(new Product(), new Product()));
        assertThat(productService.findAll()).hasSize(2);
    }

    @Test
    void shouldFindProductById() {
        Product product = new Product();
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.findById(1L);
        assertThat(result).isEqualTo(product);
    }

    @Test
    void shouldGetProductsByCategory() {
        when(productRepository.findByCategoryId(1L)).thenReturn(List.of(new Product(), new Product()));
        assertThat(productService.getProductsByCategory(1L)).hasSize(2);
    }

    @Test
    void shouldGetProductsByTag() {
        when(productRepository.findByTagsContaining("tech")).thenReturn(List.of(new Product()));
        assertThat(productService.getProductsByTag("tech")).hasSize(1);
    }

    @Test
    void shouldSearchProductsByNameOrDescription() {
        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("search", "search"))
                .thenReturn(List.of(new Product()));

        assertThat(productService.searchProducts("search")).hasSize(1);
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        when(productRepository.findById(100L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.findById(100L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}