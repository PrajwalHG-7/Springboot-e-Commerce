package com.app.ecom.controllers;

import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.dto.CartItemResponse;
import com.app.ecom.services.CartService;
import lombok.RequiredArgsConstructor;
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
        return new ResponseEntity<>(cartService.addToCart(userId, request), HttpStatus.CREATED);
    }
    
}
