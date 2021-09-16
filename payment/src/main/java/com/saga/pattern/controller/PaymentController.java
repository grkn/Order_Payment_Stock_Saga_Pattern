package com.saga.pattern.controller;

import com.saga.pattern.dto.PaymentResource;
import com.saga.pattern.entity.Payment;
import com.saga.pattern.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/saga/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResource> getPayment(@PathVariable String paymentId) {
        Payment payment = paymentService.findById(paymentId);
        return ResponseEntity.ok(PaymentResource.builder()
                .status(payment.getStatus().name())
                .totalPrice(payment.getTotalPrice())
                .transactionId(payment.getTransactionId())
                .build());
    }
}
