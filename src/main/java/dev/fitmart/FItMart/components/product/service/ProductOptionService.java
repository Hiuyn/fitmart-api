package dev.fitmart.FItMart.components.product.service;

import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.components.product.mapping.ProductOptionResponse;
import dev.fitmart.FItMart.components.product.mapping.ProductResponse;
import dev.fitmart.FItMart.components.product.model.ProductOption;

import java.util.List;
import java.util.Map;

public interface ProductOptionService {
    Paginated<List<ProductOptionResponse>> getAllOptions(int page, int limit, Map<String, String> filters, String q, int createdAtSort, String productUuid);
    ProductOptionResponse getOptionByUuid(String uuid);
    ProductOption createOption(ProductOption option);
    ProductOption updateOption(String uuid, ProductOption option);
    void deleteOption(String uuid);
    ProductOptionResponse convertOptionToResponse(ProductOption option);
}
