package com.saga.pattern.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saga.pattern.constant.OrderStatus;
import com.saga.pattern.constant.PaymentStatus;
import com.saga.pattern.constant.StockStatus;
import com.saga.pattern.dto.PaymentDto;
import com.saga.pattern.dto.StockDto;
import com.saga.pattern.entity.Payment;
import com.saga.pattern.repository.PaymentRepository;
import com.saga.pattern.sender.Sender;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final Sender sender;

    @Transactional
    public Payment createPayment(PaymentDto paymentDto) {
        Payment payment = Payment.builder()
                .status(PaymentStatus.PAYMENT_PENDING)
                .totalPrice(paymentDto.getOrders().stream().mapToDouble(value -> value.getQuantity() * value.getPrice()).sum())
                .transactionId(paymentDto.getTransactionId())
                .build();
        return paymentRepository.saveAndFlush(payment);
    }

    public void updateStatus(String transactionId, PaymentStatus paymentStatus) {
        Optional<Payment> lPayment = paymentRepository.findByTransactionId(transactionId);
        Payment payment = lPayment.orElseThrow(() -> new IllegalArgumentException("Payment data can not be found in database"));
        payment.setStatus(paymentStatus);
        paymentRepository.save(payment);
    }

    @Transactional
    public void buyFrom3rdPartyApp(PaymentDto paymentDto) {
        Optional<Payment> payment = paymentRepository.findByTransactionId(paymentDto.getTransactionId());

        if (payment.isPresent()) {
            Payment lPayment = payment.get();
            boolean isSuccess = new Random().nextBoolean();
            if (!isSuccess) {
                lPayment.setStatus(PaymentStatus.PAYMENT_FAILED);
                paymentRepository.save(lPayment);
                sendOrderFailedNotification(paymentDto);
                sendStockFailedNotification(paymentDto);
            } else {
                sendNotificationToOrderService(paymentDto, lPayment);
            }
        } else {
            sendOrderFailedNotification(paymentDto);
            sendStockFailedNotification(paymentDto);
        }
    }

    private void sendStockFailedNotification(PaymentDto paymentDto) {
        try {
            sender.stockNotify(StockDto.builder()
                    .transactionId(paymentDto.getTransactionId())
                    .orders(paymentDto.getOrders())
                    .status(StockStatus.STOCK_FAILED.name())
                    .build());
        } catch (JsonProcessingException e) {
            // Nothing to do
        }
    }

    private void sendOrderFailedNotification(PaymentDto paymentDto) {
        paymentDto.getOrders().forEach(orderDto -> {
            try {
                orderDto.setStatus(OrderStatus.ORDER_FAILED.name());
                sender.orderNotify(orderDto);
            } catch (JsonProcessingException e) {
                // do nothing for now
            }
        });
    }

    private void sendNotificationToOrderService(PaymentDto paymentDto, Payment lPayment) {
        lPayment.setStatus(PaymentStatus.PAYMENT_COMPLETED);
        final String paymentId = lPayment.getId();
        paymentRepository.save(lPayment);
        paymentDto.getOrders().forEach(orderDto -> {
            try {
                orderDto.setPaymentId(paymentId);
                orderDto.setStatus(OrderStatus.ORDER_COMPLETED.name());
                sender.orderNotify(orderDto);
            } catch (JsonProcessingException e) {
                // do nothing for now
            }
        });
    }

    public Payment findById(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment can not be found by given id"));

    }
}
