package com.oms.order.publisher;

import com.oms.order.event.OrderEvent;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderEventPublisher {

    @Autowired(required = false)
    private SqsTemplate sqsTemplate;

    @Value("${aws.sqs.queue.name:}")
    private String queueName;

    public void publishCancelOrderEvent(OrderEvent event) {
        if (sqsTemplate == null) {
            log.info("LOCAL MODE: Skipping SQS publish for CANCEL_ORDER event for orderId: {}", event.getOrderId());
            return;
        }
        
        try {
            sqsTemplate.send(queueName, event);
            log.info("Published CANCEL_ORDER event to SQS for orderId: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to publish CANCEL_ORDER event for orderId: {}", event.getOrderId(), e);
        }
    }

}