package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.dto.ProductDto;
import com.awesomity.marketplace.marketplace_api.entity.Product;
import com.awesomity.marketplace.marketplace_api.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminProductController {

    private final ProductService productService;

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<Product>> createProduct(@Valid @RequestBody ProductDto dto) {
        Product product = productService.createProduct(dto);
        log.info("Created product with ID: {}", product.getId());
        return ApiResponse.created("Product created", product);
    }

    @PatchMapping("/products/{productId}/feature")
    public ResponseEntity<ApiResponse<Product>> markProductAsFeatured(@PathVariable Long productId) {
        Product product = productService.markAsFeatured(productId);
        log.info("Marked product with ID {} as featured", productId);
        return ApiResponse.ok("Product marked as featured", product);
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long productId,
                                                              @Valid @RequestBody ProductDto dto) {
        Product updated = productService.updateProduct(productId, dto);
        log.info("Updated product with ID: {}", productId);
        return ApiResponse.ok("Product updated", updated);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        log.info("Deleted product with ID: {}", productId);
        return ApiResponse.ok("Product deleted", null);
    }
}
