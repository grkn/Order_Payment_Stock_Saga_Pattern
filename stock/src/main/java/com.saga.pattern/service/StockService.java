package com.saga.pattern.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.saga.pattern.constant.OrderStatus;
import com.saga.pattern.constant.PaymentStatus;
import com.saga.pattern.dto.OrderDto;
import com.saga.pattern.dto.PaymentDto;
import com.saga.pattern.dto.StockDto;
import com.saga.pattern.entity.Stock;
import com.saga.pattern.repository.StockRepository;
import com.saga.pattern.sender.Sender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final Sender sender;
    private static final List<String> AVAILABLE_PRODUCTS = Arrays.asList("order1", "order2", "order3", "order4", "order5");

    @PostConstruct
    public void init() {
        stockRepository.deleteAll();
        AVAILABLE_PRODUCTS.forEach(product -> {
            int totalQuantity = new Random().nextInt(10);
            stockRepository.save(Stock.builder()
                    .name(product)
                    .quantity(totalQuantity)
                    .build());
        });
    }


    @Transactional
    public void prepareStock(StockDto stockDto) {
        stockDto.getOrders().forEach(orderDto -> {
            Optional<Stock> lStock = stockRepository.findByName(orderDto.getName());
            if (lStock.isPresent()) {
                Stock stock = lStock.get();
                int total = stock.getQuantity();
                int requestedCount = orderDto.getQuantity();
                if (total - requestedCount >= 0) {
                    stock.setQuantity(total - requestedCount);
                    stockRepository.save(stock);
                    orderDto.setStatus(OrderStatus.ORDER_STOCK_COMPLETED.name());
                    sendOrderStockCompletedNotification(orderDto);
                } else {
                    sendPaymentFailedNotification(stockDto);
                    return;
                }
            } else {
                sendPaymentFailedNotification(stockDto);
                return;
            }
        });

        sendPaymentAvailableNotification(stockDto, PaymentStatus.PAYMENT_AVAILABLE);
    }

    private void sendPaymentAvailableNotification(StockDto stockDto, PaymentStatus status) {
        try {
            sender.paymentNotify(PaymentDto.builder()
                    .transactionId(stockDto.getTransactionId())
                    .orders(stockDto.getOrders())
                    .status(status.name())
                    .build());
        } catch (JsonProcessingException e) {
            // nothing to do
        }
    }

    private void sendOrderStockCompletedNotification(OrderDto orderDto) {
        try {
            sender.orderNotify(orderDto);
        } catch (JsonProcessingException e) {
            // nothing to do
        }
    }

    private void sendPaymentFailedNotification(StockDto stockDto) {
        sendPaymentAvailableNotification(stockDto, PaymentStatus.PAYMENT_FAILED);
    }
}
