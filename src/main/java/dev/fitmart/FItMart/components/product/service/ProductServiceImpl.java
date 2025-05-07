package dev.fitmart.FItMart.components.product.service;

import dev.fitmart.FItMart.common.model.Filter;
import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.common.service.FilterService;
import dev.fitmart.FItMart.components.product.mapping.ProductOptionResponse;
import dev.fitmart.FItMart.components.product.mapping.ProductOptionValueResponse;
import dev.fitmart.FItMart.components.product.mapping.ProductResponse;
import dev.fitmart.FItMart.components.product.mapping.ProductVariantResponse;
import dev.fitmart.FItMart.components.product.model.Product;
import dev.fitmart.FItMart.components.product.model.ProductOption;
import dev.fitmart.FItMart.components.product.model.ProductOptionValue;
import dev.fitmart.FItMart.components.product.model.ProductVariant;
import dev.fitmart.FItMart.components.product.repository.ProductOptionRepository;
import dev.fitmart.FItMart.components.product.repository.ProductOptionValueRepository;
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
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private ProductOptionRepository productOptionRepository;
    @Autowired
    private ProductOptionValueRepository productOptionValueRepository;
    @Autowired
    private FilterService filterService;

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
    public Product createProduct(Product product) {
        // Kiểm tra slug trùng
        if (productRepository.findBySlug(product.getSlug()).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Slug already exists: " + product.getSlug());
        }

        // Gán thời gian tạo
        product.setUuid(UuidGenerator.generateCustomUuid());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(String uuid, Product product) {
        Product existingProduct = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found with uuid: " + uuid));

        if (product.getSlug() != null && !product.getSlug().equals(existingProduct.getSlug()) &&
                productRepository.findBySlug(product.getSlug()).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Slug already exists: " + product.getSlug());
        }

        existingProduct.setTitle(product.getTitle());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setThumbnail(product.getThumbnail());
        existingProduct.setSlug(product.getSlug());
        existingProduct.setStatus(product.getStatus());
        existingProduct.setType(product.getType());
        existingProduct.setCategoryId(product.getCategoryId());
        existingProduct.setCollectionId(product.getCollectionId());
        existingProduct.setMetadata(product.getMetadata());

        existingProduct.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(existingProduct);
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
        response.setCategory_id(product.getCategoryId());
        response.setCollection_id(product.getCollectionId());
        response.setMetadata(product.getMetadata());
        response.setCreated_at(product.getCreatedAt());
        response.setUpdated_at(product.getUpdatedAt());
        response.setDeleted_at(product.getDeletedAt());

        return response;
    }

//    public ProductRequest convertProductToRequest(Product product) {
//        ProductRequest request = new ProductRequest();
//        request.setTitle(product.getTitle());
//        request.setDescription(product.getDescription());
//        request.setThumbnail(product.getThumbnail());
//        request.setHandle(product.getHandle());
//        request.setStatus(product.getStatus());
//        request.setType(product.getType());
//        request.setCategoryId(product.getCategoryId());
//        request.setCollectionId(product.getCollectionId());
//        request.setMetadata(product.getMetadata());
//
//        // Fetch and convert variants
//        List<ProductVariant> variants = productVariantRepository.findByProductId(product.getUuid());
//        List<ProductVariantRequest> variantRequests = variants.stream().map(variant -> {
//            ProductVariantRequest varRequest = new ProductVariantRequest();
//            varRequest.setTitle(variant.getTitle());
//            varRequest.setSku(variant.getSku());
//            varRequest.setBarcode(variant.getBarcode());
//            varRequest.setWeight(variant.getWeight());
//            varRequest.setHeight(variant.getHeight());
//            varRequest.setWidth(variant.getWidth());
//            varRequest.setLength(variant.getLength());
//            varRequest.setInventoryQuantity(variant.getInventoryQuantity());
//            varRequest.setAllowBackorder(variant.getAllowBackorder());
//            varRequest.setMetadata(variant.getMetadata());
//
//            // Fetch and convert option values
//            List<ProductOptionValue> optionValues = productOptionValueRepository.findByVariantId(variant.getUuid());
//            List<ProductOptionValueRequest> optionValueRequests = optionValues.stream().map(ov -> {
//                ProductOptionValueRequest ovRequest = new ProductOptionValueRequest();
//                ovRequest.setOptionId(ov.getOptionId());
//                ovRequest.setValue(ov.getValue());
//                return ovRequest;
//            }).collect(Collectors.toList());
//            varRequest.setOptionValues(optionValueRequests);
//
//            return varRequest;
//        }).collect(Collectors.toList());
//        request.setVariants(variantRequests);
//
//        // Fetch and convert options
//        List<ProductOption> options = productOptionRepository.findByProductId(product.getUuid());
//        List<ProductOptionRequest> optionRequests = options.stream().map(option -> {
//            ProductOptionRequest optRequest = new ProductOptionRequest();
//            optRequest.setTitle(option.getTitle());
//            return optRequest;
//        }).collect(Collectors.toList());
//        request.setOptions(optionRequests);
//
//        return request;
//    }
}
