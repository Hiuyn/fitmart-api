package dev.fitmart.FItMart.components.cart.mapping;

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
    private String id;
    private String user_id;
    private LocalDateTime completed_at;         // VN
    private LocalDateTime created_at;      // 2025-04-21T10:00:00Z
    private LocalDateTime updated_at;      // 2025-04-21T10:00:00Z
    private LocalDateTime deleted_at;
    private List<CartItemResponse> cartItems;
}
