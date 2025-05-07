package dev.fitmart.FItMart.components.product.mapping;

import lombok.Data;

import java.util.Map;

@Data
public class ProductVariantRequest {
    private String title;
    private String sku;
    private String barcode;
    private Integer weight;
    private Integer height;
    private Integer width;
    private Integer length;
    private Integer inventory_quantity;
    private Map<String, String> options;
}
