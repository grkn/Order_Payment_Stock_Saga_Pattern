package com.saga.pattern.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saga.pattern.dto.OrderResource;
import com.saga.pattern.entity.Order;
import com.saga.pattern.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/saga/v1/order")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ConversionService conversionService;

    @PostMapping
    public ResponseEntity<List<OrderResource>> createOrder(@RequestBody List<OrderResource> orderResource) throws JsonProcessingException {
        List<Order> orders = orderResource.stream().map(item -> conversionService.convert(item, Order.class)).collect(Collectors.toList());
        orders = orderService.createOrder(orders);
        return ResponseEntity.ok(orders.stream().map(item -> OrderResource.builder()
                .transactionId(item.getTransactionId())
                .name(item.getName())
                .build())
                .collect(Collectors.toList()));
    }

}
