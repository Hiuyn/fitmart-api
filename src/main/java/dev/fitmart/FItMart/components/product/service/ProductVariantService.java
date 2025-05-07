package dev.fitmart.FItMart.components.product.service;

import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.components.product.mapping.ProductVariantRequest;
import dev.fitmart.FItMart.components.product.mapping.ProductVariantResponse;
import dev.fitmart.FItMart.components.product.model.ProductVariant;

import java.util.*;

public interface ProductVariantService {
    Paginated<List<ProductVariantResponse>> getAllVariants(int page, int limit, Map<String, String> filters, String searchQuery, int createdAtSort, String productUuid);
    ProductVariantResponse getOneVariant(String uuid);
    ProductVariant createVariant(ProductVariant variant);
    ProductVariant createVariantFromRequest(ProductVariantRequest variantRequest, String productId, List<ProductVariant.Option> options);
    ProductVariant updateVariant(String uuid, ProductVariantRequest variantRequest, List<ProductVariant.Option> options);
    void deleteVariant(String uuid);
}
