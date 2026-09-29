package com.ecommerce.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecommerce.event.PaymentProcessedEvent;

@Service
public class PaymentKafkaProducer {

    private static final String TOPIC = "payment-processed";

    private final KafkaTemplate<String, PaymentProcessedEvent> kafkaTemplate;

    public PaymentKafkaProducer(
            KafkaTemplate<String, PaymentProcessedEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentProcessedEvent(PaymentProcessedEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.getOrderId().toString(),
                event
        );

        System.out.println("================================");
        System.out.println("PAYMENT_PROCESSED event published");
        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("Payment ID: " + event.getPaymentId());
        System.out.println("Payment Status: " + event.getPaymentStatus());
        System.out.println("================================");
    }
}