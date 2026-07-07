package com.app.ecom.services;

import java.util.Optional;
import java.math.BigDecimal;

import com.app.ecom.dto.CartItemResponse;
import com.app.ecom.models.User;
import com.app.ecom.models.Product;
import com.app.ecom.models.CartItem;
import lombok.RequiredArgsConstructor;
import com.app.ecom.dto.CartItemRequest;
import org.springframework.stereotype.Service;
import com.app.ecom.repository.UserRepository;
import com.app.ecom.repository.ProductRepository;
import com.app.ecom.repository.CartItemRepository;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    private CartItemResponse mapToCartResponse(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setProduct_id(cartItem.getProduct().getId());
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
            existingCartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
            cartItemRepository.save(existingCartItem);
        }
        else {
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            cartItemRepository.save(cartItem);
        }

        return mapToCartResponse(existingCartItem);
    }
}
