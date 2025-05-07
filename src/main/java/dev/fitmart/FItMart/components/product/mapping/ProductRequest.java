package dev.fitmart.FItMart.components.product.mapping;

import dev.fitmart.FItMart.common.model.Manage;
import dev.fitmart.FItMart.components.product.model.Product;
import dev.fitmart.FItMart.components.product.model.ProductOption;
import dev.fitmart.FItMart.components.product.model.ProductVariant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.*;

@Data
public class ProductRequest {
    @NotNull(message = "Product is required")
    @Valid
    private Product product;

    @Valid
    private Manage<ProductOption> options;

    @Valid
    private Manage<ProductVariantRequest> variants;
}
