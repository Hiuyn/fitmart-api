package dev.fitmart.FItMart.components.product.repository;

import dev.fitmart.FItMart.components.cart.model.Cart;
import dev.fitmart.FItMart.components.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Page<Product> findAll(Pageable pageable);
    Optional<Product> findByUuid(String uuid);
    Optional<Product> findBySlug(String slug);
}
