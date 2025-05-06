package dev.fitmart.FItMart.components.product.repository;

import dev.fitmart.FItMart.components.product.model.ProductOptionValue;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductOptionValueRepository extends MongoRepository<ProductOptionValue, String> {
    List<ProductOptionValue> findByVariantId(String variantId);
}
