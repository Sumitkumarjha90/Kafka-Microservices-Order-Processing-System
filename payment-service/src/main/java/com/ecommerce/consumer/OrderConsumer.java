package com.ecommerce.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.ecommerce.event.OrderCreatedEvent;
import com.ecommerce.service.PaymentService;

@Service
public class OrderConsumer {

	private final PaymentService paymentService;

	public OrderConsumer(PaymentService paymentService) {

		this.paymentService = paymentService;

	}

	@KafkaListener(topics = "order-created", groupId = "payment-group")

	public void consumerOrder(OrderCreatedEvent event) {

		System.out.println("================================");
		System.out.println("Order received by Payment Service");
		System.out.println("Order ID: " + event.getOrderId());
		System.out.println("Customer ID: " + event.getCustomerId());
		System.out.println("Amount: " + event.getAmount());
		System.out.println("Payment Method: " + event.getPaymentMethod());
		System.out.println("================================");
		
		paymentService.processPayment(event);
	}
}
