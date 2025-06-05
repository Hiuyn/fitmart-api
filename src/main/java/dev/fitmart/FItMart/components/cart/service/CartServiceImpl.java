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
import dev.fitmart.FItMart.components.order.service.OrderService;
import dev.fitmart.FItMart.components.product.mapping.ProductVariantResponse;
import dev.fitmart.FItMart.components.product.model.ProductVariant;
import dev.fitmart.FItMart.components.product.service.ProductVariantService;
import dev.fitmart.FItMart.exception.ApiException;
import dev.fitmart.FItMart.libs.UuidGenerator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService{
    private final CartRepository cartRepository;
    private final AuthenticationFacade authenticationFacade;
    private final AccountService accountService;
    private final OrderService orderService;
    private final ProductVariantService productVariantService;

    @Override
    public CartResponse createCart() {
        String accountId = accountService.findByAccountId();

        // Check if there's an existing draft cart
        Optional<Cart> existingCart = cartRepository.findFirstByAccountId(accountId);
        if (existingCart.isPresent()) {
            return convertToResponse(existingCart.get());
        }

        Cart cart = Cart.builder()
                .uuid(UuidGenerator.generateCustomUuid())
                .accountId(accountId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(null)
                .paymentMethod("")
                .items(new java.util.ArrayList<>())
                .build();

        cart = cartRepository.save(cart);
        return convertToResponse(cart);
    }

    @Override
    public CartResponse addLineItem(String cartId, CartRequest request) {
        Cart cart = validateCart(cartId);

        if (request.getVariant_id() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "variant_id is required");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "quantity must be greater than 0");
        }

        ProductVariantResponse product = productVariantService.getOneVariant(request.getVariant_id());

        Optional<Cart.CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getVariant_id().equals(request.getVariant_id()))
                .findFirst();

        if (existingItem.isPresent()) {
            Cart.CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            item.setPrice(item.getQuantity() * product.getPrice());
        } else {
            Cart.CartItem item = Cart.CartItem.builder()
                    .uuid(UuidGenerator.generateCustomUuid())
                    .variant_id(request.getVariant_id())
                    .variant(product)
                    .quantity(request.getQuantity())
                    .price(request.getQuantity() * product.getPrice())
                    .is_check(true)
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
        ProductVariantResponse product = productVariantService.getOneVariant(item.getVariant_id());
        if (request.getQuantity() != null) {
            if (request.getQuantity() <= 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "quantity must be greater than 0");
            }
            item.setQuantity(request.getQuantity());
            item.setIs_check(request.getIs_check());
            item.setPrice(request.getQuantity() * product.getPrice());
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
        if (request.getPayment_method() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Payment method is required");
        }

        cart.setPaymentMethod(request.getPayment_method());
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
        List<Cart.CartItem> selectedItems = cart.getItems().stream()
                .filter(item -> item.getIs_check() == true)
                .collect(Collectors.toList());

        if (selectedItems.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Không có món hàng nào trong giỏ hàng");
        }

        OrderResponse order = orderService.createOrder(cart, selectedItems);

        cart.getItems().removeAll(selectedItems);
        cart.setPaymentMethod("");
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return order;
    }

    private CartResponse convertToResponse(Cart cart) {
        return CartResponse.builder()
                .uuid(cart.getUuid())
                .account_id(cart.getAccountId())
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
                .variant_id(item.getVariant_id())
                .quantity(item.getQuantity())
                .build();
    }

    private Cart validateCart(String cartId) {
        String accountId = accountService.findByAccountId();
        Cart existingCart = cartRepository.findFirstByAccountId(accountId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cart not found: " + cartId));
        if (!existingCart.getAccountId().equals(accountId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only access your own cart");
        }
        return existingCart;
    }
}
