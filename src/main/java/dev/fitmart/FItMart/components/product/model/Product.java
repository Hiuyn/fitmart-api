package dev.fitmart.FItMart.components.product.model;

import jakarta.validation.constraints.*;
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
    @NotBlank(message = "Title không được trống")
    @Size(min = 2, max = 100, message = "Title phải nằm trong khoảng từ 2 đến 100 ký tự")
    private String title;
    private String description;
    private String thumbnail;
    @NotBlank(message = "Slug không được trống")
    @Pattern(
            regexp = "^[a-z0-9]+(-[a-z0-9]+)*$",
            message = "Slug chỉ được chứa chữ thường và số, cách nhau bằng dấu gạch ngang, không dấu tiếng Việt"
    )
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
