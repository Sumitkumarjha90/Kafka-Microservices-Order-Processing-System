package com.ecommerce.notification;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.ecommerce.event.DeliveryCreatedEvent;
import com.ecommerce.event.OrderCreatedEvent;
import com.ecommerce.event.PaymentProcessedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NotificationService {

    private final ObjectMapper objectMapper;

    public NotificationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // =========================================================
    // ORDER CREATED
    // =========================================================

    @KafkaListener(
            topics = "order-created",
            groupId = "notification-group"
    )
    public void consumeOrderCreated(String message) {

        try {

            OrderCreatedEvent event =
                    objectMapper.readValue(message, OrderCreatedEvent.class);

            System.out.println();
            System.out.println("==========================================");
            System.out.println("       ORDER CREATED NOTIFICATION");
            System.out.println("==========================================");

            System.out.println("Event ID       : " + event.getEventId());
            System.out.println("Event Type     : " + event.getEventType());
            System.out.println("Order ID       : " + event.getOrderId());
            System.out.println("Customer ID    : " + event.getCustomerId());
            System.out.println("Amount         : " + event.getAmount());
            System.out.println("Delivery Addr  : " + event.getDeliveryAddress());

            System.out.println("------------------------------------------");
            System.out.println("Notification:");
            System.out.println(
                    "Your order #" + event.getOrderId()
                    + " has been successfully created."
            );

            System.out.println("==========================================");
            System.out.println();

        } catch (Exception e) {

            System.out.println("Error processing ORDER_CREATED event");
            e.printStackTrace();
        }
    }


    // =========================================================
    // PAYMENT PROCESSED
    // =========================================================

    @KafkaListener(
            topics = "payment-processed",
            groupId = "notification-group"
    )
    public void consumePaymentProcessed(String message) {

        try {

            PaymentProcessedEvent event =
                    objectMapper.readValue(message, PaymentProcessedEvent.class);

            System.out.println();
            System.out.println("==========================================");
            System.out.println("      PAYMENT PROCESSED NOTIFICATION");
            System.out.println("==========================================");

            System.out.println("Event ID       : " + event.getEventId());
            System.out.println("Event Type     : " + event.getEventType());
            System.out.println("Order ID       : " + event.getOrderId());
            System.out.println("Customer ID    : " + event.getCustomerId());
            System.out.println("Amount         : " + event.getAmount());
            System.out.println("Payment ID     : " + event.getPaymentId());
            System.out.println("Payment Method : " + event.getPaymentMethod());
            System.out.println("Payment Status : " + event.getPaymentStatus());
            System.out.println("Reason         : " + event.getReason());

            System.out.println("------------------------------------------");

            if ("SUCCESS".equalsIgnoreCase(event.getPaymentStatus())) {

                System.out.println(
                        "Notification: Payment successful for order #"
                        + event.getOrderId()
                );

            } else if ("PENDING".equalsIgnoreCase(event.getPaymentStatus())) {

                System.out.println(
                        "Notification: Payment is pending for order #"
                        + event.getOrderId()
                );

            } else if ("FAILED".equalsIgnoreCase(event.getPaymentStatus())) {

                System.out.println(
                        "Notification: Payment failed for order #"
                        + event.getOrderId()
                        + ". Reason: "
                        + event.getReason()
                );
            }

            System.out.println("==========================================");
            System.out.println();

        } catch (Exception e) {

            System.out.println("Error processing PAYMENT_PROCESSED event");
            e.printStackTrace();
        }
    }


    // =========================================================
    // DELIVERY CREATED
    // =========================================================

    @KafkaListener(
            topics = "delivery-created",
            groupId = "notification-group"
    )
    public void consumeDeliveryCreated(String message) {

        try {

            DeliveryCreatedEvent event =
                    objectMapper.readValue(message, DeliveryCreatedEvent.class);

            System.out.println();
            System.out.println("==========================================");
            System.out.println("       DELIVERY NOTIFICATION");
            System.out.println("==========================================");

            System.out.println("Event ID        : " + event.getEventId());
            System.out.println("Event Type      : " + event.getEventType());
            System.out.println("Order ID        : " + event.getOrderId());
            System.out.println("Customer ID     : " + event.getCustomerId());
            System.out.println("Tracking Number : " + event.getTrackingNumber());
            System.out.println("Delivery Addr   : " + event.getDeliveryAddress());
            System.out.println("Delivery Status : " + event.getDeliveryStatus());

            System.out.println("------------------------------------------");

            System.out.println(
                    "Notification: Your order #"
                    + event.getOrderId()
                    + " has been assigned for delivery."
            );

            System.out.println(
                    "Tracking Number: "
                    + event.getTrackingNumber()
            );

            System.out.println("==========================================");
            System.out.println();

        } catch (Exception e) {

            System.out.println("Error processing DELIVERY_CREATED event");
            e.printStackTrace();
        }
    }
}