package com.app.ecom.controllers;

import lombok.RequiredArgsConstructor;
import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.dto.CartItemResponse;
import com.app.ecom.services.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}