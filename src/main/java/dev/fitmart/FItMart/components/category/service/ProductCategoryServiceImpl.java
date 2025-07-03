package dev.fitmart.FItMart.components.category.service;

import dev.fitmart.FItMart.common.model.Filter;
import dev.fitmart.FItMart.common.model.Manage;
import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.common.service.FilterService;
import dev.fitmart.FItMart.components.category.mapping.ProductCategoryRequest;
import dev.fitmart.FItMart.components.category.mapping.ProductCategoryResponse;
import dev.fitmart.FItMart.components.category.model.ProductCategory;
import dev.fitmart.FItMart.components.category.repository.ProductCategoryRepository;
import dev.fitmart.FItMart.components.product.mapping.ProductResponse;
import dev.fitmart.FItMart.components.product.model.Product;
import dev.fitmart.FItMart.components.product.repository.ProductRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService{
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private FilterService filterService;
    @Autowired
    private  ProductRepository productRepository;

    @Override
    public Paginated<List<ProductCategoryResponse>> getAllProductCategories(int page, int limit, Map<String, String> filters, String q, int createdAtSort) {
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

        Page<ProductCategory> productPage = filterService.applyFilter(ProductCategory.class, "product_categories", filterCriteria, pageable, q, limit, createdAtSort);

        List<ProductCategoryResponse> productResponses = productPage.getContent().stream()
                .map(this::convertProductCategoryToResponse)
                .collect(Collectors.toList());

        Paginated<List<ProductCategoryResponse>> response = new Paginated<>();
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
    public ProductCategoryResponse findProductCategoryByUuid(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "UUID cannot be null or empty");
        }
        ProductCategory productCategory = productCategoryRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product Category with UUID " + uuid + " not found"));
        if (productCategory.getDeletedAt() != null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Product Category not found with uuid: " + uuid);
        }
        return convertProductCategoryToResponse(productCategory);
    }

    @Override
    public ProductCategory createProductCategory(ProductCategoryRequest productCategoryRequest) {
        // Kiểm tra handle trùng
        if (productCategoryRepository.findByHandle(productCategoryRequest.getHandle()).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Handle already exists: " + productCategoryRequest.getHandle());
        }
        ProductCategory productCategory = new ProductCategory();
        productCategory.setUuid(UuidGenerator.generateCustomUuid());
        productCategory.setCreatedAt(LocalDateTime.now());
        productCategory.setUpdatedAt(LocalDateTime.now());
        productCategory.setTitle(productCategoryRequest.getTitle());
        productCategory.setDescription(productCategoryRequest.getDescription());
        productCategory.setHandle(productCategoryRequest.getHandle());
        productCategory.setRank(productCategoryRequest.getRank());
        productCategory.setIsActive(productCategoryRequest.getIs_active());
        productCategory.setProducts(new ArrayList<>());

        String parentCategoryId = productCategoryRequest.getParent_category_id();
        if (parentCategoryId != null && !parentCategoryId.isEmpty()) {
            Optional<ProductCategory> parentCategoryOpt = productCategoryRepository.findByUuid(parentCategoryId);
            if (parentCategoryOpt.isPresent()) {
                productCategory.setParent_category_id(parentCategoryId);
                productCategory.setHas_child(true);
            } else {
                productCategory.setParent_category_id(""); // hoặc null tùy vào định nghĩa DB
                productCategory.setHas_child(false); // object rỗng
            }
        }

        return productCategoryRepository.save(productCategory);
    }

    @Override
    public ProductCategory updateProductCategory(String uuid, ProductCategoryRequest productCategory) {
        ProductCategory existingProduct = productCategoryRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product Category not found with uuid: " + uuid));

        if (productCategory.getHandle() != null && !productCategory.getHandle().equals(existingProduct.getHandle()) &&
                productCategoryRepository.findByHandle(productCategory.getHandle()).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Handle already exists: " + productCategory.getHandle());
        }
        String parentCategoryId = productCategory.getParent_category_id();
        if (parentCategoryId.equals(uuid)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Do not set parent category id with its own id");
        }

        existingProduct.setTitle(productCategory.getTitle());
        existingProduct.setDescription(productCategory.getDescription());
        existingProduct.setHandle(productCategory.getHandle());
        existingProduct.setRank(productCategory.getRank());
        existingProduct.setIsActive(productCategory.getIs_active());
        existingProduct.setUpdatedAt(LocalDateTime.now());

        if (!parentCategoryId.isEmpty()) {
            Optional<ProductCategory> parentCategoryOpt = productCategoryRepository.findByUuid(parentCategoryId);
            if (parentCategoryOpt.isPresent()) {
                existingProduct.setParent_category_id(parentCategoryId);
                existingProduct.setHas_child(true);
            } else {
                existingProduct.setParent_category_id(""); // hoặc null tùy vào định nghĩa DB
                existingProduct.setHas_child(false); // object rỗng
            }
        }

        return productCategoryRepository.save(existingProduct);
    }

    @Override
    public void deleteProductCategory(String uuid) {
        ProductCategory productCategory = productCategoryRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product Category not found with uuid: " + uuid));
//        productCategory.setDeletedAt(LocalDateTime.now());
        productCategoryRepository.delete(productCategory);
    }

    @Override
    public ProductCategoryResponse convertProductCategoryToResponse(ProductCategory productCategory) {
        ProductCategoryResponse response = new ProductCategoryResponse();
        response.setUuid(productCategory.getUuid());
        response.setTitle(productCategory.getTitle());
        response.setDescription(productCategory.getDescription());
        response.setHandle(productCategory.getHandle());
        response.setRank(productCategory.getRank());
        response.setIs_active(productCategory.getIsActive());
        response.setCreated_at(productCategory.getCreatedAt());
        response.setUpdated_at(productCategory.getUpdatedAt());
        response.setDeleted_at(productCategory.getDeletedAt());
        response.setProducts(productCategory.getProducts());
        response.setParent_category_id(productCategory.getParent_category_id());
        response.setHas_child(productCategory.getHas_child());

        return response;
    }

    @Override
    public ProductCategory manageProductsForCategory(String categoryUuid, Manage<String> manageProductUuids) {
        ProductCategory category = productCategoryRepository.findByUuid(categoryUuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product Category not found"));

        List<Product> categoryProducts = category.getProducts();
        if (categoryProducts == null) {
            categoryProducts = new ArrayList<>();
            category.setProducts(categoryProducts);
        }

        // Handle created (add product to category)
        if (manageProductUuids.getCreated() != null && !manageProductUuids.getCreated().isEmpty()) {
            for (String productUuid : manageProductUuids.getCreated()) {
                Product product = productRepository.findByUuid(productUuid).orElse(null);
                if (product == null) continue;
                // Set category for product
                product.setCategory_id(categoryUuid);
                productRepository.save(product); // Save the updated product

                if (!categoryProducts.contains(product)) {
                    categoryProducts.add(product);
                }
            }
        }

        // Handle deleted (remove product from category)
        if (manageProductUuids.getDeleted() != null && !manageProductUuids.getDeleted().isEmpty()) {
            for (String productUuid : manageProductUuids.getDeleted()) {
                Product product = productRepository.findByUuid(productUuid).orElse(null);
                if (product == null) continue;

                if (categoryProducts.contains(product)) {
                    categoryProducts.remove(product);
                }

                // Clear category from product
                product.setCategory_id(null);
                productRepository.save(product);
            }
        }

        return productCategoryRepository.save(category);
    }
}
