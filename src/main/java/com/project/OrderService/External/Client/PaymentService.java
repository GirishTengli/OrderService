package com.project.OrderService.External.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.project.OrderService.External.request.PaymentRequest;
import com.project.OrderService.exception.CustomException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@CircuitBreaker(name = "external", fallbackMethod = "fallback")
@FeignClient(name="PAYMENT-SERVICE/payment")
public interface PaymentService {

	@PutMapping
	public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);

	default void fallback(Exception e) {
		throw new CustomException("Payment service is not available", "UNAVAILABLE", 500);
	}
}
