package dev.fitmart.FItMart.components.category.mapping;

import dev.fitmart.FItMart.components.category.model.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductCategoryRequest {
    @NotEmpty
    @NotNull
    @NotBlank(message = "Title must not be blank")
    private String title;
    @NotEmpty
    @NotNull
    @NotBlank(message = "Handle must not be blank")
    private String handle;
    private String description;
    private int rank;
    private int is_active;

    private String parent_category_id;
}
