package com.saga.pattern.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saga.pattern.constant.ApplicationConstant;
import com.saga.pattern.constant.StockStatus;
import com.saga.pattern.dto.StockDto;
import com.saga.pattern.service.StockService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
public class Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    private final ObjectMapper objectMapper;
    private final StockService stockService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = ApplicationConstant.STOCK_QUEUE, durable = "true"),
            exchange = @Exchange(value = ApplicationConstant.EXCHANGE, ignoreDeclarationExceptions = "true"),
            key = ApplicationConstant.STOCK_ROUTING_KEY)
    )
    public void listen(Message message) throws IOException {
        StockDto stockDto = objectMapper.readValue(message.getBody(), StockDto.class);
        StockStatus stockStatus = StockStatus.valueOf(stockDto.getStatus());
        LOGGER.info("Stock {} request is received. TransactionId : {}", stockStatus.name() ,stockDto.getTransactionId());
        switch (stockStatus) {
            case STOCK_PENDING:
            case STOCK_COMPLETED:
                break;
            case STOCK_FAILED:
                stockService.recalculateStockValues(stockDto.getOrders());
                break;
            case STOCK_REQUESTED:
                stockService.prepareStock(stockDto);
                break;
            default:
                break;
        }
    }
}
