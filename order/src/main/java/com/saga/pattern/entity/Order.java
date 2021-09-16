package com.saga.pattern.entity;

import com.saga.pattern.constant.OrderStatus;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "order_entity")
public class Order {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String name;

    private String transactionId;

    private LocalDateTime created;

    private String paymentId;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
