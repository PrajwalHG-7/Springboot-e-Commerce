package com.app.ecom.services;

import com.app.ecom.dto.CartItemResponse;
import com.app.ecom.dto.OrderItemDTO;
import com.app.ecom.dto.OrderResponse;
import com.app.ecom.models.*;
import com.app.ecom.repository.OrderRepository;
import com.app.ecom.repository.UserRepository;
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
                orderItem.getPrice(),
                orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()))
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

        BigDecimal totalPrice = cartItems.stream().map(CartItemResponse::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();

        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);
        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> new OrderItem(
                        null,
                        item.getProduct(),
                        item.getQuantity(),
                        item.getPrice(),
                        order
                ))
                .toList();
        order.setItems(orderItems.stream().map(orderItem -> new OrderItem(
                orderItem.getId(),
                orderItem.getProduct(),
                orderItem.getQuantity(),
                orderItem.getPrice(),
                orderItem.getOrder()
        )).toList());
        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(userId);

        return mapToOrderResponse(savedOrder);
    }
}
