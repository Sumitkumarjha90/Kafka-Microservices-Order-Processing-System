package com.ecommerce.kafka;

import com.ecommerce.event.DeliveryCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DeliveryKafkaProducer {

    private static final String TOPIC = "delivery-created";

    private final KafkaTemplate<String, DeliveryCreatedEvent> kafkaTemplate;

    public DeliveryKafkaProducer(KafkaTemplate<String, DeliveryCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(DeliveryCreatedEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.getOrderId().toString(),
                event
        );

        System.out.println("========================================");
        System.out.println("DELIVERY_CREATED event published");
        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("Tracking Number: " + event.getTrackingNumber());
        System.out.println("Status: " + event.getDeliveryStatus());
        System.out.println("========================================");
    }
}
