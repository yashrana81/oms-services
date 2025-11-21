package com.oms.order.mapper;

import com.oms.order.dto.OrderResponse;
import com.oms.order.entity.Order;
import com.oms.order.enums.OrderStatusEnum;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        String statusName = OrderStatusEnum.fromId(order.getStatusId()).getName();
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .status(statusName.toUpperCase())
                .build();
    }

    public List<OrderResponse> toResponseList(List<Order> orders) {
        return orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

}