package dev.fitmart.FItMart.components.product.model;

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
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "products")
public class Product {
    @Id
    private ObjectId id;
    private String uuid;
    private String title;
    private String description;
    private String thumbnail;
    @NotNull
    @NotEmpty
    @NotBlank(message = "Slug is required")
    private String slug;
    private String status;
    private String type;
    @Field("category_id")
    private String categoryId;
    @Field("collection_id")
    private String collectionId;
    @Field("created_at")
    private LocalDateTime createdAt;
    @Field("updated_at")
    private LocalDateTime updatedAt;
    @Field("deleted_at")
    private LocalDateTime deletedAt;
    private Map<String, String> metadata;
}
