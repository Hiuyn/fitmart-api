package dev.fitmart.FItMart.components.product.service;

import dev.fitmart.FItMart.common.model.Filter;
import dev.fitmart.FItMart.common.model.Manage;
import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.common.service.FilterService;
import dev.fitmart.FItMart.components.category.service.ProductCategoryService;
import dev.fitmart.FItMart.components.product.mapping.ProductOptionResponse;
import dev.fitmart.FItMart.components.product.mapping.ProductResponse;
import dev.fitmart.FItMart.components.product.mapping.ProductVariantResponse;
import dev.fitmart.FItMart.components.product.model.Product;
import dev.fitmart.FItMart.components.product.model.ProductOption;
import dev.fitmart.FItMart.components.product.model.ProductVariant;
import dev.fitmart.FItMart.components.product.repository.ProductOptionRepository;
import dev.fitmart.FItMart.components.product.repository.ProductRepository;
import dev.fitmart.FItMart.components.product.repository.ProductVariantRepository;
import dev.fitmart.FItMart.exception.ApiException;
import dev.fitmart.FItMart.libs.UuidGenerator;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private FilterService filterService;
    @Autowired
    private ProductOptionRepository productOptionRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private ProductOptionService productOptionService;
    @Autowired
    private ProductVariantService productVariantService;
    @Autowired
    private ProductCategoryService productCategoryService;

    @Override
    public Paginated<List<ProductResponse>> getAllProducts(int page, int limit, Map<String, String> filters, String q, int createdAtSort) {
        // Page is 0-based in Spring Data, but API uses 1-based
        Pageable pageable = limit == -1 ? PageRequest.of(0, Integer.MAX_VALUE) : PageRequest.of(page - 1, limit);

        List<Filter> filterCriteria = new ArrayList<>();
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                continue;
            }
            Filter criteria = new Filter();
            criteria.setField(entry.getKey());
            criteria.setValue(entry.getValue());
            filterCriteria.add(criteria);
        }

        Page<Product> productPage = filterService.applyFilter(Product.class, "products", filterCriteria, pageable, q, limit, createdAtSort);

        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(this::convertProductToResponse)
                .collect(Collectors.toList());

        Paginated<List<ProductResponse>> response = new Paginated<>();
        response.setData(productResponses);

        Paginated.Pagination pagination = new Paginated.Pagination();
        pagination.setTotal(productPage.getTotalElements());
        pagination.setCount(productResponses.size());
        pagination.setPerPage(limit == -1 ? productResponses.size() : limit);
        pagination.setCurrentPage(limit == -1 ? 1 : page);
        pagination.setTotalPages(limit == -1 ? 1 : productPage.getTotalPages());
        response.setPagination(pagination);

        return response;
    }

    @Override
    public ProductResponse findProductByUuid(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "UUID cannot be null or empty");
        }
        Product product = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product with UUID " + uuid + " not found"));
        if (product.getDeletedAt() != null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Product not found with uuid: " + uuid);
        }
        return convertProductToResponse(product);
    }

    @Override
    public ProductResponse createProduct(Product product) {
        // Kiểm tra slug trùng
        if (productRepository.findBySlug(product.getSlug()).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Slug already exists: " + product.getSlug());
        }

        // Gán thời gian tạo
        product.setUuid(UuidGenerator.generateCustomUuid());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        productRepository.save(product);

        if (product.getCategory_id() != null && !product.getCategory_id().isEmpty()) {
            Manage<String> manage = new Manage<>();
            manage.setCreated(Collections.singletonList(product.getUuid()));
            productCategoryService.manageProductsForCategory(product.getCategory_id(), manage);
        }

        return convertProductToResponse(product);
    }

    @Override
    public ProductResponse updateProduct(String uuid, Product product) {
        Product existingProduct = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found with uuid: " + uuid));

        if (product.getSlug() != null && !product.getSlug().equals(existingProduct.getSlug()) &&
                productRepository.findBySlug(product.getSlug()).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Slug already exists: " + product.getSlug());
        }
        String oldCategoryId = existingProduct.getCategory_id();
        String newCategoryId = product.getCategory_id();

        existingProduct.setTitle(product.getTitle());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setThumbnail(product.getThumbnail());
        existingProduct.setSlug(product.getSlug());
        existingProduct.setStatus(product.getStatus());
        existingProduct.setType(product.getType());
        existingProduct.setCategory_id(product.getCategory_id());
        existingProduct.setCollection_id(product.getCollection_id());
        existingProduct.setMetadata(product.getMetadata());

        existingProduct.setUpdatedAt(LocalDateTime.now());

        productRepository.save(existingProduct);

        if (!Objects.equals(oldCategoryId, newCategoryId)) {
            if (oldCategoryId != null && !oldCategoryId.isEmpty()) {
                Manage<String> manage = new Manage<>();
                manage.setDeleted(Collections.singletonList(uuid));
                productCategoryService.manageProductsForCategory(oldCategoryId, manage);
            }
            if (newCategoryId != null && !newCategoryId.isEmpty()) {
                Manage<String> manage = new Manage<>();
                manage.setCreated(Collections.singletonList(uuid));
                productCategoryService.manageProductsForCategory(newCategoryId, manage);
            }
        }
        return convertProductToResponse(existingProduct);
    }

    @Override
    public void deleteProduct(String uuid) {
        Product product = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found with uuid: " + uuid));
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    @Override
    public ProductResponse convertProductToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setUuid(product.getUuid());
        response.setTitle(product.getTitle());
        response.setDescription(product.getDescription());
        response.setThumbnail(product.getThumbnail());
        response.setSlug(product.getSlug());
        response.setStatus(product.getStatus());
        response.setType(product.getType());
        response.setCategory_id(product.getCategory_id());
        response.setCollection_id(product.getCollection_id());
        response.setMetadata(product.getMetadata());
        response.setCreated_at(product.getCreatedAt());
        response.setUpdated_at(product.getUpdatedAt());
        response.setDeleted_at(product.getDeletedAt());

        List<ProductOption> options = productOptionRepository.findByProductId(product.getUuid());
        List<ProductOptionResponse> optionResponses = options.stream()
                .map(productOptionService::convertOptionToResponse)
                .collect(Collectors.toList());
        response.setOptions(optionResponses);

        // Lấy variants theo productId
        List<ProductVariant> variants = productVariantRepository.findByProductId(product.getUuid());
        List<ProductVariantResponse> variantResponses = variants.stream()
                .map(productVariantService::convertVariantToResponse)
                .collect(Collectors.toList());
        response.setVariants(variantResponses);

        return response;
    }
}
