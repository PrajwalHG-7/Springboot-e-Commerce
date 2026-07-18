package com.app.ecom.controllers;

import lombok.RequiredArgsConstructor;
import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.dto.CartItemResponse;
import com.app.ecom.services.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<?> addToCart(@RequestHeader("X-User-ID") String userId, @RequestBody CartItemRequest request) {
        CartItemResponse cartItemResponse = cartService.addToCart(userId, request);
        if(cartItemResponse == null)
            return ResponseEntity.badRequest().body("Product Out of Stock OR User or Product not found");

        return new ResponseEntity<>(cartItemResponse, HttpStatus.ACCEPTED);
    }

    @GetMapping
    public ResponseEntity<?> getUserCart(@RequestHeader("X-User-ID") String userId) {
        List<CartItemResponse> cart = cartService.fetchUserCart(userId);
        if(cart == null)
            return ResponseEntity.badRequest().body("Cart is empty OR User not found");

        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<?> removeFromCart(@RequestHeader("X-User-ID") String userId, @PathVariable Long productId) {
        CartItemResponse cartItemResponse = cartService.deleteItemFromCart(userId, productId);
        if(cartItemResponse == null)
            return ResponseEntity.badRequest().body("Product not found in the cart");

        return new ResponseEntity<>(cartItemResponse, HttpStatus.OK);
    }

    @DeleteMapping("/item/{productId}")
    public ResponseEntity<?> removeOneFromCart(@RequestHeader("X-User-ID") String userId, @PathVariable Long productId) {
        CartItemResponse cartItemResponse = cartService.deleteOneItemFromCart(userId, productId);
        if(cartItemResponse == null)
            return ResponseEntity.badRequest().body("Product not found in the cart");

        return new ResponseEntity<>(cartItemResponse, HttpStatus.OK);
    }
}