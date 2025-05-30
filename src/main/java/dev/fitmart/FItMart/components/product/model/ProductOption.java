package dev.fitmart.FItMart.components.product.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Document(collection = "product_options")
public class ProductOption {
    @Id
    private ObjectId id;
    private String uuid;
    @NotBlank(message = "Title không được trống")
    @Size(min = 2, max = 100, message = "Title phải nằm trong khoảng từ 2 đến 100 ký tự")
    private String title;
    private List<String> values;
    @Field("product_id")
    private String productId;
    @Field("created_at")
    private LocalDateTime createdAt;
    @Field("updated_at")
    private LocalDateTime updatedAt;
    @Field("deleted_at")
    private LocalDateTime deletedAt;
}
