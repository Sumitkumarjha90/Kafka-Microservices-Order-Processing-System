package com.ecommerce.event;

import java.math.BigDecimal;

public class OrderCreatedEvent {
	
	private Long orderId;
	
	private Long customerId;
	
	private BigDecimal amount;
	
	private String paymentMethod;
	
	public OrderCreatedEvent() {
		
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

	public void setAmount(BigDecimal amonut) {
		this.amount = amonut;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	
	

}
