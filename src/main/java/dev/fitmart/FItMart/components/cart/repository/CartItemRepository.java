package dev.fitmart.FItMart.components.cart.repository;

import dev.fitmart.FItMart.components.cart.model.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends MongoRepository<CartItem, String> {
    List<CartItem> findByCartIdAndDeletedAtIsNull(String cartId);
    Optional<CartItem> findByUuidAndDeletedAtIsNull(String uuid);
    Optional<CartItem> findByCartIdAndProductIdAndVariantIdAndDeletedAtIsNull(
            String cartId, String productId, String variantId);
}
