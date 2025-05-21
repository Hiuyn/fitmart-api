package dev.fitmart.FItMart.components.order.model;

import dev.fitmart.FItMart.components.cart.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private ObjectId id;
    private String uuid;
    private List<Cart.CartItem> items;
    @Field("account_id")
    private String accountId;
    @Field("payment_method")
    private String paymentMethod;
    @Field("total_fee")
    private Long totalFee;
    @Field("status")
    private int status; // 1-99 (see below)
    @Field("status_txt")
    private String statusTxt;
    @Field("completed_at")
    private LocalDateTime completedAt;
    @Field("created_at")
    private LocalDateTime createdAt;
    @Field("updated_at")
    private LocalDateTime updatedAt;
    @Field("deleted_at")
    private LocalDateTime deletedAt;

    public static String getStatusText(int status) {
        return switch (status) {
            case 1 -> "Đang chờ đóng hàng";
            case 2 -> "Đang chờ giao hàng";
            case 3 -> "Đang giao hàng";
            case 4 -> "Đang chờ thanh toán";
            case 5 -> "Đã thanh toán";
            case 99 -> "Đã huỷ";
            default -> "unknown";
        };
    }
}
