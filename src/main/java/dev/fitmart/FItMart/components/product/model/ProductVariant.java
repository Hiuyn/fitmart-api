package dev.fitmart.FItMart.components.product.model;

import jakarta.validation.constraints.NotBlank;
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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "product_variants")
public class ProductVariant {
    @Id
    private ObjectId id;
    private String uuid;
    private String title;
    @NotBlank(message = "SKU is required")
    private String sku;
    private String barcode;
    private Integer weight; // g
    private Integer height;
    private Integer width;
    private Integer length;
    @Field("inventory_quantity")
    private Integer inventoryQuantity;
    @Field("allow_backorder")
    private Boolean allowBackorder;
    private Map<String, String> metadata;
    @Field("product_id")
    private String productId;
    @Field("created_at")
    private LocalDateTime createdAt;
    @Field("updated_at")
    private LocalDateTime updatedAt;
    @Field("deleted_at")
    private LocalDateTime deletedAt;
}
