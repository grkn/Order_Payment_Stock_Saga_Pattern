package com.saga.pattern.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PaymentDto {
    private String status;
    private String transactionId;
    List<OrderDto> orders;
}
