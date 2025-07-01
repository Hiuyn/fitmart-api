package dev.fitmart.FItMart.components.category.controller;

import dev.fitmart.FItMart.common.model.Manage;
import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.components.category.mapping.ProductCategoryRequest;
import dev.fitmart.FItMart.components.category.mapping.ProductCategoryResponse;
import dev.fitmart.FItMart.components.category.model.ProductCategory;
import dev.fitmart.FItMart.components.category.service.ProductCategoryService;
import dev.fitmart.FItMart.exception.BaseResponse;
import dev.fitmart.FItMart.exception.ResponseUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/store/product-categories")
public class ProductCategoryPublicController {
    @Autowired
    private ProductCategoryService productCategoryService;

    @GetMapping
    public ResponseEntity<BaseResponse<Paginated<List<ProductCategoryResponse>>>> getAllProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "25") int limit,
            @RequestParam(required = false) String filterField,
            @RequestParam(required = false) String filterValue,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "-1") int created_at,
            @RequestParam Map<String, String> allParams
    ) {
        int createdAtSort = (created_at == -1 || created_at == 1 || created_at == 0) ? created_at : -1;

        // Create filter map from dynamic query parameters
        Map<String, String> filters = new HashMap<>();

        // Add filterField and filterValue if provided (for backwards compatibility)
        if (filterField != null && filterValue != null) {
            filters.put(filterField, filterValue);
        }

        // Add other query parameters as filters, excluding reserved ones
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            if (!key.equals("page") && !key.equals("limit") && !key.equals("q") &&
                    !key.equals("created_at") && !key.equals("filterField") && !key.equals("filterValue")) {
                filters.put(key, entry.getValue());
            }
        }

        Paginated<List<ProductCategoryResponse>> response = productCategoryService.getAllProductCategories(page, limit, filters, q, createdAtSort);
        return ResponseUtils.success(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<BaseResponse<ProductCategoryResponse>> getProductByUuid(@PathVariable String uuid) {
        ProductCategoryResponse ProductCategoryResponse = productCategoryService.findProductCategoryByUuid(uuid);
        return ResponseUtils.success(ProductCategoryResponse);
    }
}
