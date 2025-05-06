package dev.fitmart.FItMart.components.product.controller;

import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.components.product.mapping.ProductRequest;
import dev.fitmart.FItMart.components.product.mapping.ProductResponse;
import dev.fitmart.FItMart.components.product.model.Product;
import dev.fitmart.FItMart.components.product.model.ProductOption;
import dev.fitmart.FItMart.components.product.model.ProductVariant;
import dev.fitmart.FItMart.components.product.service.ProductOptionService;
import dev.fitmart.FItMart.components.product.service.ProductService;
import dev.fitmart.FItMart.components.product.service.ProductServiceImpl;
import dev.fitmart.FItMart.components.product.service.ProductVariantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductOptionService productOptionService;
    @Autowired
    private ProductVariantService productVariantService;

    @GetMapping
    public ResponseEntity<Paginated<List<ProductResponse>>> getAllProducts(
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
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{uuid}")
    public ResponseEntity<ProductResponse> getProductByUuid(@PathVariable String uuid) {
        ProductResponse productResponse = productService.findProductByUuid(uuid);
        return ResponseEntity.ok(productResponse);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
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
            for (ProductVariant variant : productRequest.getVariants().getCreated()) {
                variant.setProductId(productId);
                productVariantService.createVariant(variant);
            }
        }

        // Chuyển đổi sang ProductResponse
        ProductResponse response = ((ProductServiceImpl) productService).convertProductToResponse(product);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable String uuid, @Valid @RequestBody ProductRequest productRequest) {
        // Cập nhật sản phẩm
        Product product = productService.updateProduct(uuid, productRequest.getProduct());
        String productId = product.getUuid();

        // Xử lý options
        if (productRequest.getOptions() != null) {
            // Tạo mới options
            if (productRequest.getOptions().getCreated() != null && !productRequest.getOptions().getCreated().isEmpty()) {
                for (ProductOption option : productRequest.getOptions().getCreated()) {
                    option.setProductId(productId);
                    productOptionService.createOption(option);
                }
            }
            // Cập nhật options
            if (productRequest.getOptions().getUpdated() != null && !productRequest.getOptions().getUpdated().isEmpty()) {
                for (ProductOption option : productRequest.getOptions().getUpdated()) {
                    productOptionService.updateOption(option.getUuid(), option);
                }
            }
            // Xóa options
            if (productRequest.getOptions().getDeleted() != null && !productRequest.getOptions().getDeleted().isEmpty()) {
                for (String optionUuid : productRequest.getOptions().getDeleted()) {
                    productOptionService.deleteOption(optionUuid);
                }
            }
        }

        // Xử lý variants
        if (productRequest.getVariants() != null) {
            // Tạo mới variants
            if (productRequest.getVariants().getCreated() != null && !productRequest.getVariants().getCreated().isEmpty()) {
                for (ProductVariant variant : productRequest.getVariants().getCreated()) {
                    variant.setProductId(productId);
                    productVariantService.createVariant(variant);
                }
            }
            // Cập nhật variants
            if (productRequest.getVariants().getUpdated() != null && !productRequest.getVariants().getUpdated().isEmpty()) {
                for (ProductVariant variant : productRequest.getVariants().getUpdated()) {
                    productVariantService.updateVariant(variant.getUuid(), variant);
                }
            }
            // Xóa variants
            if (productRequest.getVariants().getDeleted() != null && !productRequest.getVariants().getDeleted().isEmpty()) {
                for (String variantUuid : productRequest.getVariants().getDeleted()) {
                    productVariantService.deleteVariant(variantUuid);
                }
            }
        }

        // Chuyển đổi sang ProductResponse
        ProductResponse response = ((ProductServiceImpl) productService).convertProductToResponse(product);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String uuid, @RequestBody(required = false) ProductRequest productRequest) {
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

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
