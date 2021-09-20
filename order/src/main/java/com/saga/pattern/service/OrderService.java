package com.saga.pattern.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saga.pattern.constant.OrderStatus;
import com.saga.pattern.constant.PaymentStatus;
import com.saga.pattern.constant.StockStatus;
import com.saga.pattern.dto.OrderDto;
import com.saga.pattern.dto.PaymentDto;
import com.saga.pattern.dto.StockDto;
import com.saga.pattern.entity.Order;
import com.saga.pattern.repository.OrderRepository;
import com.saga.pattern.send.Sender;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private final Sender sender;
    private final OrderRepository orderRepository;

    @Transactional
    public List<Order> createOrder(List<Order> orders) throws JsonProcessingException {
        List<Order> lOrders = orderRepository.saveAll(orders);
        String transactionId = UUID.randomUUID().toString();
        LOGGER.info("Sending PAYMENT_REQUESTED notification to payment queue. Transaction_id: {}", transactionId);
        sender.paymentNotify(PaymentDto.builder()
                .transactionId(transactionId)
                .orders(lOrders.stream()
                        .map(item -> OrderDto.builder()
                                .name(item.getName())
                                .transactionId(item.getTransactionId())
                                .quantity(item.getQuantity())
                                .status(OrderStatus.ORDER_PENDING.name())
                                .price((double) new Random().nextInt(100))
                                .build())
                        .collect(Collectors.toList()))
                .status(PaymentStatus.PAYMENT_REQUESTED.name())
                .build());
        LOGGER.info("Sending STOCK_REQUESTED notification to stock queue. Transaction_id: {}", transactionId);
        sender.stockNotify(StockDto.builder()
                .orders(lOrders.stream()
                        .map(item -> OrderDto.builder()
                                .name(item.getName())
                                .transactionId(item.getTransactionId())
                                .price((double) new Random().nextInt(100))
                                .quantity(item.getQuantity())
                                .status(OrderStatus.ORDER_PENDING.name())
                                .build())
                        .collect(Collectors.toList()))
                .transactionId(transactionId)
                .status(StockStatus.STOCK_REQUESTED.name())
                .build());
        return lOrders;
    }

    public void updateOrder(String transactionId, OrderStatus status, String paymentId) {
        Optional<Order> lOrder = orderRepository.findByTransactionId(transactionId);
        if (lOrder.isPresent()) {
            Order order = lOrder.get();
            order.setStatus(status);
            order.setPaymentId(paymentId);
            orderRepository.save(order);
        }
    }

    public Order findByTransactionId(String transactionId) {
        return orderRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Order can not be found by transactionId : %s", transactionId)));
    }
}
