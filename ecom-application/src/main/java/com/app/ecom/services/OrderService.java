package com.app.ecom.services;

import com.app.ecom.dto.CartItemResponse;
import com.app.ecom.dto.OrderItemDTO;
import com.app.ecom.dto.OrderResponse;
import com.app.ecom.models.*;
import com.app.ecom.repository.OrderRepository;
import com.app.ecom.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    private OrderResponse mapToOrderResponse(Order newOrder) {
        OrderResponse response = new OrderResponse();
        response.setId(newOrder.getId());
        response.setStatus(newOrder.getStatus());
        response.setTotalAmount(newOrder.getTotalAmount());
        response.setItems(newOrder.getItems().stream().map(orderItem -> new OrderItemDTO(
                orderItem.getId(),
                orderItem.getProduct().getId(),
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getPrice()
        )).toList());
        response.setCreatedAt(newOrder.getCreatedAt());

        return response;
    }

    public OrderResponse createOrder(String userId) {
        List<CartItemResponse> cartItems = cartService.fetchUserCart(userId);
        if (cartItems.isEmpty()) {
            return null;
        }

        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if (userOpt.isEmpty()) {
            return null;
        }
        User user = userOpt.get();

        BigDecimal totalPrice = cartItems.stream()
                .map(CartItemResponse::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);

        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> new OrderItem(
                        null,                          // id
                        item.getProduct(),             // product
                        item.getQuantity(),            // quantity
                        item.getPrice(),                // price (subtotal, already qty * unitPrice)
                        item.getProduct().getPrice(),   // unitPrice
                        order                            // order
                ))
                .toList();

        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(userId);

        return mapToOrderResponse(savedOrder);
    }

    public OrderResponse cancelOrder(String orderId) {
        Order order = orderRepository.findById(Long.valueOf(orderId))
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        order.setStatus(OrderStatus.CANCELLED);

        return mapToOrderResponse(order);
    }

    public List<OrderResponse> fetchUserOrders(String userId) {
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if (userOpt.isEmpty()) {
            return null;
        }
        User user = userOpt.get();

        List<Order> userOrders = orderRepository.findByUser(user);

        return userOrders.stream().map(this::mapToOrderResponse).toList();
    }
}
