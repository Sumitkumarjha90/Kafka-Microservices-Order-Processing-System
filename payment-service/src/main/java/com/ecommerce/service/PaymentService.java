package com.ecommerce.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.ecommerce.entity.Payment;
import com.ecommerce.event.OrderCreatedEvent;
import com.ecommerce.repository.PaymentRepository;

@Service
public class PaymentService {

	private final PaymentRepository paymentRepository;

	public PaymentService(PaymentRepository paymentRepository) {

		this.paymentRepository = paymentRepository;

	}

	public void processPayment(OrderCreatedEvent event) {

		Payment payment = new Payment();

		payment.setOrderId(event.getOrderId());

		payment.setCustomerId(event.getCustomerId());

		payment.setAmount(event.getAmount());

		payment.setPaymentMethod(event.getPaymentMethod());

		payment.setPaymentStatus("SUCCESS");

		payment.setCreatedAt(LocalDateTime.now());

		paymentRepository.save(payment);
		System.out.println("Payment saved successfully!");
		System.out.println("Order ID: " + event.getOrderId());
		System.out.println("Amount: " + event.getAmount());

	}
}
