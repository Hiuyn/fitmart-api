package dev.fitmart.FItMart.components.product.service;

import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.components.product.mapping.ProductResponse;
import dev.fitmart.FItMart.components.product.model.Product;

import java.util.*;

public interface ProductService {
    Paginated<List<ProductResponse>> getAllProducts(int page, int limit, Map<String, String> filters, String q, int createdAtSort);
    ProductResponse findProductByUuid(String uuid);
    ProductResponse createProduct(Product product);
    ProductResponse updateProduct(String uuid, Product product);
    void deleteProduct(String uuid);
    ProductResponse convertProductToResponse(Product product);
}
