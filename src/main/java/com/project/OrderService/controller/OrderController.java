package com.project.OrderService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.OrderService.model.OrderRequest;
import com.project.OrderService.model.OrderResponse;
import com.project.OrderService.service.OrderService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/order")
@Log4j2
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PostMapping("/placeorder")
	public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest) {
		Long orderId = orderService.placeOrder(orderRequest);
		log.info("Order Id: {} ", orderId);
		return new ResponseEntity<Long>(orderId, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable("id") long orderId) {
		OrderResponse orderResponse = orderService.getOrderDetails(orderId);
		return new ResponseEntity<OrderResponse>(orderResponse, HttpStatus.OK);

	}

}
