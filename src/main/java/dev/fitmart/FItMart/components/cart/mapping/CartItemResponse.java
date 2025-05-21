package dev.fitmart.FItMart.components.cart.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponse {
    private String id;
    private String cart_id;
    private String variant_id;
    private int quantity;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private Long price;
}
