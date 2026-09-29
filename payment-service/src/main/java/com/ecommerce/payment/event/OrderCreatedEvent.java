package com.ecommerce.payment.event;

import java.math.BigDecimal;

public class OrderCreatedEvent {

	private Long orderId;

    private Long customerId;

    private BigDecimal amount;

    private String paymentMethod;

    public OrderCreatedEvent() {
    }


    public OrderCreatedEvent(Long orderId, Long customerId, BigDecimal amount, String paymentMethod) {
		super();
		this.orderId = orderId;
		this.customerId = customerId;
		this.amount = amount;
		this.paymentMethod = paymentMethod;
	}





	@Override
	public String toString() {
		return "OrderCreatedEvent [orderId=" + orderId + ", customerId=" + customerId + ", amount=" + amount
				+ ", paymentMethod=" + paymentMethod + ", getOrderId()=" + getOrderId() + ", getCustomerId()="
				+ getCustomerId() + ", getAmount()=" + getAmount() + ", getPaymentMethod()=" + getPaymentMethod()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
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


	public BigDecimal getAmount() {
		return amount;
	}


	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}


	public String getPaymentMethod() {
		return paymentMethod;
	}


	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
    
}