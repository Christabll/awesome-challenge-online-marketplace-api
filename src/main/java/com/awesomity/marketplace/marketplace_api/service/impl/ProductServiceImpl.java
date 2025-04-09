package com.awesomity.marketplace.marketplace_api.service.impl;

import com.awesomity.marketplace.marketplace_api.dto.ProductDto;
import com.awesomity.marketplace.marketplace_api.entity.Category;
import com.awesomity.marketplace.marketplace_api.entity.Product;
import com.awesomity.marketplace.marketplace_api.exception.ResourceNotFoundException;
import com.awesomity.marketplace.marketplace_api.repository.CategoryRepository;
import com.awesomity.marketplace.marketplace_api.repository.ProductRepository;
import com.awesomity.marketplace.marketplace_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Product createProduct(ProductDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setCurrency(dto.getCurrency());
        product.setFeatured(false);
        product.setCategory(category);

        if (dto.getTags() != null) {
            product.setTags(dto.getTags());
        }

        Product saved = productRepository.save(product);
        log.info("Created product with ID: {}", saved.getId());
        return saved;
    }

    @Override
    public Product updateProduct(Long productId, ProductDto dto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setCurrency(dto.getCurrency());

        if (dto.getTags() != null) {
            product.setTags(dto.getTags());
        }

        Product updated = productRepository.save(product);
        log.info("Updated product with ID: {}", updated.getId());
        return updated;
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        productRepository.delete(product);
        log.info("Deleted product with ID: {}", productId);
    }

    @Override
    public Product markAsFeatured(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        product.setFeatured(true);
        Product updated = productRepository.save(product);
        log.info("Marked product as featured. ID: {}", updated.getId());
        return updated;
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Product> getProductsByTag(String tag) {
        return productRepository.findByTagsContaining(tag);
    }

    @Override
    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }
}
