package dev.fitmart.FItMart.components.cart.service;

import dev.fitmart.FItMart.components.cart.mapping.CartItemRequest;
import dev.fitmart.FItMart.components.cart.mapping.CartItemResponse;
import dev.fitmart.FItMart.components.cart.model.CartItem;
import dev.fitmart.FItMart.components.cart.repository.CartItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CartItemService {
    @Autowired
    private final CartItemRepository cartItemRepository;

    public CartItemResponse addItemToCart(CartItemRequest request) {
        // Check if item already exists in cart
        var existingItem = cartItemRepository.findByCartIdAndProductIdAndVariantIdAndDeletedAtIsNull(
                request.getCartId(), request.getProductId(), request.getVariantId());

        CartItem cartItem;
        if (existingItem.isPresent()) {
            // Update quantity if item exists
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            // Create new cart item
            cartItem = new CartItem();
            cartItem.setCartId(request.getCartId());
            cartItem.setProductId(request.getProductId());
            cartItem.setVariantId(request.getVariantId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setCreatedAt(LocalDateTime.now());
        }

        cartItem.setUpdatedAt(LocalDateTime.now());
        CartItem savedItem = cartItemRepository.save(cartItem);
        return convertToResponse(savedItem);
    }

    public List<CartItemResponse> getItemsByCartId(String cartId) {
        return cartItemRepository.findByCartIdAndDeletedAtIsNull(cartId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public CartItemResponse updateCartItemQuantity(String itemId, int quantity) {
        CartItem cartItem = cartItemRepository.findByUuidAndDeletedAtIsNull(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cartItem.setQuantity(quantity);
        cartItem.setUpdatedAt(LocalDateTime.now());

        CartItem updatedItem = cartItemRepository.save(cartItem);
        return convertToResponse(updatedItem);
    }

    public void removeItemFromCart(String itemId) {
        CartItem cartItem = cartItemRepository.findByUuidAndDeletedAtIsNull(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cartItem.setDeletedAt(LocalDateTime.now());
        cartItemRepository.save(cartItem);
    }

    public void deleteAllItemsInCart(String cartId) {
        List<CartItem> items = cartItemRepository.findByCartIdAndDeletedAtIsNull(cartId);
        items.forEach(item -> {
            item.setDeletedAt(LocalDateTime.now());
            cartItemRepository.save(item);
        });
    }

    private CartItemResponse convertToResponse(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setId(cartItem.getUuid());
        response.setCartId(cartItem.getCartId());
        response.setProductId(cartItem.getProductId());
        response.setVariantId(cartItem.getVariantId());
        response.setQuantity(cartItem.getQuantity());
        response.setCreatedAt(cartItem.getCreatedAt());
        response.setUpdatedAt(cartItem.getUpdatedAt());
        return response;
    }
}
