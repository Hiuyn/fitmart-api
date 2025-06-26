package dev.fitmart.FItMart.components.cart.controller;

import dev.fitmart.FItMart.components.cart.mapping.CartItemRequest;
import dev.fitmart.FItMart.components.cart.mapping.CartItemResponse;
import dev.fitmart.FItMart.components.cart.mapping.CartRequest;
import dev.fitmart.FItMart.components.cart.mapping.CartResponse;
import dev.fitmart.FItMart.components.cart.service.CartService;
import dev.fitmart.FItMart.components.order.mapping.OrderResponse;
import dev.fitmart.FItMart.exception.BaseResponse;
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

    @PostMapping()
    public ResponseEntity<BaseResponse<CartResponse>> createCart() {
        return ResponseUtils.success(cartService.createCart());
    }

    @GetMapping("/{cart_id}")
    public ResponseEntity<BaseResponse<CartResponse>> getCart(@PathVariable("cart_id") String cartId) {
        return ResponseUtils.success(cartService.getCartById(cartId));
    }

    @PostMapping("/{cart_id}/add-items")
    public ResponseEntity<BaseResponse<CartResponse>> addLineItem(@PathVariable("cart_id") String cartId,
                                                    @Valid @RequestBody CartRequest request) {
        return ResponseUtils.success(cartService.addLineItem(cartId, request));
    }

    @PutMapping("/{cart_id}/items/{item_id}")
    public ResponseEntity<BaseResponse<CartItemResponse>> updateCartItem(@PathVariable("cart_id") String cartId,
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
    public ResponseEntity<BaseResponse<CartResponse>> selectPaymentMethod(@PathVariable("cart_id") String cartId,
                                                            @Valid @RequestBody CartRequest request) {
        return ResponseUtils.success(cartService.selectPaymentMethod(cartId, request));
    }

    @PostMapping("/{cart_id}/complete")
    public ResponseEntity<BaseResponse<OrderResponse>> completeCart(@PathVariable("cart_id") String cartId) {
        return ResponseUtils.success(cartService.completeCart(cartId));
    }
}
