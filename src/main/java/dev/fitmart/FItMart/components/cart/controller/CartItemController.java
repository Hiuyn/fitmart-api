package dev.fitmart.FItMart.components.cart.controller;

import dev.fitmart.FItMart.components.cart.mapping.CartItemRequest;
import dev.fitmart.FItMart.components.cart.mapping.CartItemResponse;
import dev.fitmart.FItMart.components.cart.service.CartItemService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/cart-items")
public class CartItemController {
    @Autowired
    private final CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<CartItemResponse> addItemToCart(@RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartItemService.addItemToCart(request));
    }

    @GetMapping("/cart/{cartId}")
    public ResponseEntity<List<CartItemResponse>> getCartItems(@PathVariable String cartId) {
        return ResponseEntity.ok(cartItemService.getItemsByCartId(cartId));
    }

    @PutMapping("/{itemId}/quantity")
    public ResponseEntity<CartItemResponse> updateItemQuantity(
            @PathVariable String itemId, @RequestParam int quantity) {
        return ResponseEntity.ok(cartItemService.updateCartItemQuantity(itemId, quantity));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable String itemId) {
        cartItemService.removeItemFromCart(itemId);
        return ResponseEntity.noContent().build();
    }
}
