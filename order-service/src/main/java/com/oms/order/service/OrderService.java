package com.oms.order.service;

import com.oms.order.client.InventoryClient;
import com.oms.order.dto.OrderRequest;
import com.oms.order.entity.Order;
import com.oms.order.enums.OrderStatusEnum;
import com.oms.order.event.OrderEvent;
import com.oms.order.exception.OrderServiceException;
import com.oms.order.publisher.OrderEventPublisher;
import com.oms.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final InventoryClient inventoryClient;
    private final UserService userService;

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public Order placeOrder(String token, OrderRequest request) {
        Long userId = userService.validateToken(token);
        if (userId == null) {
            throw new OrderServiceException("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        if (request.getProductId() == null) {
            throw new OrderServiceException("Product ID is required", HttpStatus.BAD_REQUEST);
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new OrderServiceException("Quantity must be greater than 0", HttpStatus.BAD_REQUEST);
        }

        try {
            inventoryClient.reserveStock(request.getProductId(), request.getQuantity());
        } catch (Exception ex) {
            log.error("[ORDER SERVICE] Stock reservation failed for product {}", request.getProductId(), ex);
            throw new OrderServiceException("Unable to reserve stock. Product may be out of stock or unavailable", HttpStatus.BAD_REQUEST);
        }

        Order order = Order.builder()
                .userId(userId)
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .statusId(OrderStatusEnum.ORDERED.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        try {
            return orderRepository.save(order);
        } catch (Exception ex) {
            log.error("Database error while placing order", ex);
            throw new OrderServiceException("Unable to place order. Please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Cancels order and triggers async stock restoration via SQS
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order;
        try {
            order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new OrderServiceException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND));
        } catch (OrderServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("DB error fetching order {}", orderId, ex);
            throw new OrderServiceException("Unable to fetch order. Please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        OrderStatusEnum currentStatus = OrderStatusEnum.fromId(order.getStatusId());

        if (currentStatus == OrderStatusEnum.CANCELLED) {
            throw new OrderServiceException("Order is already cancelled", HttpStatus.BAD_REQUEST);
        }

        order.setStatusId(OrderStatusEnum.CANCELLED.getId());
        order.setUpdatedAt(LocalDateTime.now());

        try {
            Order updatedOrder = orderRepository.save(order);
            log.info("Order cancelled successfully. OrderId: {}", orderId);

            OrderEvent event = OrderEvent.builder()
                    .eventType("CANCEL_ORDER")
                    .orderId(orderId)
                    .productId(order.getProductId())
                    .quantity(order.getQuantity())
                    .userId(order.getUserId())
                    .build();
            orderEventPublisher.publishCancelOrderEvent(event);

            return updatedOrder;
        } catch (Exception ex) {
            log.error("DB error cancelling order {}", orderId, ex);
            throw new OrderServiceException("Unable to cancel order. Please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Updates order status with validation for allowed transitions
    @Transactional
    public Order updateOrderStatus(Long orderId, String status) {
        Order order;
        try {
            order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new OrderServiceException("Order not found with id: " + orderId, HttpStatus.NOT_FOUND));
        } catch (OrderServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("DB error fetching order {}", orderId, ex);
            throw new OrderServiceException("Unable to fetch order. Please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        OrderStatusEnum newStatus = OrderStatusEnum.fromName(status);
        if (newStatus == null) {
            throw new OrderServiceException("Invalid status: " + status, HttpStatus.BAD_REQUEST);
        }

        OrderStatusEnum currentStatus = OrderStatusEnum.fromId(order.getStatusId());

        if (currentStatus == newStatus) {
            throw new OrderServiceException("Order is already in " + currentStatus.getName() + " status", HttpStatus.BAD_REQUEST);
        }

        validateStatusTransition(currentStatus, newStatus);

        order.setStatusId(newStatus.getId());
        order.setUpdatedAt(LocalDateTime.now());

        try {
            return orderRepository.save(order);
        } catch (Exception ex) {
            log.error("DB error updating order status for order {}", orderId, ex);
            throw new OrderServiceException("Unable to update order status. Please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Validates business rules for order status transitions
    private void validateStatusTransition(OrderStatusEnum currentStatus, OrderStatusEnum newStatus) {
        if (currentStatus == OrderStatusEnum.CANCELLED) {
            throw new OrderServiceException("Cannot update status of a cancelled order", HttpStatus.BAD_REQUEST);
        }

        if (currentStatus == OrderStatusEnum.DELIVERED) {
            throw new OrderServiceException("Cannot update status of a delivered order", HttpStatus.BAD_REQUEST);
        }

        if (currentStatus == OrderStatusEnum.ORDERED && newStatus == OrderStatusEnum.DELIVERED) {
            throw new OrderServiceException("Cannot mark order as delivered without shipping first", HttpStatus.BAD_REQUEST);
        }

        if (currentStatus == OrderStatusEnum.SHIPPED && newStatus == OrderStatusEnum.ORDERED) {
            throw new OrderServiceException("Cannot revert shipped order back to ordered status", HttpStatus.BAD_REQUEST);
        }

        if (currentStatus == OrderStatusEnum.SHIPPED && newStatus == OrderStatusEnum.CANCELLED) {
            throw new OrderServiceException("Cannot cancel order that has already been shipped", HttpStatus.BAD_REQUEST);
        }

        if (currentStatus == OrderStatusEnum.DELIVERED && newStatus == OrderStatusEnum.CANCELLED) {
            throw new OrderServiceException("Cannot cancel order that has already been delivered", HttpStatus.BAD_REQUEST);
        }
    }
}