package com.ecommerce.service;

import com.ecommerce.entity.Delivery;
import com.ecommerce.event.DeliveryCreatedEvent;
import com.ecommerce.event.PaymentSuccessEvent;
import com.ecommerce.kafka.DeliveryKafkaProducer;
import com.ecommerce.repository.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryKafkaProducer deliveryKafkaProducer;

    private final AtomicLong trackingSequence = new AtomicLong(50000);

    public DeliveryService(
            DeliveryRepository deliveryRepository,
            DeliveryKafkaProducer deliveryKafkaProducer) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryKafkaProducer = deliveryKafkaProducer;
    }

    @Transactional
    public void createDelivery(PaymentSuccessEvent event) {

        // Idempotency: same Kafka event must not create business data twice.
        if (deliveryRepository.existsByEventId(event.getEventId())) {
            System.out.println("Duplicate event ignored: " + event.getEventId());
            return;
        }

        // Business protection: one delivery per order.
        if (deliveryRepository.existsByOrderId(event.getOrderId())) {
            System.out.println("Delivery already exists for order: " + event.getOrderId());
            return;
        }

        Delivery delivery = new Delivery();
        delivery.setEventId("DEL-" + event.getOrderId());
        delivery.setOrderId(event.getOrderId());
        delivery.setCustomerId(event.getCustomerId());
        delivery.setTrackingNumber("TRK-" + trackingSequence.incrementAndGet());
        delivery.setDeliveryAddress(event.getDeliveryAddress());
        delivery.setDeliveryStatus("CREATED");

        Delivery saved = deliveryRepository.save(delivery);

        DeliveryCreatedEvent deliveryEvent = new DeliveryCreatedEvent();
        deliveryEvent.setEventId(saved.getEventId());
        deliveryEvent.setEventType("DELIVERY_CREATED");
        deliveryEvent.setOrderId(saved.getOrderId());
        deliveryEvent.setCustomerId(saved.getCustomerId());
        deliveryEvent.setTrackingNumber(saved.getTrackingNumber());
        deliveryEvent.setDeliveryAddress(saved.getDeliveryAddress());
        deliveryEvent.setDeliveryStatus(saved.getDeliveryStatus());

        deliveryKafkaProducer.publish(deliveryEvent);
    }
}
