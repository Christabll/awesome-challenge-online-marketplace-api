package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.entity.Product;
import com.awesomity.marketplace.marketplace_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String search) {

        List<Product> products;

        if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId);
            log.info("Fetched products by category ID: {}", categoryId);
        } else if (tag != null) {
            products = productService.getProductsByTag(tag);
            log.info("Fetched products by tag: {}", tag);
        } else if (search != null) {
            products = productService.searchProducts(search);
            log.info("Fetched products by search: {}", search);
        } else {
            products = productService.findAll();
            log.info("Fetched all products");
        }

        return ApiResponse.ok("Products retrieved", products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long productId) {
        Product product = productService.findById(productId); // Will throw if not found
        log.info("Fetched product with ID: {}", productId);
        return ApiResponse.ok("Product retrieved", product);
    }
}
