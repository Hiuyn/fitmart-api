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
    private String accountId;
    private List<Cart.CartItem> items;
    private String paymentMethod;
    private int packagingStatus;
    private int shippingStatus;
    private int paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double total_fee;
}
