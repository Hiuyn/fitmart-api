package dev.fitmart.FItMart.components.cart.model;

import dev.fitmart.FItMart.components.product.mapping.ProductVariantResponse;
import dev.fitmart.FItMart.components.product.model.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.*;

@Document(collection = "carts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cart {
    @Id
    private ObjectId id;
    private String uuid;
    @Field("account_id")
    private String accountId;
    @Field("created_at")
    private LocalDateTime createdAt;
    @Field("updated_at")
    private LocalDateTime updatedAt;
    @Field("deleted_at")
    private LocalDateTime deletedAt;
    private List<CartItem> items = new ArrayList<>();
    @Field("payment_method")
    private String paymentMethod;

    @Data
    @Builder
    public static class CartItem {
        private String uuid;
        private String variant_id;
        private ProductVariantResponse variant;
        private int quantity;
        private Long price;
        private int is_check; // 0: false, 1: true
    }
}
