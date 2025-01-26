package com.project.OrderService.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.OrderService.External.Client.PaymentService;
import com.project.OrderService.External.Client.ProductService;
import com.project.OrderService.External.request.PaymentRequest;
import com.project.OrderService.External.request.PaymentResponse;
import com.project.OrderService.entity.Order;
import com.project.OrderService.exception.CustomException;
import com.project.OrderService.model.OrderRequest;
import com.project.OrderService.model.OrderResponse;
import com.project.OrderService.model.OrderResponse.ProductDetails;
import com.project.OrderService.model.ProductResponse;
import com.project.OrderService.repository.OrderRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ProductService productService;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public Long placeOrder(OrderRequest orderRequest) {

		// Order Entity-> Save the Order with status CREATED
		// ProductService - Block product (Reduce the Quantity)
		// paymentService - Payments - success -> COMPLETED. Else
		// CANCELLED

		// reduce quantity
		productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());
		log.info("Creating Order with status CREATED");

		// create order
		log.info("Placing Order Request: {}", orderRequest);
		Order order = Order.builder().amount(orderRequest.getTotalAmount()).orderStatus("CREATED")
				.productId(orderRequest.getProductId()).orderDate(Instant.now()).quantity(orderRequest.getQuantity())
				.build();

		// save order
		order = orderRepository.save(order);
		log.info("Order Places successfully with order id: {}", order.getId());

		// call payment service
		log.info("Calling Payment Service to complete the Payment");
		PaymentRequest paymentRequest = PaymentRequest.builder().orderId(order.getId())
				.paymentMode(orderRequest.getPaymentMode()).amount(orderRequest.getTotalAmount()).build();

		String orderStatus = null;
		try {
			paymentService.doPayment(paymentRequest);
			log.info("Payment done successfully. Changing order status to PLACED", orderStatus = "PLACED");

		} catch (Exception e) {
			log.error("Error occured in payment Changing order status to PAYMENT_FAILED",
					orderStatus = "PAYMENT_FAILED ");
		}

		order.setOrderStatus(orderStatus);
		orderRepository.save(order);
		log.info("Order Placed with Order Id : {}", order.getId());

		return order.getId();

	}

	@Override
	public OrderResponse getOrderDetails(long orderId) {
		log.info("Get Order details for Order Id: {}", orderId);

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new CustomException("Order not found for Order Id :" + orderId, "NOT_FOUND", 404));

		log.info("Invoking Produce Service to fetch the Product details for product Id {} ", order.getProductId());

		ProductResponse productResponse = restTemplate
				.getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId(), ProductResponse.class);

		log.info("Getting payment info from Payment Service");
		
		PaymentResponse paymentResponse = null;
		try {
		paymentResponse = restTemplate
				.getForObject("http://PAYMENT-SERVICE/payment/order/" + order.getId(),
				PaymentResponse.class);
		} catch (Exception ex) {
		    log.info("Payment not found with Order id: {}", orderId);
		    throw new CustomException("Payment not found with Order id: " + orderId, "PAYMENT_NOT_FOUND", 404);
		}
		OrderResponse.ProductDetails produceDetails = OrderResponse.ProductDetails.builder()
				.productName(productResponse.getProductName()).productId(productResponse.getProductId())
				.price(productResponse.getPrice()).quantity(productResponse.getQuantity()).build();

		OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails.builder()
				.paymentId(paymentResponse.getPaymentId()).paymentDate(paymentResponse.getPaymentDate())
				.paymentMode(paymentResponse.getPaymentMode()).status(paymentResponse.getStatus())

				.build();

		OrderResponse orderResponse = OrderResponse.builder().orderId(order.getId()).orderStatus(order.getOrderStatus())
				.amount(order.getAmount()).orderDate(order.getOrderDate()).productDetails(produceDetails)
				.paymentDetails(paymentDetails).build();

		return orderResponse;
	}
}
