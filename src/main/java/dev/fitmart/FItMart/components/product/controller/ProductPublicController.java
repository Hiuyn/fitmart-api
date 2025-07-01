package dev.fitmart.FItMart.components.product.controller;

import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.components.product.mapping.*;
import dev.fitmart.FItMart.components.product.model.Product;
import dev.fitmart.FItMart.components.product.model.ProductOption;
import dev.fitmart.FItMart.components.product.model.ProductVariant;
import dev.fitmart.FItMart.components.product.service.ProductOptionService;
import dev.fitmart.FItMart.components.product.service.ProductService;
import dev.fitmart.FItMart.components.product.service.ProductVariantService;
import dev.fitmart.FItMart.exception.BaseResponse;
import dev.fitmart.FItMart.exception.ResponseUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/store/products")
public class ProductPublicController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductOptionService productOptionService;
    @Autowired
    private ProductVariantService productVariantService;

    @GetMapping
    public ResponseEntity<BaseResponse<Paginated<List<ProductResponse>>>> getAllProducts(
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

        Paginated<List<ProductResponse>> response = productService.getAllProducts(page, limit, filters, q, createdAtSort);
        return ResponseUtils.success(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<BaseResponse<ProductResponse>> getProductByUuid(@PathVariable String uuid) {
        ProductResponse productResponse = productService.findProductByUuid(uuid);
        return ResponseUtils.success(productResponse);
    }

    @GetMapping("/{uuid}/options")
    public ResponseEntity<BaseResponse<Paginated<List<ProductOptionResponse>>>> getOptionsByProductUuid(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "25") int limit,
            @RequestParam(required = false) String filterField,
            @RequestParam(required = false) String filterValue,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "-1") int created_at,
            @RequestParam Map<String, String> allParams,
            @PathVariable String uuid
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
        Paginated<List<ProductOptionResponse>> response = productOptionService.getAllOptions(page, limit, filters, q, createdAtSort, uuid);
        return ResponseUtils.success(response);
    }

    @GetMapping("/{uuid}/variants")
    public ResponseEntity<BaseResponse<Paginated<List<ProductVariantResponse>>>> getVariantsByProductUuid(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "25") int limit,
            @RequestParam(required = false) String filterField,
            @RequestParam(required = false) String filterValue,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "-1") int created_at,
            @RequestParam Map<String, String> allParams,
            @PathVariable String uuid
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
        Paginated<List<ProductVariantResponse>> response = productVariantService.getAllVariants(page, limit, filters, q, createdAtSort, uuid);
        return ResponseUtils.success(response);
    }
}
