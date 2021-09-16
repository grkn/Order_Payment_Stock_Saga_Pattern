package com.saga.pattern.dto;


import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class StockDto {
    private List<OrderDto> orders;
    private String transactionId;
    private String status;
}
