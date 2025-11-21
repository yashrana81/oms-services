package com.oms.order.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvent {

    private String eventType;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private Long userId;
}