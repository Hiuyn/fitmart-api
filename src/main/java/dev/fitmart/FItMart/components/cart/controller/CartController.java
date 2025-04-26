package dev.fitmart.FItMart.components.cart.controller;

import dev.fitmart.FItMart.components.cart.mapping.CartRequest;
import dev.fitmart.FItMart.components.cart.mapping.CartResponse;
import dev.fitmart.FItMart.components.cart.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartController {
    @Autowired
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponse> createCart(@RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.createCart(request));
    }

    @GetMapping
    public ResponseEntity<List<CartResponse>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartResponse> getCartById(@PathVariable String id) {
        return ResponseEntity.ok(cartService.getCartById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CartResponse> getCartByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCartById(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable String id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<CartResponse> completeCart(@PathVariable String id) {
        return ResponseEntity.ok(cartService.completeCart(id));
    }
}
