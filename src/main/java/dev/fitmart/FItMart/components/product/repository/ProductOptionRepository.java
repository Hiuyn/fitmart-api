package dev.fitmart.FItMart.components.product.repository;

import dev.fitmart.FItMart.components.product.model.Product;
import dev.fitmart.FItMart.components.product.model.ProductOption;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductOptionRepository extends MongoRepository<ProductOption, String> {
    List<ProductOption> findByProductId(String productId);
    Optional<ProductOption> findByUuid(String uuid);
}
