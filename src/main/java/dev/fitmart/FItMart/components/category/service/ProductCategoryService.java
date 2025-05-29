package dev.fitmart.FItMart.components.category.service;

import dev.fitmart.FItMart.common.model.Manage;
import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.components.category.mapping.ProductCategoryRequest;
import dev.fitmart.FItMart.components.category.mapping.ProductCategoryResponse;
import dev.fitmart.FItMart.components.category.model.ProductCategory;
import dev.fitmart.FItMart.components.product.mapping.ProductResponse;
import dev.fitmart.FItMart.components.product.model.Product;

import java.util.List;
import java.util.Map;

public interface ProductCategoryService {
    Paginated<List<ProductCategoryResponse>> getAllProductCategories(int page, int limit, Map<String, String> filters, String q, int createdAtSort);
    ProductCategoryResponse findProductCategoryByUuid(String uuid);
    ProductCategory createProductCategory(ProductCategoryRequest productCategoryRequest);
    ProductCategory updateProductCategory(String uuid, ProductCategoryRequest productCategoryRequest);
    void deleteProductCategory(String uuid);
    ProductCategoryResponse convertProductCategoryToResponse(ProductCategory productCategory);
    ProductCategory manageProductsForCategory(String categoryUuid, Manage<String> manageProductUuids);
}
