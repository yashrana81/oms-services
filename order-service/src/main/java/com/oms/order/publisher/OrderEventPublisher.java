package com.oms.order.publisher;

import com.oms.order.event.OrderEvent;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final SqsTemplate sqsTemplate;

    @Value("${aws.sqs.queue.name:}")
    private String queueName;

    public void publishCancelOrderEvent(OrderEvent event) {
        try {
            sqsTemplate.send(queueName, event);
            log.info("Published CANCEL_ORDER event to SQS for orderId: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("SQS publish failed for orderId: {} - {}", event.getOrderId(), e.getMessage(), e);
        }
    }
}