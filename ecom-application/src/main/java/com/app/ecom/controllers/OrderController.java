package com.app.ecom.controllers;

import com.app.ecom.dto.OrderResponse;
import com.app.ecom.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestHeader("X-User-ID") String userId) {
        OrderResponse order = orderService.createOrder(userId);
        if (order == null) {
            return new ResponseEntity<>("Cart is Empty or Cart not found or User not found.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(@RequestHeader("X-User-ID") String userId) {
        List<OrderResponse> order = orderService.fetchUserOrders(userId);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<OrderResponse> cancelOrder(@RequestHeader("X-Order-ID") String orderId) {
        OrderResponse order = orderService.cancelOrder(orderId);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}
