package dev.fitmart.FItMart.components.cart.repository;

import dev.fitmart.FItMart.components.cart.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUserId(String userId);
    List<Cart> findAllByDeletedAtIsNull();
    Optional<Cart> findByUuidAndDeletedAtIsNull(String uuid);
}
