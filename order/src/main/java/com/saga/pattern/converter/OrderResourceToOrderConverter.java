package com.saga.pattern.converter;

import com.saga.pattern.constant.OrderStatus;
import com.saga.pattern.dto.OrderResource;
import com.saga.pattern.entity.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class OrderResourceToOrderConverter implements Converter<OrderResource, Order> {
    @Override
    public Order convert(OrderResource orderResource) {
        return Order.builder()
                .created(LocalDateTime.now())
                .name(orderResource.getName())
                .status(OrderStatus.ORDER_RECEIVED)
                .quantity(orderResource.getQuantity())
                .transactionId(UUID.randomUUID().toString())
                .build();
    }
}
