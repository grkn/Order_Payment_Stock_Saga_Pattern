package com.saga.pattern.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OrderResource {
    private String transactionId;
    private String name;
    private Integer quantity;
}
