package com.ecommerce.service;

import org.springframework.stereotype.Service;

import com.ecommerce.entity.Order;
import com.ecommerce.event.OrderCreatedEvent;
import com.ecommerce.kafka.OrderKafkaProducer;
import com.ecommerce.repository.OrderRepository;

@Service
public class OrderService {
	
	private final OrderRepository orderRepository;
	private final OrderKafkaProducer kafkaProducer;
	
	
	public OrderService(OrderRepository orderRepository ,  OrderKafkaProducer kafkaProducer) {
		
		this.orderRepository = orderRepository;
		this.kafkaProducer = kafkaProducer;
		
		
	}
	
	public Order createOrder(Order order) {
		

        // 1. Set initial status
		order.setStatus("Created");
		
	     // 2. Save order in database
		Order savedOrder = orderRepository.save(order);
		
		 // 3. Create Kafka event
		OrderCreatedEvent event = new OrderCreatedEvent();
		
        event.setEventId("EVT-" + savedOrder.getId());
        event.setEventType("ORDER_CREATED");
        event.setOrderId(savedOrder.getId());
        event.setCustomerId(savedOrder.getCustomerId());
        event.setAmount(savedOrder.getAmount());
        event.setDeliveryAddress(savedOrder.getDeliveryAddress());
        
        
        // 4. Send event to Kafka
        kafkaProducer.sendOrderCreatedEvent(event);
        
        return savedOrder;
        
		
	}

}
