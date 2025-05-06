package dev.fitmart.FItMart.components.product.service;

import dev.fitmart.FItMart.components.product.model.ProductVariant;

public interface ProductVariantService {
    ProductVariant createVariant(ProductVariant variant);
    ProductVariant updateVariant(String uuid, ProductVariant variant);
    void deleteVariant(String uuid);
}
