package com.ecommerce.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ecommerce.entity.Payment;
import com.ecommerce.event.PaymentProcessedEvent;
import com.ecommerce.repository.PaymentRepository;

@Service
public class PaymentService {

	private final PaymentRepository paymentRepository;

	public PaymentService(PaymentRepository paymentRepository) {

		this.paymentRepository = paymentRepository;

	}

	public PaymentProcessedEvent processPayment(PaymentProcessedEvent event) {

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
//	------------------------------------------------
	

		if (event.getAmount() == null || event.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

			payment.setPaymentStatus("FAILED");
			payment.setReason("INVALID_AMOUNT");

			paymentEvent.setEventType("PAYMENT_FAILED");
			paymentEvent.setPaymentStatus("FAILED");
			paymentEvent.setReason("INVALID_AMOUNT");

		} else if (event.getPaymentMethod() == null || event.getPaymentMethod().isBlank()) {

			payment.setPaymentStatus("FAILED");
			payment.setReason("PAYMENT_METHOD_MISSING");

			paymentEvent.setEventType("PAYMENT_FAILED");
			paymentEvent.setPaymentStatus("FAILED");
			paymentEvent.setReason("PAYMENT_METHOD_MISSING");

		} else if (event.getPaymentMethod().equalsIgnoreCase("UPI")) {

			// For now, assume UPI payment succeeds
			payment.setPaymentStatus("SUCCESS");

			paymentEvent.setEventType("PAYMENT_SUCCESS");
			paymentEvent.setPaymentStatus("SUCCESS");

		} else if (event.getPaymentMethod().equalsIgnoreCase("CARD")) {

			// For now, assume card payment succeeds
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

		// Save payment to MySQL
		paymentRepository.save(payment);

		return paymentEvent;
	}
}
