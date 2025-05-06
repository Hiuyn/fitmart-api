package dev.fitmart.FItMart.components.product.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductOptionValueResponse {
    private String uuid;
    private String variant_id;
    private String option_id;
    private String product_id;
    private Integer value;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;
}
