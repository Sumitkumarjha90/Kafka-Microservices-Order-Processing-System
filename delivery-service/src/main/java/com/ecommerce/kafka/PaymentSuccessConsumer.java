package com.ecommerce.kafka;

import com.ecommerce.event.PaymentSuccessEvent;
import com.ecommerce.service.DeliveryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentSuccessConsumer {

    private final ObjectMapper objectMapper;
    private final DeliveryService deliveryService;

    public PaymentSuccessConsumer(
            ObjectMapper objectMapper,
            DeliveryService deliveryService) {
        this.objectMapper = objectMapper;
        this.deliveryService = deliveryService;
    }

    @KafkaListener(
            topics = "payment-success",
            groupId = "delivery-service-group"
    )
    public void consume(String message) {

        try {
            PaymentSuccessEvent event =
                    objectMapper.readValue(message, PaymentSuccessEvent.class);

            System.out.println("========================================");
            System.out.println("PAYMENT_SUCCESS received");
            System.out.println(event);
            System.out.println("========================================");

            if (!"SUCCESS".equalsIgnoreCase(event.getPaymentStatus())) {
                System.out.println("Ignoring non-success payment event.");
                return;
            }

            deliveryService.createDelivery(event);

        } catch (Exception e) {
            System.err.println("Unable to process payment-success event.");
            throw new RuntimeException(e);
        }
    }
}
