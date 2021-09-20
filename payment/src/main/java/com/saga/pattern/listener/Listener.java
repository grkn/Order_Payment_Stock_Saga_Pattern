package com.saga.pattern.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saga.pattern.constant.ApplicationConstant;
import com.saga.pattern.constant.PaymentStatus;
import com.saga.pattern.dto.PaymentDto;
import com.saga.pattern.sender.Sender;
import com.saga.pattern.service.PaymentService;
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
    private final PaymentService paymentService;
    private final Sender sender;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = ApplicationConstant.PAYMENT_QUEUE, durable = "true"),
            exchange = @Exchange(value = ApplicationConstant.EXCHANGE, ignoreDeclarationExceptions = "true"),
            key = ApplicationConstant.PAYMENT_ROUTING_KEY)
    )
    public void listen(Message message) throws IOException {
        PaymentDto paymentDto = objectMapper.readValue(message.getBody(), PaymentDto.class);
        assert paymentDto.getStatus() != null;
        PaymentStatus paymentStatus = PaymentStatus.valueOf(paymentDto.getStatus());
        LOGGER.info("Payment {} message is received. TransactionId :  {}", paymentStatus.name(), paymentDto.getTransactionId());
        switch (paymentStatus) {
            case PAYMENT_REQUESTED:
                paymentService.createPayment(paymentDto);
                break;
            case PAYMENT_PENDING:
            case PAYMENT_FAILED:
                paymentService.updateStatus(paymentDto.getTransactionId(), paymentStatus);
                break;
            case PAYMENT_AVAILABLE:
                paymentService.buyFrom3rdPartyApp(paymentDto);
                break;
            default:
                break;
        }
    }
}
