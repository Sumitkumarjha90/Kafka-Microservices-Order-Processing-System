package com.ecommerce.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String eventId;

    @Column(nullable = false, unique = true)
    private Long orderId;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false, unique = true)
    private String trackingNumber;

    private String deliveryAddress;

    @Column(nullable = false)
    private String deliveryStatus;

    @Column(nullable = false)
    private LocalDateTime eventTime;

    public Delivery() {
    }

    @PrePersist
    public void prePersist() {
        if (eventTime == null) {
            eventTime = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public LocalDateTime getEventTime() { return eventTime; }
}
