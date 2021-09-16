# Order_Payment_Stock_Saga_Pattern
Saga pattern with Java => order -> payment -> stock microservices are ready to use

## Docker-compose.yaml

You can see that rabbitmq is necessary for message broker.

Orchestration-based saga is used for implementation.

### How to run

git clone git@github.com:grkn/Order_Payment_Stock_Saga_Pattern.git
Open IDE from your computer
Import Project
Run Application.java for each services (I didn't write startup script)

There is only one endpoint for creating order.

```
POST http://localhost:8089/saga/v1/order
[
    {
        "name" : "order1",
        "quantity": 2
    },
    {
        "name" : "order2",
        "quantity": 2
    },
    {
        "name" : "order3",
        "quantity": 1
    }
]
```

Of course there are some failure cases

Buy operation is dump and sometime gives error.

Also stock is limited maybe you can not buy necessary amount.

There are static order names : order1, order2, order3, order4, order5. Other names will give an error

Database are h2 database and each database has one table.
You can easily check status from database.


### Additional Information : 


User sends order request to ORDER service.
ORDER service sends 2 amqp message to PAYMENT service and STOCK service
STOCK service sends related amqp message to ORDER service and PAYMENT service
PAYMENT service sends success or fail to ORDER service.

Statuses that are related with Order, Payment, Stock

```
ORDER_RECEIVED, ORDER_COMPLETED, ORDER_PENDING, ORDER_FAILED, ORDER_STOCK_COMPLETED

PAYMENT_REQUESTED, PAYMENT_PENDING, PAYMENT_COMPLETED, PAYMENT_FAILED, PAYMENT_AVAILABLE

STOCK_REQUESTED, STOCK_COMPLETED, STOCK_FAILED, STOCK_PENDING
```

Each service listens the related queue.

Exchange -> sagaExchange

Order service -> orderQueue -> orderDto is payload
Payment service -> paymentQueue -> paymentDto is payload
Stock service -> stockQueue -> stockDto is payload


![image](https://user-images.githubusercontent.com/5938655/133602197-bf7704e6-357b-49a3-8616-38d1d37e365a.png)




