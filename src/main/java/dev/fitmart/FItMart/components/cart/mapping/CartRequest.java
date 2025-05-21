package dev.fitmart.FItMart.components.cart.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartRequest {
    private String variant_id;
    private Integer quantity;
    private String payment_method;
}
