package com.ecommerce.event;

public class PaymentSuccessEvent {

    private String eventId;
    private String eventType;
    private Long orderId;
    private Long customerId;
    private Double amount;
    private String paymentId;
    private String paymentMethod;
    private String paymentStatus;
    private String deliveryAddress;

    public PaymentSuccessEvent() {
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    @Override
    public String toString() {
        return "PaymentSuccessEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", orderId=" + orderId +
                ", customerId=" + customerId +
                ", amount=" + amount +
                ", paymentId='" + paymentId + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                '}';
    }
}
