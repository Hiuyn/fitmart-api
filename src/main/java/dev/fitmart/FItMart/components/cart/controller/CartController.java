package dev.fitmart.FItMart.components.cart.controller;

import dev.fitmart.FItMart.components.cart.mapping.CartItemRequest;
import dev.fitmart.FItMart.components.cart.mapping.CartItemResponse;
import dev.fitmart.FItMart.components.cart.mapping.CartRequest;
import dev.fitmart.FItMart.components.cart.mapping.CartResponse;
import dev.fitmart.FItMart.components.cart.service.CartService;
import dev.fitmart.FItMart.components.order.mapping.OrderResponse;
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
    public ResponseEntity<CartResponse> createCart() {
        return ResponseEntity.ok(cartService.createCart());
    }

    @PostMapping("/{cart_id}/line-items")
    public ResponseEntity<CartResponse> addLineItem(@PathVariable("cart_id") String cartId,
                                                    @Valid @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.addLineItem(cartId, request));
    }

    @PatchMapping("/{cart_id}/items/{item_id}")
    public ResponseEntity<CartItemResponse> updateCartItem(@PathVariable("cart_id") String cartId,
                                                           @PathVariable("item_id") String itemId,
                                                           @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.updateCartItem(cartId, itemId, request));
    }

    @DeleteMapping("/{cart_id}/items/{item_id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable("cart_id") String cartId,
                                               @PathVariable("item_id") String itemId) {
        cartService.deleteCartItem(cartId, itemId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cart_id}/payment-methods")
    public ResponseEntity<CartResponse> selectPaymentMethod(@PathVariable("cart_id") String cartId,
                                                            @Valid @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.selectPaymentMethod(cartId, request));
    }

    @PostMapping("/{cart_id}/complete")
    public ResponseEntity<OrderResponse> completeCart(@PathVariable("cart_id") String cartId) {
        return ResponseEntity.ok(cartService.completeCart(cartId));
    }
}
