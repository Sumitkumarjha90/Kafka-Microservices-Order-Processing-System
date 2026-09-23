package com.ecommerce.service;

import org.springframework.stereotype.Service;

import com.ecommerce.entity.Order;
import com.ecommerce.repository.OrderRepository;

@Service
public class OrderService {
	
	final OrderRepository orderRepository;
	
	public OrderService(OrderRepository orderRepository) {
		
		this.orderRepository = orderRepository;
		
	}
	
	public Order createOrder(Order order) {
		
		order.setStatus("Created");
		return orderRepository.save(order);
		
	}

}
