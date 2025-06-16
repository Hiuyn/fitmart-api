package dev.fitmart.FItMart.components.cart.service;


import dev.fitmart.FItMart.auth.AuthenticationFacade;
import dev.fitmart.FItMart.components.cart.mapping.CartItemRequest;
import dev.fitmart.FItMart.components.cart.mapping.CartItemResponse;
import dev.fitmart.FItMart.components.cart.mapping.CartRequest;
import dev.fitmart.FItMart.components.cart.mapping.CartResponse;
import dev.fitmart.FItMart.components.cart.model.Cart;
import dev.fitmart.FItMart.components.cart.repository.CartRepository;
import dev.fitmart.FItMart.components.order.mapping.OrderResponse;
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

public interface CartService {
    CartResponse createCart();
    CartResponse getCartById(String cartId);
    CartResponse addLineItem(String cartId, CartRequest request);
    CartItemResponse updateCartItem(String cartId, String itemId, CartItemRequest request);
    void deleteCartItem(String cartId, String itemId);
    CartResponse selectPaymentMethod(String cartId, CartRequest request);
    OrderResponse completeCart(String cartId);
}
