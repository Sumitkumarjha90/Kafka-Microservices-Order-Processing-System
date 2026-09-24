package com.ecommerce.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecommerce.event.OrderCreatedEvent;

@Service
public class OrderKafkaProducer {
	
	private static final String Topic = "order-created";
	
	private final KafkaTemplate<String ,OrderCreatedEvent> kafkaTemplate;
	
	public OrderKafkaProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
		
		this.kafkaTemplate = kafkaTemplate;
		
	}

	public void sendOrderCreatedEvent(OrderCreatedEvent event) {
		
		kafkaTemplate.send(Topic , event.getOrderId().toString(),event);
	}
}
