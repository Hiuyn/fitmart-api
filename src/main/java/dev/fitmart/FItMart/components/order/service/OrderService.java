package dev.fitmart.FItMart.components.order.service;

import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.components.cart.model.Cart;
import dev.fitmart.FItMart.components.order.mapping.OrderRequest;
import dev.fitmart.FItMart.components.order.mapping.OrderResponse;
import dev.fitmart.FItMart.components.order.model.Order;
import dev.fitmart.FItMart.components.product.mapping.ProductResponse;

import java.util.List;
import java.util.Map;

public interface OrderService {
    OrderResponse createOrder(Cart cart, List<Cart.CartItem> items);
    Paginated<List<OrderResponse>> getAllOrders(int page, int limit, Map<String, String> filters, String q, int createdAtSort);
    OrderResponse getOrder(String uuid);
    OrderResponse updateOrderStatus(String uuid, OrderRequest orderRequest);
}
