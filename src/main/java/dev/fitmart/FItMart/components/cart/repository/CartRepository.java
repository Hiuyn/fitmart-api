package dev.fitmart.FItMart.components.cart.repository;

import dev.fitmart.FItMart.components.cart.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {
    @Query("{ 'uuid' : ?0, 'deleted_at' : null }")
    Optional<Cart> findByUuid(String uuid);
    @Query("{ 'account_id' : ?0, 'is_draft' : 1, 'completed_at' : null, 'deleted_at' : null }")
    Optional<Cart> findByAccountId(String accountId);
}
