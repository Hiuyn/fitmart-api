package dev.fitmart.FItMart.components.order.model;

import dev.fitmart.FItMart.components.cart.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private ObjectId id;
    private String uuid;
    private List<Cart.CartItem> items;
    @Field("account_id")
    private String accountId;
    @Field("payment_method")
    private String paymentMethod;
    @Field("total_fee")
    private Double totalFee;
    @Field("packaging_status")
    private int packagingStatus; // 0: PENDING_PACKAGING, 1: PACKAGED
    @Field("shipping_status")
    private int shippingStatus; // 0: PENDING_SHIPPING, 1: SHIPPING
    @Field("payment_status")
    private int paymentStatus; // 0: UNPAID, 1: PAID
    @Field("completed_at")
    private LocalDateTime completedAt;
    @Field("created_at")
    private LocalDateTime createdAt;
    @Field("updated_at")
    private LocalDateTime updatedAt;
    @Field("deleted_at")
    private LocalDateTime deletedAt;
}
