package com.saga.pattern.event;


import com.saga.pattern.entity.Order;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateOrdersEvent {
    List<Order> orders;
}
