package dev.fitmart.FItMart.components.category.mapping;

import dev.fitmart.FItMart.components.category.model.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductCategoryRequest {
    @NotBlank(message = "Title không được trống")
    private String title;
    @NotBlank(message = "Handle không được trống")
    private String handle;
    private String description;
    private int rank;
    private Boolean is_active;

    private String parent_category_id;
}
