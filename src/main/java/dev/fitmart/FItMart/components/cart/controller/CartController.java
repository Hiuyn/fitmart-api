package dev.fitmart.FItMart.components.cart.controller;

import dev.fitmart.FItMart.components.cart.mapping.CartItemRequest;
import dev.fitmart.FItMart.components.cart.mapping.CartItemResponse;
import dev.fitmart.FItMart.components.cart.mapping.CartRequest;
import dev.fitmart.FItMart.components.cart.mapping.CartResponse;
import dev.fitmart.FItMart.components.cart.service.CartService;
import dev.fitmart.FItMart.components.order.mapping.OrderResponse;
import dev.fitmart.FItMart.exception.ApiResponse;
import dev.fitmart.FItMart.exception.ResponseUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/store/carts")
public class CartController {
    @Autowired
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<ApiResponse<CartResponse>> createCart() {
        return ResponseUtils.success(cartService.createCart());
    }

    @PostMapping("/{cart_id}/add-items")
    public ResponseEntity<ApiResponse<CartResponse>> addLineItem(@PathVariable("cart_id") String cartId,
                                                    @Valid @RequestBody CartRequest request) {
        return ResponseUtils.success(cartService.addLineItem(cartId, request));
    }

    @PutMapping("/{cart_id}/items/{item_id}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateCartItem(@PathVariable("cart_id") String cartId,
                                                           @PathVariable("item_id") String itemId,
                                                           @Valid @RequestBody CartItemRequest request) {
        return ResponseUtils.success(cartService.updateCartItem(cartId, itemId, request));
    }

    @DeleteMapping("/{cart_id}/items/{item_id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable("cart_id") String cartId,
                                               @PathVariable("item_id") String itemId) {
        cartService.deleteCartItem(cartId, itemId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cart_id}/payment-methods")
    public ResponseEntity<ApiResponse<CartResponse>> selectPaymentMethod(@PathVariable("cart_id") String cartId,
                                                            @Valid @RequestBody CartRequest request) {
        return ResponseUtils.success(cartService.selectPaymentMethod(cartId, request));
    }

    @PostMapping("/{cart_id}/complete")
    public ResponseEntity<ApiResponse<OrderResponse>> completeCart(@PathVariable("cart_id") String cartId) {
        return ResponseUtils.success(cartService.completeCart(cartId));
    }
}
