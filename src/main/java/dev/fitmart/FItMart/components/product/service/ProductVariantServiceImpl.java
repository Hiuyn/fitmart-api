package dev.fitmart.FItMart.components.product.service;

import dev.fitmart.FItMart.common.model.Filter;
import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.common.service.FilterService;
import dev.fitmart.FItMart.components.product.mapping.ProductVariantRequest;
import dev.fitmart.FItMart.components.product.mapping.ProductVariantResponse;
import dev.fitmart.FItMart.components.product.model.ProductVariant;
import dev.fitmart.FItMart.components.product.model.ProductOption;
import dev.fitmart.FItMart.components.product.repository.ProductOptionRepository;
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
public class ProductVariantServiceImpl implements ProductVariantService{
    @Autowired
    private final ProductVariantRepository productVariantRepository;
    @Autowired
    private final ProductOptionRepository productOptionRepository;
    @Autowired
    private FilterService filterService;

    @Override
    public ProductVariant createVariant(ProductVariant variant) {
        variant.setUuid(UuidGenerator.generateCustomUuid());
        variant.setCreatedAt(LocalDateTime.now());
        variant.setUpdatedAt(LocalDateTime.now());
        variant.setPrice(variant.getPrice());
        variant.setImage(variant.getImage());
        variant.setAllow_backorder(variant.getInventory_quantity() != null && variant.getInventory_quantity() > 0);

        // Validate options
        if (variant.getOptions() != null) {
            List<ProductOption> productOptions = productOptionRepository.findByProductId(variant.getProductId());
            for (ProductVariant.Option option : variant.getOptions()) {
                ProductOption productOption = productOptions.stream()
                        .filter(opt -> opt.getUuid().equals(option.getId()))
                        .findFirst()
                        .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Option with uuid '" + option.getId() + "' not found"));
                if (!productOption.getValues().contains(option.getValue())) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid value '" + option.getValue() + "' for option '" + productOption.getTitle() + "'");
                }
            }
        }

