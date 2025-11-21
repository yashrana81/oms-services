package com.oms.inventory.listener;

import com.oms.order.event.OrderEvent;
import com.oms.inventory.service.ProductService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!local")
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final ProductService productService;

    @SqsListener("${SQS_QUEUE_NAME:order-events-queue}")
    public void handleOrderEvent(OrderEvent event) {
        log.info("Received event: {}", event);

        try {
            if ("CANCEL_ORDER".equals(event.getEventType())) {
                productService.restoreStock(event.getProductId(), event.getQuantity());
                log.info("Processed CANCEL_ORDER event for orderId: {}", event.getOrderId());
            } else {
                log.warn("Unknown event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error processing event: {}", event, e);
            throw e;
        }
    }
}