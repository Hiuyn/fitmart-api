package dev.fitmart.FItMart.components.product.service;

import dev.fitmart.FItMart.components.product.model.ProductVariant;
import dev.fitmart.FItMart.components.product.repository.ProductVariantRepository;
import dev.fitmart.FItMart.exception.ApiException;
import dev.fitmart.FItMart.libs.UuidGenerator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService{
    private final ProductVariantRepository productVariantRepository;

    @Override
    public ProductVariant createVariant(ProductVariant variant) {
        variant.setUuid(UuidGenerator.generateCustomUuid());
        variant.setCreatedAt(LocalDateTime.now());
        variant.setUpdatedAt(LocalDateTime.now());
        return productVariantRepository.save(variant);
    }

    @Override
    public ProductVariant updateVariant(String uuid, ProductVariant variant) {
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
        existingVariant.setInventoryQuantity(variant.getInventoryQuantity());
        existingVariant.setAllowBackorder(variant.getAllowBackorder());
        existingVariant.setMetadata(variant.getMetadata());

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
}
