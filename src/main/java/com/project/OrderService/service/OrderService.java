package com.project.OrderService.service;

import com.project.OrderService.model.OrderRequest;
import com.project.OrderService.model.OrderResponse;

public interface OrderService {

	public Long placeOrder(OrderRequest orderRequest);

	public OrderResponse getOrderDetails(long orderId);

}
