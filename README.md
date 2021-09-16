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

POST http://localhost:8089/saga/v1/order

```
[
    {
        "name" : "order1",
        "quantity": 2
    },
    {
        "name" : "order2",
        "quantity": 9
    },
    {
        "name" : "order3",
        "quantity": 1
    }
]
```

