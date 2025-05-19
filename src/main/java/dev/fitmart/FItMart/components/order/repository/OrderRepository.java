package dev.fitmart.FItMart.components.order.repository;

import dev.fitmart.FItMart.components.order.model.Order;
import dev.fitmart.FItMart.components.product.model.Product;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, ObjectId> {
}
