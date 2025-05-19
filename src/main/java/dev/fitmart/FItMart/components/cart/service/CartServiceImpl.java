package dev.fitmart.FItMart.components.cart.service;

import dev.fitmart.FItMart.auth.AuthenticationFacade;
import dev.fitmart.FItMart.components.account.service.AccountService;
import dev.fitmart.FItMart.components.cart.mapping.CartItemRequest;
import dev.fitmart.FItMart.components.cart.mapping.CartItemResponse;
import dev.fitmart.FItMart.components.cart.mapping.CartRequest;
import dev.fitmart.FItMart.components.cart.mapping.CartResponse;
import dev.fitmart.FItMart.components.cart.model.Cart;
import dev.fitmart.FItMart.components.cart.repository.CartRepository;
import dev.fitmart.FItMart.components.order.mapping.OrderResponse;
import dev.fitmart.FItMart.components.order.model.Order;
import dev.fitmart.FItMart.components.order.repository.OrderRepository;
import dev.fitmart.FItMart.exception.ApiException;
import dev.fitmart.FItMart.libs.UuidGenerator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService{
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthenticationFacade authenticationFacade;
    private final AccountService accountService;

    @Override
    public CartResponse createCart() {
        String accountId = accountService.findByAccountId();

        // Check if there's an existing draft cart
        Optional<Cart> existingCart = cartRepository.findByAccountId(accountId);
        if (existingCart.isPresent()) {
            return convertToResponse(existingCart.get());
        }

        Cart cart = Cart.builder()
                .uuid(UuidGenerator.generateCustomUuid())
                .accountId(accountId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(null)
                .completedAt(null)
                .paymentMethod("")
                .items(new java.util.ArrayList<>())
                .build();

        cart = cartRepository.save(cart);
        return convertToResponse(cart);
    }

    @Override
    public CartResponse addLineItem(String cartId, CartRequest request) {
        Cart cart = validateCart(cartId);

        if (request.getVariantId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "variant_id is required");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "quantity must be greater than 0");
        }

        Optional<Cart.CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getVariant_id().equals(request.getVariantId()))
                .findFirst();

        if (existingItem.isPresent()) {
            Cart.CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            Cart.CartItem item = Cart.CartItem.builder()
                    .uuid(UuidGenerator.generateCustomUuid())
                    .variant_id(request.getVariantId())
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(item);
        }


        cart.setUpdatedAt(LocalDateTime.now());
        cart = cartRepository.save(cart);
        return convertToResponse(cart);
    }

    @Override
    public CartItemResponse updateCartItem(String cartId, String itemId, CartItemRequest request) {
        Cart cart = validateCart(cartId);

        Cart.CartItem item = cart.getItems().stream()
                .filter(i -> i.getUuid().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cart item not found: " + itemId));

        if (request.getQuantity() != null) {
            if (request.getQuantity() <= 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "quantity must be greater than 0");
            }
            item.setQuantity(request.getQuantity());
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return convertToCartItemResponse(item);
    }

    @Override
    public void deleteCartItem(String cartId, String itemId) {
        Cart cart = validateCart(cartId);

        Cart.CartItem item = cart.getItems().stream()
                .filter(i -> i.getUuid().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cart item not found: " + itemId));

        cart.getItems().remove(item);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    public CartResponse selectPaymentMethod(String cartId, CartRequest request) {
        Cart cart = validateCart(cartId);
        if (request.getPaymentMethod() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Payment method is required");
        }

        cart.setPaymentMethod(request.getPaymentMethod());
        cart.setUpdatedAt(LocalDateTime.now());
        cart = cartRepository.save(cart);
        return convertToResponse(cart);
    }

    @Override
    public OrderResponse completeCart(String cartId) {
        Cart cart = validateCart(cartId);
        if (cart.getItems().isEmpty() || cart.getPaymentMethod() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cart is incomplete");
        }

        String accountId = cart.getAccountId();
        if (!accountService.findAccountByUuid(accountId).getUuid().equals(accountId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid account information");
        }

        Order order = Order.builder()
                .uuid(UuidGenerator.generateCustomUuid())
                .accountId(cart.getAccountId())
                .items(cart.getItems().stream().toList())
                .paymentMethod(cart.getPaymentMethod())
                .packagingStatus(0)
                .shippingStatus(0)
                .paymentStatus(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(null)
                .completedAt(null)
                .build();

        order = orderRepository.save(order);
        cart.setItems(new ArrayList<>());
        cart.setUpdatedAt(LocalDateTime.now());
        cart = cartRepository.save(cart);
        return convertToOrderResponse(order);
    }

    private CartResponse convertToResponse(Cart cart) {
        return CartResponse.builder()
                .uuid(cart.getUuid())
                .account_id(cart.getAccountId())
                .completed_at(cart.getCompletedAt())
                .created_at(cart.getCreatedAt())
                .updated_at(cart.getUpdatedAt())
                .deleted_at(cart.getDeletedAt())
                .items(cart.getItems().stream().toList())
                .payment_method(cart.getPaymentMethod())
                .build();
    }

    private CartItemResponse convertToCartItemResponse(Cart.CartItem item) {
        return CartItemResponse.builder()
                .id(item.getUuid())
                .variantId(item.getVariant_id())
                .quantity(item.getQuantity())
                .build();
    }

    private Cart validateCart(String cartId) {
        String accountId = accountService.findByAccountId();
        Cart existingCart = cartRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cart not found: " + cartId));
        if (!existingCart.getAccountId().equals(accountId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only access your own cart");
        }
        if (existingCart.getCompletedAt() != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cart is already completed");
        }
        return existingCart;
    }

    private OrderResponse convertToOrderResponse(Order order) {
        return OrderResponse.builder()
                .uuid(order.getUuid())
                .accountId(order.getAccountId())
                .items(order.getItems())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .packagingStatus(order.getPackagingStatus())
                .shippingStatus(order.getShippingStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
