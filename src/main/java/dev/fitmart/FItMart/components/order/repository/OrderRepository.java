package dev.fitmart.FItMart.components.order.repository;

import dev.fitmart.FItMart.components.order.model.Order;
import dev.fitmart.FItMart.components.product.model.Product;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findByUuid(String uuid);
    List<Order> findByAccountId(String accountId);
}
