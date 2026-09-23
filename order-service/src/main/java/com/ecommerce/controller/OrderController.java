package com.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.entity.Order;
import com.ecommerce.service.OrderService;

@RestController
@RequestMapping("/orders")

public class OrderController {
	
	private final OrderService orderService;
	
	public OrderController(OrderService orderService)
	{
		this.orderService = orderService;
	}
	
	@PostMapping
	public ResponseEntity<Order> createOrder(@RequestBody Order order){
		
		Order saveOrder = orderService.createOrder(order);
		
		return ResponseEntity.ok(saveOrder);
		
	}

}
