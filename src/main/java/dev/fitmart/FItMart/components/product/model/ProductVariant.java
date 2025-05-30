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
@Document(collection = "product_variants")
public class ProductVariant {
    @Id
    private ObjectId id;
    private String uuid;
    @NotBlank(message = "Title không được trống")
    @Size(min = 2, max = 100, message = "Title phải nằm trong khoảng từ 2 đến 100 ký tự")
    private String title;
    @NotBlank(message = "SKU không được để trống")
    @Pattern(
            regexp = "^[a-zA-Z0-9]+(-[a-zA-Z0-9]+)*$",
            message = "SKU chỉ được chứa chữ và số, cách nhau bằng dấu gạch ngang, không dấu tiếng Việt"
    )
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
    private List<Option> options;
    private Long price;
    @Field("product_id")
    private String productId;
    @Field("created_at")
    private LocalDateTime createdAt;
    @Field("updated_at")
    private LocalDateTime updatedAt;
    @Field("deleted_at")
    private LocalDateTime deletedAt;
    private String image;

    @Data
    public static class Option {
        private String id; // uuid của ProductOptionValue
        private String value; // Giá trị như "S", "Red"
    }
}
