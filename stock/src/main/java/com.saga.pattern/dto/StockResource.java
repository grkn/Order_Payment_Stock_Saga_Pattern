package com.saga.pattern.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class StockResource {

    private String name;
    private Integer quantity;
}
