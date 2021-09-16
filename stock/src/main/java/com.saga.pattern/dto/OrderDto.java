package com.saga.pattern.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OrderDto {
    private String transactionId;
    private String name;
    private String status;
    private Double price;
    private String paymentId;
    private int quantity;
}
