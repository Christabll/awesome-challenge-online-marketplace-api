package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.dto.ProductDto;
import com.awesomity.marketplace.marketplace_api.entity.Product;
import com.awesomity.marketplace.marketplace_api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;


@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {


    private final ProductService productService;


    @PostMapping("/products")
    public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody ProductDto dto) {
        Product product = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created", product));
    }


    @PatchMapping("/products/{productId}/feature")
    public ResponseEntity<ApiResponse> markProductAsFeatured(@PathVariable Long productId) {
        Product product = productService.markAsFeatured(productId);
        return ResponseEntity.ok(ApiResponse.success("Product marked as featured", product));
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long productId,
                                                     @Valid @RequestBody ProductDto dto) {
        Product updated = productService.updateProduct(productId, dto);
        return ResponseEntity.ok(ApiResponse.success("Product updated", updated));
    }



    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }



}
