package com.project.OrderService.External.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.project.OrderService.External.request.PaymentRequest;

@FeignClient(name="PAYMENT-SERVICE/payment")
public interface PaymentService {

	@PutMapping
	public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);
}
