package dev.fitmart.FItMart.components.order.mapping;

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
public class OrderResponse {
    private String uuid;
    private String account_id;
    private List<Cart.CartItem> items;
    private String payment_method;
    private int status;
    private String status_txt;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;
    private LocalDateTime completed_at;
    private Long total_fee;
}
