package com.oms.order.publisher;

import com.oms.order.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("local")
@Primary
@Slf4j
public class NoOpEventPublisher extends OrderEventPublisher {

    @Override
    public void publishCancelOrderEvent(OrderEvent event) {
        log.info("LOCAL MODE: Skipping SQS publish for CANCEL_ORDER event for orderId: {}", event.getOrderId());
    }
}

