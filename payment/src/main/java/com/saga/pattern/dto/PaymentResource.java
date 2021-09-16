package com.saga.pattern.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PaymentResource {
    private String status;
    private Double totalPrice;
    private String transactionId;
}
