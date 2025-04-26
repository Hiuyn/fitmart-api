package dev.fitmart.FItMart.components.cart.service;


import dev.fitmart.FItMart.auth.AuthenticationFacade;
import dev.fitmart.FItMart.components.cart.mapping.CartRequest;
import dev.fitmart.FItMart.components.cart.mapping.CartResponse;
import dev.fitmart.FItMart.components.cart.model.Cart;
import dev.fitmart.FItMart.components.cart.repository.CartRepository;
import dev.fitmart.FItMart.components.user.UserModel;
import dev.fitmart.FItMart.components.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemService cartItemService;
    private final AuthenticationFacade authenticationFacade;
    private final UserRepository userRepository;

    public CartResponse createCart(CartRequest request) {
        Cart cart = new Cart();
        cart.setUserId(request.getUserId());
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());

        Cart savedCart = cartRepository.save(cart);
        return convertToResponse(savedCart);
    }

    public List<CartResponse> getAllCarts() {
        return cartRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public CartResponse getCartById(String id) {
        Cart cart = cartRepository.findByUuidAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return convertToResponse(cart);
    }

    public String findByUserId() {
        String loggedInUserEmail = authenticationFacade.getAuthentication().getName();
        UserModel loggedInUser =  userRepository.findByEmail(loggedInUserEmail).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return loggedInUser.getUuid();
    }

    public void deleteCart(String id) {
        Cart cart = cartRepository.findByUuidAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.setDeletedAt(LocalDateTime.now());
        cartRepository.save(cart);

        // Soft delete all cart items
        cartItemService.deleteAllItemsInCart(id);
    }

    public CartResponse completeCart(String id) {
        Cart cart = cartRepository.findByUuidAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.setCompletedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());

        Cart updatedCart = cartRepository.save(cart);
        return convertToResponse(updatedCart);
    }

    private CartResponse convertToResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getUuid());
        response.setUserId(cart.getUserId());
        response.setCompletedAt(cart.getCompletedAt());
        response.setCreatedAt(cart.getCreatedAt());
        response.setUpdatedAt(cart.getUpdatedAt());

        // Get cart items
        var items = cartItemService.getItemsByCartId(cart.getUuid());
        response.setCartItems(items);

        return response;
    }
}
