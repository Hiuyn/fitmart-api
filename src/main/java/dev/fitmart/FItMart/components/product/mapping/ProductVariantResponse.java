package dev.fitmart.FItMart.components.product.mapping;

import dev.fitmart.FItMart.components.product.model.ProductVariant;
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
public class ProductVariantResponse {
    private String uuid;
    private String product_id;
    private String title;
    private String sku;
    private String barcode;
    private Integer weight; // g
    private Integer height; //cm
    private Integer width;  //cm
    private Integer length;  //cm
    private Integer inventory_quantity;
    private Boolean allow_backorder;
    private List<ProductVariant.Option> options;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;
    private Long price;
    private String image;
}
