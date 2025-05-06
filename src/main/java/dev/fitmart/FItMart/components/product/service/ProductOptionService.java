package dev.fitmart.FItMart.components.product.service;

import dev.fitmart.FItMart.components.product.model.ProductOption;

public interface ProductOptionService {
    ProductOption createOption(ProductOption option);
    ProductOption updateOption(String uuid, ProductOption option);
    void deleteOption(String uuid);
}
