package dev.fitmart.FItMart.components.product.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private String uuid;
    private String title;
    private String description;
    private String thumbnail;
    private String slug;
    private String status;
    private String type;
    private String category_id;
    private String collection_id;
    private Map<String, String> metadata;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;

    private List<ProductOptionResponse> options;
    private List<ProductVariantResponse> variants;
}
