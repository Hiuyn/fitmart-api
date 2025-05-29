package dev.fitmart.FItMart.components.category.mapping;

import dev.fitmart.FItMart.components.category.model.ProductCategory;
import dev.fitmart.FItMart.components.product.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCategoryResponse {
    private String uuid;
    private String title;
    private String description;
    private String handle;
    private int rank;
    private int is_active;
    private List<Product> products;
    private String parent_category_id;
    private int has_child;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;
}
