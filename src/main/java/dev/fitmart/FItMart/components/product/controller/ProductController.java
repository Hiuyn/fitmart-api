package dev.fitmart.FItMart.components.product.controller;

import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.components.product.mapping.*;
import dev.fitmart.FItMart.components.product.model.Product;
import dev.fitmart.FItMart.components.product.model.ProductOption;
import dev.fitmart.FItMart.components.product.model.ProductVariant;
import dev.fitmart.FItMart.components.product.service.ProductOptionService;
import dev.fitmart.FItMart.components.product.service.ProductService;
import dev.fitmart.FItMart.components.product.service.ProductServiceImpl;
import dev.fitmart.FItMart.components.product.service.ProductVariantService;
import dev.fitmart.FItMart.exception.BaseResponse;
import dev.fitmart.FItMart.exception.ResponseUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductOptionService productOptionService;
    @Autowired
    private ProductVariantService productVariantService;

    @PreAuthorize("hasRole('ADMIN', 'ACCOUNT')")
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

    @PreAuthorize("hasRole('ADMIN', 'ACCOUNT')")
    @GetMapping("/{uuid}")
    public ResponseEntity<BaseResponse<ProductResponse>> getProductByUuid(@PathVariable String uuid) {
        ProductResponse productResponse = productService.findProductByUuid(uuid);
        return ResponseUtils.success(productResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BaseResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        // Tạo sản phẩm
        Product product = productService.createProduct(productRequest.getProduct());
        String productId = product.getUuid();

        // Tạo options
        if (productRequest.getOptions() != null && productRequest.getOptions().getCreated() != null) {
            for (ProductOption option : productRequest.getOptions().getCreated()) {
                option.setProductId(productId);
                productOptionService.createOption(option);
            }
        }

        // Tạo variants
        if (productRequest.getVariants() != null && productRequest.getVariants().getCreated() != null) {
            List<ProductVariantRequest> variantsToCreate = productRequest.getVariants().getCreated();

            for (ProductVariantRequest variantRequest : variantsToCreate) {
                List<ProductVariant.Option> variantOptions = new ArrayList<>();
                if (variantRequest.getOptions() != null) {
                    // Get all ProductOptions for the product
                    List<ProductOptionResponse> optionResponses = productOptionService.getAllOptions(1, -1, new HashMap<>(), null, -1, productId)
                            .getData();
                    List<ProductOption> existingOptions = optionResponses.stream()
                            .map(response -> {
                                ProductOption option = new ProductOption();
                                option.setUuid(response.getUuid());
                                option.setTitle(response.getTitle());
                                option.setValues(response.getValues());
                                option.setProductId(response.getProduct_id());
                                option.setCreatedAt(response.getCreated_at());
                                option.setUpdatedAt(response.getUpdated_at());
                                option.setDeletedAt(response.getDeleted_at());
                                return option;
                            })
                            .collect(Collectors.toList());

                    // Process each key-value pair in options Object
                    for (Map.Entry<String, String> entry : variantRequest.getOptions().entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();

                        // Check if ProductOption exists with title matching key
                        ProductOption option = existingOptions.stream()
                                .filter(opt -> opt.getTitle().equalsIgnoreCase(key))
                                .findFirst()
                                .orElse(null);

                        if (option == null) {
                            // Create new ProductOption
                            option = new ProductOption();
                            option.setTitle(key);
                            option.setValues(new ArrayList<>(List.of(value)));
                            option.setProductId(productId);
                            option = productOptionService.createOption(option);
                        } else {
                            // Check if value exists in option values
                            if (!option.getValues().contains(value)) {
                                // Update ProductOption to include new value
                                option.getValues().add(value);
                                option = productOptionService.updateOption(option.getUuid(), option);
                            }
                        }

                        // Add to variant options
                        ProductVariant.Option variantOption = new ProductVariant.Option();
                        variantOption.setId(option.getUuid());
                        variantOption.setValue(value);
                        variantOptions.add(variantOption);
                    }
                }

                // Create variant from request
                productVariantService.createVariantFromRequest(variantRequest, productId, variantOptions);
            }
        }

        // Chuyển đổi sang ProductResponse
        ProductResponse response = productService.convertProductToResponse(product);
        return ResponseUtils.success(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{uuid}")
    public ResponseEntity<BaseResponse<ProductResponse>> updateProduct(@PathVariable String uuid, @Valid @RequestBody ProductRequest productRequest) {
        // Cập nhật sản phẩm
        Product product = productService.updateProduct(uuid, productRequest.getProduct());
        String productId = product.getUuid();

        // Xử lý options
        if (productRequest.getOptions() != null) {
            // Create new options
            if (productRequest.getOptions().getCreated() != null && !productRequest.getOptions().getCreated().isEmpty()) {
                for (ProductOption option : productRequest.getOptions().getCreated()) {
                    option.setProductId(productId);
                    productOptionService.createOption(option);
                }
            }
            // Update options
            if (productRequest.getOptions().getUpdated() != null && !productRequest.getOptions().getUpdated().isEmpty()) {
                for (ProductOption option : productRequest.getOptions().getUpdated()) {
                    productOptionService.updateOption(option.getUuid(), option);
                }
            }
            // Delete options
            if (productRequest.getOptions().getDeleted() != null && !productRequest.getOptions().getDeleted().isEmpty()) {
                for (String optionUuid : productRequest.getOptions().getDeleted()) {
                    productOptionService.deleteOption(optionUuid);
                }
            }
        }

        // Handle variants and process options as Object
        if (productRequest.getVariants() != null) {
            // Create new variants
            if (productRequest.getVariants().getCreated() != null && !productRequest.getVariants().getCreated().isEmpty()) {
                for (ProductVariantRequest variantRequest : productRequest.getVariants().getCreated()) {
                    List<ProductVariant.Option> variantOptions = new ArrayList<>();
                    if (variantRequest.getOptions() != null) {
                        List<ProductOptionResponse> optionResponses = productOptionService.getAllOptions(1, -1, new HashMap<>(), null, -1, productId)
                                .getData();
                        List<ProductOption> existingOptions = optionResponses.stream()
                                .map(response -> {
                                    ProductOption option = new ProductOption();
                                    option.setUuid(response.getUuid());
                                    option.setTitle(response.getTitle());
                                    option.setValues(response.getValues());
                                    option.setProductId(response.getProduct_id());
                                    option.setCreatedAt(response.getCreated_at());
                                    option.setUpdatedAt(response.getUpdated_at());
                                    option.setDeletedAt(response.getDeleted_at());
                                    return option;
                                })
                                .collect(Collectors.toList());

                        for (Map.Entry<String, String> entry : variantRequest.getOptions().entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();

                            ProductOption option = existingOptions.stream()
                                    .filter(opt -> opt.getTitle().equalsIgnoreCase(key))
                                    .findFirst()
                                    .orElse(null);

                            if (option == null) {
                                option = new ProductOption();
                                option.setTitle(key);
                                option.setValues(new ArrayList<>(List.of(value)));
                                option.setProductId(productId);
                                option = productOptionService.createOption(option);
                            } else {
                                if (!option.getValues().contains(value)) {
                                    option.getValues().add(value);
                                    option = productOptionService.updateOption(option.getUuid(), option);
                                }
                            }

                            ProductVariant.Option variantOption = new ProductVariant.Option();
                            variantOption.setId(option.getUuid());
                            variantOption.setValue(value);
                            variantOptions.add(variantOption);
                        }
                    }

                    productVariantService.createVariantFromRequest(variantRequest, productId, variantOptions);
                }
            }
            // Update variants
            if (productRequest.getVariants().getUpdated() != null && !productRequest.getVariants().getUpdated().isEmpty()) {
                for (ProductVariantRequest variantRequest : productRequest.getVariants().getUpdated()) {
                    List<ProductVariant.Option> variantOptions = new ArrayList<>();
                    if (variantRequest.getOptions() != null) {
                        List<ProductOptionResponse> optionResponses = productOptionService.getAllOptions(1, -1, new HashMap<>(), null, -1, productId)
                                .getData();
                        List<ProductOption> existingOptions = optionResponses.stream()
                                .map(response -> {
                                    ProductOption option = new ProductOption();
                                    option.setUuid(response.getUuid());
                                    option.setTitle(response.getTitle());
                                    option.setValues(response.getValues());
                                    option.setProductId(response.getProduct_id());
                                    option.setCreatedAt(response.getCreated_at());
                                    option.setUpdatedAt(response.getUpdated_at());
                                    option.setDeletedAt(response.getDeleted_at());
                                    return option;
                                })
                                .collect(Collectors.toList());

                        for (Map.Entry<String, String> entry : variantRequest.getOptions().entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();

                            ProductOption option = existingOptions.stream()
                                    .filter(opt -> opt.getTitle().equalsIgnoreCase(key))
                                    .findFirst()
                                    .orElse(null);

                            if (option == null) {
                                option = new ProductOption();
                                option.setTitle(key);
                                option.setValues(new ArrayList<>(List.of(value)));
                                option.setProductId(productId);
                                option = productOptionService.createOption(option);
                            } else {
                                if (!option.getValues().contains(value)) {
                                    option.getValues().add(value);
                                    option = productOptionService.updateOption(option.getUuid(), option);
                                }
                            }

                            ProductVariant.Option variantOption = new ProductVariant.Option();
                            variantOption.setId(option.getUuid());
                            variantOption.setValue(value);
                            variantOptions.add(variantOption);
                        }
                    }

                    productVariantService.updateVariant(productId, variantRequest, variantOptions);
                }
            }
            // Delete variants
            if (productRequest.getVariants().getDeleted() != null && !productRequest.getVariants().getDeleted().isEmpty()) {
                for (String variantUuid : productRequest.getVariants().getDeleted()) {
                    productVariantService.deleteVariant(variantUuid);
                }
            }
        }

        // Chuyển đổi sang ProductResponse
        ProductResponse response = productService.convertProductToResponse(product);
        return ResponseUtils.success(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{uuid}")
    public ResponseEntity<BaseResponse<Void>> deleteProduct(@PathVariable String uuid, @RequestBody(required = false) ProductRequest productRequest) {
        // Xóa sản phẩm (soft delete)
        productService.deleteProduct(uuid);

        // Xử lý options
        if (productRequest != null && productRequest.getOptions() != null && productRequest.getOptions().getDeleted() != null) {
            for (String optionUuid : productRequest.getOptions().getDeleted()) {
                productOptionService.deleteOption(optionUuid);
            }
        }

        // Xử lý variants
        if (productRequest != null && productRequest.getVariants() != null && productRequest.getVariants().getDeleted() != null) {
            for (String variantUuid : productRequest.getVariants().getDeleted()) {
                productVariantService.deleteVariant(variantUuid);
            }
        }

        return ResponseUtils.noContent();
    }

    @PreAuthorize("hasRole('ADMIN', 'ACCOUNT')")
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

    @PreAuthorize("hasRole('ADMIN', 'ACCOUNT')")
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
