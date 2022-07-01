package com.saga.pattern.event.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saga.pattern.constant.OrderStatus;
import com.saga.pattern.constant.PaymentStatus;
import com.saga.pattern.constant.StockStatus;
import com.saga.pattern.dto.OrderDto;
import com.saga.pattern.dto.PaymentDto;
import com.saga.pattern.dto.StockDto;
import com.saga.pattern.entity.Order;
import com.saga.pattern.event.CreateOrdersEvent;
import com.saga.pattern.repository.OrderRepository;
import com.saga.pattern.send.Sender;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderEventHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrderEventHandler.class);
    private final Sender sender;
    private final OrderRepository orderRepository;

    @TransactionalEventListener
    public void createOrdersEvent(CreateOrdersEvent createOrdersEvent) throws JsonProcessingException {
        List<Order> lOrders = createOrdersEvent.getOrders();
        if (!CollectionUtils.isEmpty(lOrders)) {
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

            lOrders.forEach(order -> {
                order.setStatus(OrderStatus.ORDER_PENDING);
                orderRepository.save(order);
            });
        }
    }
}
