package dev.fitmart.FItMart.components.cart.mapping;

import dev.fitmart.FItMart.components.cart.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    private String uuid;
    private String account_id;
    private LocalDateTime completed_at;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;
    private List<Cart.CartItem> items;
    private String payment_method;
}
