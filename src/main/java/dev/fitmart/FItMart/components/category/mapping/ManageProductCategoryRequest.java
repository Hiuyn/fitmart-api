package dev.fitmart.FItMart.components.category.mapping;

import dev.fitmart.FItMart.common.model.Manage;
import dev.fitmart.FItMart.components.product.model.Product;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class ManageProductCategoryRequest {
    @Valid
    private Manage<Product> products;
}
