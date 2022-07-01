package com.saga.pattern.scheduler;

import com.saga.pattern.constant.OrderStatus;
import com.saga.pattern.entity.Order;
import com.saga.pattern.event.CreateOrdersEvent;
import com.saga.pattern.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderRepository orderRepository;
    private final static Logger LOGGER = LoggerFactory.getLogger(OrderScheduler.class);
    private final ApplicationEventPublisher applicationEventPublisher;

    @Scheduled(fixedRate = 60000)
    @Transactional(readOnly = true)
    public void resendMissingOrders() {
        LOGGER.warn("Missing Received Orders are sent to RabbitMQ by Scheduler Task");
        List<Order> lOrders = orderRepository.findByStatus(OrderStatus.ORDER_RECEIVED);
        applicationEventPublisher.publishEvent(new CreateOrdersEvent(lOrders));
    }

}
