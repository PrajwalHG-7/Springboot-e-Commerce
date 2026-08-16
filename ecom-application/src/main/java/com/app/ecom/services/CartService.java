package com.app.ecom.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.stream.Collectors;

import com.app.ecom.dto.CartItemResponse;
import com.app.ecom.models.User;
import com.app.ecom.models.Product;
import com.app.ecom.models.CartItem;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.app.ecom.dto.CartItemRequest;
import org.springframework.stereotype.Service;
import com.app.ecom.repository.UserRepository;
import com.app.ecom.repository.ProductRepository;
import com.app.ecom.repository.CartItemRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    private CartItemResponse mapToCartResponse(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setProduct(cartItem.getProduct());
        response.setPrice(cartItem.getPrice());
        response.setQuantity(cartItem.getQuantity());

        return response;
    }

    public CartItemResponse addToCart(String userId, CartItemRequest request) {
        Optional<Product> productOpt= productRepository.findById(request.getProduct_id());
        if(productOpt.isEmpty())
            return null;

        Product product = productOpt.get();
        if(product.getStockQuantity() < request.getQuantity())
            return null;

        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if(userOpt.isEmpty())
            return null;

        User user = userOpt.get();

        CartItem existingCartItem = cartItemRepository.findByUserAndProduct(user, product);

        if(existingCartItem != null) {
            if(product.getStockQuantity() < existingCartItem.getQuantity() + request.getQuantity())
                return null;
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            existingCartItem.setUnitPrice(product.getPrice());
            existingCartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
            cartItemRepository.save(existingCartItem);
            return mapToCartResponse(existingCartItem);
        }
        else {
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setUnitPrice(product.getPrice());
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            cartItemRepository.save(cartItem);
            return mapToCartResponse(cartItem);
        }
    }

    public CartItemResponse deleteItemFromCart(String userId, Long productId) {
        Optional<Product> productOpt= productRepository.findById(productId);
        if(productOpt.isEmpty())
            return null;

        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if(userOpt.isEmpty())
            return null;

        return userOpt.flatMap(user -> productOpt.map(
                product ->
                {
                    CartItem cartItem = cartItemRepository.deleteByUserAndProduct(user, product);
                    return mapToCartResponse(cartItem);
                })
        ).orElse(null);
    }

    public CartItemResponse deleteOneItemFromCart(String userId, Long productId) {
        Optional<Product> productOpt= productRepository.findById(productId);
        if(productOpt.isEmpty())
            return null;

        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if(userOpt.isEmpty())
            return null;

        return userOpt.flatMap(user -> productOpt.map(
                product ->
                {
                    CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product);
                    if(cartItem.getQuantity() <= 1)
                        deleteOneItemFromCart(userId, productId);
                    else {
                        cartItem.setQuantity(cartItem.getQuantity() - 1);
                        cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                        cartItemRepository.save(cartItem);
                    }
                    return mapToCartResponse(cartItem);
                })
        ).orElse(null);
    }

    public List<CartItemResponse> fetchUserCart(String userId) {
        List<CartItem> cart = cartItemRepository.findByUserId(Long.valueOf(userId));
        return cart.stream()
                .map(this::mapToCartResponse)
                .collect(Collectors.toList());
    }

    public void clearCart(String userId) {
        userRepository.findById(Long.valueOf(userId)).ifPresent(cartItemRepository::deleteByUser);
    }
}
