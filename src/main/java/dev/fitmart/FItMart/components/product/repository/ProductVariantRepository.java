package dev.fitmart.FItMart.components.product.repository;

import dev.fitmart.FItMart.components.product.model.ProductOption;
import dev.fitmart.FItMart.components.product.model.ProductVariant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends MongoRepository<ProductVariant, String> {
    List<ProductVariant> findByProductId(String productId);
    Optional<ProductVariant> findByUuid(String uuid);
    Optional<ProductVariant> findBySku(String sku);
}
