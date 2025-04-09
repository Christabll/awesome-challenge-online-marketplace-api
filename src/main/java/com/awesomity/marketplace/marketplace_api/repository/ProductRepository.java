package com.awesomity.marketplace.marketplace_api.repository;

import com.awesomity.marketplace.marketplace_api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByTagsContaining(String tag);
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);


}

