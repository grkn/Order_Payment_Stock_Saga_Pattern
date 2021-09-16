package com.saga.pattern.entity;

import com.saga.pattern.constant.PaymentStatus;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String transactionId;

    @Enumerated(value = EnumType.STRING)
    private PaymentStatus status;

    private Double totalPrice;


}