        return productVariantRepository.save(variant);
    }

    @Override
    public ProductVariant createVariantFromRequest(ProductVariantRequest variantRequest, String productId, List<ProductVariant.Option> options) {
        ProductVariant variant = new ProductVariant();
        variant.setTitle(variantRequest.getTitle());
        variant.setSku(variantRequest.getSku());
        variant.setBarcode(variantRequest.getBarcode());
        variant.setWeight(variantRequest.getWeight());
        variant.setHeight(variantRequest.getHeight());
        variant.setWidth(variantRequest.getWidth());
        variant.setLength(variantRequest.getLength());
        variant.setInventory_quantity(variantRequest.getInventory_quantity());
        variant.setOptions(options);
        variant.setProductId(productId);
        variant.setPrice(variantRequest.getPrice());
        variant.setImage(variantRequest.getImage());
        return createVariant(variant);
    }

    @Override
    public ProductVariant updateVariant(String uuid, ProductVariantRequest variant, List<ProductVariant.Option> options) {
        ProductVariant existingVariant = productVariantRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Variant not found with uuid: " + uuid));

        if (variant.getSku() != null && !variant.getSku().equals(existingVariant.getSku()) &&
                productVariantRepository.findBySku(variant.getSku()).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "SKU already exists: " + variant.getSku());
        }

        existingVariant.setTitle(variant.getTitle());
        existingVariant.setSku(variant.getSku());
        existingVariant.setBarcode(variant.getBarcode());
        existingVariant.setWeight(variant.getWeight());
        existingVariant.setHeight(variant.getHeight());
        existingVariant.setWidth(variant.getWidth());
        existingVariant.setLength(variant.getLength());

        existingVariant.setPrice(variant.getPrice());
        existingVariant.setImage(variant.getImage());
        existingVariant.setInventory_quantity(variant.getInventory_quantity());
        existingVariant.setAllow_backorder(variant.getInventory_quantity() != null && variant.getInventory_quantity() > 0);

        List<ProductOption> productOptions = productOptionRepository.findByProductId(existingVariant.getProductId());
        for (ProductVariant.Option option : options) {
            ProductOption productOption = productOptions.stream()
                    .filter(opt -> opt.getUuid().equals(option.getId()))
                    .findFirst()
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Option with uuid '" + option.getId() + "' not found"));
            if (!productOption.getValues().contains(option.getValue())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid value '" + option.getValue() + "' for option '" + productOption.getTitle() + "'");
            }
        }

        existingVariant.setOptions(options);

        existingVariant.setUpdatedAt(LocalDateTime.now());
        return productVariantRepository.save(existingVariant);
    }

    @Override
    public void deleteVariant(String uuid) {
        ProductVariant variant = productVariantRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Variant not found with uuid: " + uuid));
        variant.setDeletedAt(LocalDateTime.now());
        productVariantRepository.save(variant);
    }

    @Override
    public Paginated<List<ProductVariantResponse>> getAllVariants(int page, int limit, Map<String, String> filters, String q, int createdAtSort, String uuid) {
        // Page is 0-based in Spring Data, but API uses 1-based
        Pageable pageable = limit == -1 ? PageRequest.of(0, Integer.MAX_VALUE) : PageRequest.of(page - 1, limit);

        List<Filter> filterCriteria = new ArrayList<>();
        // Add productId filter
        if (uuid != null && !uuid.isEmpty()) {
            Filter productIdFilter = new Filter();
            productIdFilter.setField("productId");
            productIdFilter.setValue(uuid);
            filterCriteria.add(productIdFilter);
        }

        // Add other filters
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                continue;
            }
            Filter criteria = new Filter();
            criteria.setField(entry.getKey());
            criteria.setValue(entry.getValue());
            filterCriteria.add(criteria);
        }

        // Apply filters and search using filterService
        Page<ProductVariant> variantPage = filterService.applyFilter(
                ProductVariant.class, "product_variants", filterCriteria, pageable, q, limit, createdAtSort);

        List<ProductVariantResponse> variantResponses = variantPage.getContent().stream()
                .map(this::convertVariantToResponse)
                .collect(Collectors.toList());

        Paginated<List<ProductVariantResponse>> response = new Paginated<>();
        response.setData(variantResponses);

        Paginated.Pagination pagination = new Paginated.Pagination();
        pagination.setTotal(variantPage.getTotalElements());
        pagination.setCount(variantResponses.size());
        pagination.setPerPage(limit == -1 ? variantResponses.size() : limit);
        pagination.setCurrentPage(limit == -1 ? 1 : page);
        pagination.setTotalPages(limit == -1 ? 1 : variantPage.getTotalPages());
        response.setPagination(pagination);

        return response;
    }

    @Override
    public ProductVariantResponse getOneVariant(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "UUID cannot be null or empty");
        }
        ProductVariant variant = productVariantRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Variant not found with uuid: " + uuid));
        if (variant.getDeletedAt() != null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Variant not found with uuid: " + uuid);
        }
        return convertVariantToResponse(variant);
    }

    @Override
    public ProductVariantResponse convertVariantToResponse(ProductVariant variant) {
        ProductVariantResponse response = new ProductVariantResponse();
        response.setUuid(variant.getUuid());
        response.setTitle(variant.getTitle());
        response.setSku(variant.getSku());
        response.setBarcode(variant.getBarcode());
        response.setWeight(variant.getWeight());
        response.setHeight(variant.getHeight());
        response.setWidth(variant.getWidth());
        response.setLength(variant.getLength());
        response.setInventory_quantity(variant.getInventory_quantity());
        response.setAllow_backorder(variant.getAllow_backorder());
        response.setProduct_id(variant.getProductId());
        response.setOptions(variant.getOptions());
        response.setPrice(variant.getPrice());
        response.setImage(variant.getImage());
        response.setCreated_at(variant.getCreatedAt());
        response.setUpdated_at(variant.getUpdatedAt());
        response.setDeleted_at(variant.getDeletedAt());
        return response;
    }
}
