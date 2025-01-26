package com.project.OrderService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.OrderService.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
