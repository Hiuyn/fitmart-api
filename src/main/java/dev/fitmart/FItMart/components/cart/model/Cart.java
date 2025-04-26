package dev.fitmart.FItMart.components.cart.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.*;

@Document(collection = "carts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cart {
    @Id
    private ObjectId id;
    private String uuid;                // FMn4k9x2r5
    private String userId;     // 100000
    private LocalDateTime completedAt;         // VN
    private LocalDateTime createdAt;      // 2025-04-21T10:00:00Z
    private LocalDateTime updatedAt;      // 2025-04-21T10:00:00Z
    private LocalDateTime deletedAt;
    private List<CartItem> cartItems = new ArrayList<>();
}
