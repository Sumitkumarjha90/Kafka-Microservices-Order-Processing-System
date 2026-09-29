package com.ecommerce.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ecommerce.entity.Payment;
import com.ecommerce.event.PaymentProcessedEvent;
import com.ecommerce.kafka.PaymentKafkaProducer;
import com.ecommerce.payment.event.OrderCreatedEvent;
import com.ecommerce.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentKafkaProducer paymentKafkaProducer;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentKafkaProducer paymentKafkaProducer) {

        this.paymentRepository = paymentRepository;
        this.paymentKafkaProducer = paymentKafkaProducer;
    }

    public PaymentProcessedEvent processPayment(OrderCreatedEvent event) {

        Payment payment = new Payment();

        payment.setOrderId(event.getOrderId());
        payment.setCustomerId(event.getCustomerId());
        payment.setAmount(event.getAmount());
        payment.setPaymentMethod(event.getPaymentMethod());
        payment.setPaymentId("TXN-" + System.currentTimeMillis());
        payment.setCreatedAt(LocalDateTime.now());

        PaymentProcessedEvent paymentEvent = new PaymentProcessedEvent();

        paymentEvent.setEventId("PAY-" + UUID.randomUUID());
        paymentEvent.setOrderId(event.getOrderId());
        paymentEvent.setCustomerId(event.getCustomerId());
        paymentEvent.setAmount(event.getAmount());
        paymentEvent.setPaymentId(payment.getPaymentId());
        paymentEvent.setPaymentMethod(event.getPaymentMethod());

        // PAYMENT VALIDATIONS

        if (event.getAmount() == null ||
                event.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {

            payment.setPaymentStatus("FAILED");
            payment.setReason("INVALID_AMOUNT");

            paymentEvent.setEventType("PAYMENT_FAILED");
            paymentEvent.setPaymentStatus("FAILED");
            paymentEvent.setReason("INVALID_AMOUNT");

        } else if (event.getPaymentMethod() == null ||
                event.getPaymentMethod().isBlank()) {

            payment.setPaymentStatus("FAILED");
            payment.setReason("PAYMENT_METHOD_MISSING");

            paymentEvent.setEventType("PAYMENT_FAILED");
            paymentEvent.setPaymentStatus("FAILED");
            paymentEvent.setReason("PAYMENT_METHOD_MISSING");

        } else if (event.getPaymentMethod().equalsIgnoreCase("UPI")) {

            payment.setPaymentStatus("SUCCESS");

            paymentEvent.setEventType("PAYMENT_SUCCESS");
            paymentEvent.setPaymentStatus("SUCCESS");

        } else if (event.getPaymentMethod().equalsIgnoreCase("CARD")) {

            payment.setPaymentStatus("SUCCESS");

            paymentEvent.setEventType("PAYMENT_SUCCESS");
            paymentEvent.setPaymentStatus("SUCCESS");

        } else if (event.getPaymentMethod().equalsIgnoreCase("COD")) {

            payment.setPaymentStatus("PENDING");

            paymentEvent.setEventType("PAYMENT_PENDING");
            paymentEvent.setPaymentStatus("PENDING");

        } else {

            payment.setPaymentStatus("FAILED");
            payment.setReason("UNSUPPORTED_PAYMENT_METHOD");

            paymentEvent.setEventType("PAYMENT_FAILED");
            paymentEvent.setPaymentStatus("FAILED");
            paymentEvent.setReason("UNSUPPORTED_PAYMENT_METHOD");
        }

        // Save payment
        paymentRepository.save(payment);

        // Publish event to Kafka
        paymentKafkaProducer.sendPaymentProcessedEvent(paymentEvent);

        return paymentEvent;
    }
}