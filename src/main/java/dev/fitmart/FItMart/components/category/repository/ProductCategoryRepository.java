package dev.fitmart.FItMart.components.category.repository;

import dev.fitmart.FItMart.components.category.model.ProductCategory;
import dev.fitmart.FItMart.components.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductCategoryRepository extends MongoRepository<ProductCategory, String> {
    Optional<ProductCategory> findByUuid(String uuid);
    Optional<ProductCategory> findByHandle(String handle);
}
