package dev.fitmart.FItMart.components.product.model;

import dev.fitmart.FItMart.components.cart.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "product_option_values")
public class ProductOptionValue {
    @Id
    private ObjectId id;
    private String uuid;
    private Integer value;
    @Field("variant_id")
    private String variantId;
    @Field("product_id")
    private String productId;
    @Field("option_id")
    private String optionId;
    @Field("created_at")
    private LocalDateTime createdAt;
    @Field("updated_at")
    private LocalDateTime updatedAt;
    @Field("deleted_at")
    private LocalDateTime deletedAt;
}
