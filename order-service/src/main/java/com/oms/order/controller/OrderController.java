package com.oms.order.controller;

import com.oms.order.dto.OrderRequest;
import com.oms.order.dto.OrderResponse;
import com.oms.order.dto.UpdateStatusRequest;
import com.oms.order.entity.Order;
import com.oms.order.exception.OrderServiceException;
import com.oms.order.mapper.OrderMapper;
import com.oms.order.service.OrderService;
import com.oms.order.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final UserService userService;

    // Fetch order by ID with user authorization check
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        if (token == null) {
            throw new OrderServiceException("Missing Authorization header", HttpStatus.UNAUTHORIZED);
        }

        Long userId = userService.validateToken(token);
        if (userId == null) {
            throw new OrderServiceException("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new OrderServiceException("Order not found with id: " + id, HttpStatus.NOT_FOUND));
        
        if (!order.getUserId().equals(userId)) {
            throw new OrderServiceException("Unauthorized access. You can only view your own orders", HttpStatus.UNAUTHORIZED);
        }
        
        return ResponseEntity.ok(orderMapper.toResponse(order));
    }

    // Fetch all orders for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(
            @PathVariable Long userId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        if (token == null) {
            throw new OrderServiceException("Missing Authorization header", HttpStatus.UNAUTHORIZED);
        }

        Long tokenUserId = userService.validateToken(token);
        if (tokenUserId == null) {
            throw new OrderServiceException("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        
        if (!tokenUserId.equals(userId)) {
            throw new OrderServiceException("Unauthorized access. Invalid token or user mismatch", HttpStatus.UNAUTHORIZED);
        }

        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orderMapper.toResponseList(orders));
    }

    // Place a new order after validating stock availability
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request, @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null) {
            throw new OrderServiceException("Missing Authorization header", HttpStatus.UNAUTHORIZED);
        }

        Order order = orderService.placeOrder(token, request);
        return ResponseEntity.ok(orderMapper.toResponse(order));
    }

    // Cancel an order and trigger async stock restoration with user authorization check
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        if (token == null) {
            throw new OrderServiceException("Missing Authorization header", HttpStatus.UNAUTHORIZED);
        }

        Long userId = userService.validateToken(token);
        if (userId == null) {
            throw new OrderServiceException("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new OrderServiceException("Order not found with id: " + id, HttpStatus.NOT_FOUND));
        
        if (!order.getUserId().equals(userId)) {
            throw new OrderServiceException("Unauthorized access. You can only cancel your own orders", HttpStatus.UNAUTHORIZED);
        }

        Order cancelledOrder = orderService.cancelOrder(id);
        return ResponseEntity.ok(orderMapper.toResponse(cancelledOrder));
    }

    // Internal endpoint to update order status
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        Order order = orderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(orderMapper.toResponse(order));
    }
}