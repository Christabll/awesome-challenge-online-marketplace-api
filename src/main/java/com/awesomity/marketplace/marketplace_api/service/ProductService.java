package com.awesomity.marketplace.marketplace_api.service;

import com.awesomity.marketplace.marketplace_api.dto.ProductDto;
import com.awesomity.marketplace.marketplace_api.entity.Product;

import java.util.List;

public interface ProductService {
    Product createProduct(ProductDto dto);
    Product updateProduct(Long productId, ProductDto dto);
    void deleteProduct(Long productId);
    Product markAsFeatured(Long productId);
    List<Product> findAll();
    Product findById(Long productId);
    List<Product> getProductsByCategory(Long categoryId);
    List<Product> getProductsByTag(String tag);
    List<Product> searchProducts(String query);

}
