package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.entity.Product;
import com.awesomity.marketplace.marketplace_api.exception.ResourceNotFoundException;
import com.awesomity.marketplace.marketplace_api.repository.ProductRepository;
import com.awesomity.marketplace.marketplace_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Products retrieved", products));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long productId) {
        Product product = productService.findById(productId);
        return ResponseEntity.ok(ApiResponse.success("Product retrieved", product));
    }

}
