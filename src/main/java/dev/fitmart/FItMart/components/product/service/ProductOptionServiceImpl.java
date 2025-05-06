package dev.fitmart.FItMart.components.product.service;

import dev.fitmart.FItMart.components.product.model.ProductOption;
import dev.fitmart.FItMart.components.product.repository.ProductOptionRepository;
import dev.fitmart.FItMart.exception.ApiException;
import dev.fitmart.FItMart.libs.UuidGenerator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ProductOptionServiceImpl implements ProductOptionService{
    private final ProductOptionRepository productOptionRepository;

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
}
