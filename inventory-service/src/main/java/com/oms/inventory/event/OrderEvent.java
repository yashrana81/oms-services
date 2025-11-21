package com.oms.inventory.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

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