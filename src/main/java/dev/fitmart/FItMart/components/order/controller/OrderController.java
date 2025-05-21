package dev.fitmart.FItMart.components.order.controller;

import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.components.order.mapping.OrderResponse;
import dev.fitmart.FItMart.components.order.service.OrderService;
import dev.fitmart.FItMart.components.product.mapping.ProductResponse;
import dev.fitmart.FItMart.exception.ApiResponse;
import dev.fitmart.FItMart.exception.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    // ✅ Lấy tất cả đơn hàng
    @GetMapping
    public ResponseEntity<ApiResponse<Paginated<List<OrderResponse>>>> getAllOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "25") int limit,
            @RequestParam(required = false) String filterField,
            @RequestParam(required = false) String filterValue,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "-1") int created_at,
            @RequestParam Map<String, String> allParams
    ) {
        int createdAtSort = (created_at == -1 || created_at == 1 || created_at == 0) ? created_at : -1;

        // Create filter map from dynamic query parameters
        Map<String, String> filters = new HashMap<>();

        // Add filterField and filterValue if provided (for backwards compatibility)
        if (filterField != null && filterValue != null) {
            filters.put(filterField, filterValue);
        }

        // Add other query parameters as filters, excluding reserved ones
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            if (!key.equals("page") && !key.equals("limit") && !key.equals("q") &&
                    !key.equals("created_at") && !key.equals("filterField") && !key.equals("filterValue")) {
                filters.put(key, entry.getValue());
            }
        }

        Paginated<List<OrderResponse>> response = orderService.getAllOrders(page, limit, filters, q, createdAtSort);
        return ResponseUtils.success(response);
    }

    // ✅ Lấy chi tiết đơn hàng theo UUID
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable String uuid) {
        return ResponseUtils.success(orderService.getOrder(uuid));
    }

    // ✅ Cập nhật trạng thái đơn hàng
    @PutMapping("/{uuid}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable String uuid,
            @RequestParam int status
    ) {
        return ResponseUtils.success(orderService.updateOrderStatus(uuid, status));
    }
}
