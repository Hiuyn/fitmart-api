package dev.fitmart.FItMart.components.product.service;

import dev.fitmart.FItMart.common.model.Filter;
import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.common.service.FilterService;
import dev.fitmart.FItMart.components.product.mapping.ProductOptionResponse;
import dev.fitmart.FItMart.components.product.mapping.ProductResponse;
import dev.fitmart.FItMart.components.product.model.Product;
import dev.fitmart.FItMart.components.product.model.ProductOption;
import dev.fitmart.FItMart.components.product.repository.ProductOptionRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductOptionServiceImpl implements ProductOptionService{
    @Autowired
    private final ProductOptionRepository productOptionRepository;
    @Autowired
    private FilterService filterService;

    @Override
    public Paginated<List<ProductOptionResponse>> getAllOptions(int page, int limit, Map<String, String> filters, String q, int createdAtSort, String productUuid) {
        // Page is 0-based in Spring Data, but API uses 1-based
        Pageable pageable = limit == -1 ? PageRequest.of(0, Integer.MAX_VALUE) : PageRequest.of(page - 1, limit);

        List<Filter> filterCriteria = new ArrayList<>();
        // Add productId filter
        if (productUuid != null && !productUuid.isEmpty()) {
            Filter productIdFilter = new Filter();
            productIdFilter.setField("productId");
            productIdFilter.setValue(productUuid);
            filterCriteria.add(productIdFilter);
        }

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                continue;
            }
            Filter criteria = new Filter();
            criteria.setField(entry.getKey());
            criteria.setValue(entry.getValue());
            filterCriteria.add(criteria);
        }

        Page<ProductOption> productPage = filterService.applyFilter(ProductOption.class, "product_options", filterCriteria, pageable, q, limit, createdAtSort);

        List<ProductOptionResponse> productResponses = productPage.getContent().stream()
                .map(this::convertOptionToResponse)
                .collect(Collectors.toList());

        Paginated<List<ProductOptionResponse>> response = new Paginated<>();
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
    public ProductOptionResponse getOptionByUuid(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "UUID cannot be null or empty");
        }
        ProductOption option = productOptionRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Option not found with uuid: " + uuid));
        if (option.getDeletedAt() != null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Option not found with uuid: " + uuid);
        }
        return convertOptionToResponse(option);
    }

    @Override
    public ProductOption createOption(ProductOption option) {
        option.setUuid(UuidGenerator.generateCustomUuid());
        option.setCreatedAt(LocalDateTime.now());
        option.setUpdatedAt(LocalDateTime.now());
        return productOptionRepository.save(option);
    }

    @Override
    public ProductOption updateOption(String uuid, ProductOption option) {
        ProductOption existingOption = productOptionRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Option not found with uuid: " + uuid));

        existingOption.setTitle(option.getTitle());
        existingOption.setValues(option.getValues());
        existingOption.setUpdatedAt(LocalDateTime.now());
        return productOptionRepository.save(existingOption);
    }

    @Override
    public void deleteOption(String uuid) {
        ProductOption option = productOptionRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Option not found with uuid: " + uuid));
        option.setDeletedAt(LocalDateTime.now());
        productOptionRepository.save(option);
    }

    @Override
    public ProductOptionResponse convertOptionToResponse(ProductOption option) {
        ProductOptionResponse response = new ProductOptionResponse();
        response.setUuid(option.getUuid());
        response.setTitle(option.getTitle());
        response.setValues(option.getValues());
        response.setProduct_id(option.getProductId());
        response.setCreated_at(option.getCreatedAt());
        response.setUpdated_at(option.getUpdatedAt());
        response.setDeleted_at(option.getDeletedAt());
        return response;
    }
}
