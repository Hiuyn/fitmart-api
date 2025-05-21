package dev.fitmart.FItMart.components.order.service;

import dev.fitmart.FItMart.common.model.Filter;
import dev.fitmart.FItMart.common.model.Paginated;
import dev.fitmart.FItMart.common.service.FilterService;
import dev.fitmart.FItMart.components.cart.model.Cart;
import dev.fitmart.FItMart.components.order.mapping.OrderResponse;
import dev.fitmart.FItMart.components.order.model.Order;
import dev.fitmart.FItMart.components.order.repository.OrderRepository;
import dev.fitmart.FItMart.components.product.mapping.ProductResponse;
import dev.fitmart.FItMart.components.product.model.Product;
import dev.fitmart.FItMart.exception.ApiException;
import dev.fitmart.FItMart.libs.UuidGenerator;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService{
    @Autowired
    private final OrderRepository orderRepository;
    @Autowired
    private FilterService filterService;

    @Override
    public OrderResponse createOrder(Cart cart) {
        Order order = Order.builder()
                .uuid(UuidGenerator.generateCustomUuid())
                .accountId(cart.getAccountId())
                .paymentMethod(cart.getPaymentMethod())
                .totalFee(cart.getItems().stream()
                        .mapToLong(item -> item.getPrice())
                        .sum())
                .items(cart.getItems())
                .status(1)
                .statusTxt(Order.getStatusText(1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        order = orderRepository.save(order);
        return convertOrderToResponse(order);
    }

    @Override
    public Paginated<List<OrderResponse>> getAllOrders(int page, int limit, Map<String, String> filters, String q, int createdAtSort) {
        // Page is 0-based in Spring Data, but API uses 1-based
        Pageable pageable = limit == -1 ? PageRequest.of(0, Integer.MAX_VALUE) : PageRequest.of(page - 1, limit);

        List<Filter> filterCriteria = new ArrayList<>();
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                continue;
            }
            Filter criteria = new Filter();
            criteria.setField(entry.getKey());
            criteria.setValue(entry.getValue());
            filterCriteria.add(criteria);
        }

        Page<Order> OrderPage = filterService.applyFilter(Order.class, "orders", filterCriteria, pageable, q, limit, createdAtSort);

        List<OrderResponse> orderResponses = OrderPage.getContent().stream()
                .map(this::convertOrderToResponse)
                .collect(Collectors.toList());

        Paginated<List<OrderResponse>> response = new Paginated<>();
        response.setData(orderResponses);

        Paginated.Pagination pagination = new Paginated.Pagination();
        pagination.setTotal(OrderPage.getTotalElements());
        pagination.setCount(orderResponses.size());
        pagination.setPerPage(limit == -1 ? orderResponses.size() : limit);
        pagination.setCurrentPage(limit == -1 ? 1 : page);
        pagination.setTotalPages(limit == -1 ? 1 : OrderPage.getTotalPages());
        response.setPagination(pagination);

        return response;
    }

    @Override
    public OrderResponse getOrder(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "UUID cannot be null or empty");
        }
        Order order = orderRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Order with UUID " + uuid + " not found"));
        if (order.getDeletedAt() != null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Order not found with uuid: " + uuid);
        }
        return convertOrderToResponse(order);
    }

    @Override
    public OrderResponse updateOrderStatus(String uuid, int newStatus) {
        Order order = orderRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Order not found"));

        order.setStatus(newStatus);
        order.setStatusTxt(Order.getStatusText(newStatus));
        order.setUpdatedAt(LocalDateTime.now());

        if (newStatus == 5) {
            order.setCompletedAt(LocalDateTime.now());
        }

        return convertOrderToResponse(orderRepository.save(order));
    }

    private OrderResponse convertOrderToResponse(Order order) {
        return OrderResponse.builder()
                .uuid(order.getUuid())
                .account_id(order.getAccountId())
                .payment_method(order.getPaymentMethod())
                .total_fee(order.getTotalFee())
                .status(order.getStatus())
                .status_txt(order.getStatusTxt())
                .items(order.getItems())
                .created_at(order.getCreatedAt())
                .updated_at(order.getUpdatedAt())
                .completed_at(order.getCompletedAt())
                .deleted_at(order.getDeletedAt())
                .build();
    }
}
