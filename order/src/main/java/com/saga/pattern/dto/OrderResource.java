package com.saga.pattern.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResource {
    private String transactionId;
    private String name;
    private Integer quantity;
    private String status;
}
