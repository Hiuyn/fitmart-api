package dev.fitmart.FItMart.components.category.model;

import dev.fitmart.FItMart.components.cart.model.Cart;
import dev.fitmart.FItMart.components.product.model.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "product_categories")
public class ProductCategory {
    @Id
    private ObjectId id;
    private String uuid;
    private String title;
    private String handle;
    private String description;
    private int rank;
    @Field("is_active")
    private Boolean isActive;
    private List<Product> products;
    private String parent_category_id;
    private Boolean has_child; // 0: false, 1: true
    @Field("created_at")
    private LocalDateTime createdAt;
    @Field("updated_at")
    private LocalDateTime updatedAt;
    @Field("deleted_at")
    private LocalDateTime deletedAt;
}
