# [webflux, mongodb and nginx](https://github.com/jonasperez/webflux-mongodb-nginx.git)

## What you will build ?

- A modern Java Reactive Application with WebFux, Reactor Netty Server, MongoDB NoSQL and NGINX, all of this running inside Docker containers

## Stack
- Docker
- Java 1.8
- Spring Boot 2.0.4
- WebFlux 2.0.4
- Reactor Netty Server
- MongoDB 4.0.9
- Nginx 1.13
- Maven 3.5

## Run
- Run command 
```
docker-compose up
```
- Access to http://localhost/health
- If the result was **{"status":"up"}**, congratulations, everything worked fine !!!


# Accounts

## POST http://localhost/v1/accounts
##### Payload Example
```
{
    "available_credit_limit" : {
        "amount": 100.00
    },
    "available_withdrawal_limit" : {
        "amount": 100.00
    }
}
```
##### Curl
```
curl --data '{"available_credit_limit" : {"amount": 100.00},"available_withdrawal_limit" : {"amount": 100.00}}' -v -X POST -H 'Content-Type:application/json' http://localhost/v1/accounts
```



## GET http://localhost/v1/accounts/limits

##### curl
```
curl GET http://localhost/v1/accounts/limits
```



## PATCH http://localhost/v1/accounts/{accountId}
##### Obs.:
- [x] Use **Negative** values: **To Subtract**
- [x] Use **Positive** values: **To Plus**

##### Payload Example
```
{
    "available_credit_limit" : {
        "amount": -50.00
    },
    "available_withdrawal_limit" : {

    }
}
```
##### Curl
```
curl --data '{"available_credit_limit" : {"amount": -50.00}}' -v -X PATCH -H 'Content-Type:application/json' http://localhost/v1/accounts/{accountId}
```

# Transactions
##### Operations Type 
- [x] 1 - CASH PAYMENT
- [x] 2 - DEFERRED PAYMENT
- [x] 3 - WITHDRAWAL

## POST http://localhost/v1/transactions
##### Payload Example
```
{
    "account_id": "{accountId}", 
    "operation_type_id": 1, 
    "amount": -18.7
}
```
```
{
    "account_id": "{accountId}", 
    "operation_type_id": 2, 
    "amount": -23.5
}
```
```
{
    "account_id": "{accountId}",
    "operation_type_id": 3, 
    "amount": -50.0
}
```
##### Curl
```
curl --data '{"account_id": "{accountId}", "operation_type_id": 1, "amount": -18.7}' -v -X POST -H 'Content-Type:application/json' http://localhost/v1/transactions
curl --data '{"account_id": "{accountId}", "operation_type_id": 2, "amount": -23.5}' -v -X POST -H 'Content-Type:application/json' http://localhost/v1/transactions
curl --data '{"account_id": "{accountId}", "operation_type_id": 3, "amount": -50.0}' -v -X POST -H 'Content-Type:application/json' http://localhost/v1/transactions
```

## GET http://localhost/v1/transactions
##### Curl
```
curl http://localhost/v1/transactions
```

## GET http://localhost/v1/transactions/account/{accountId}
##### Curl
```
curl http://localhost/v1/transactions/account/{accountId}
```

# Payments
##### Implicit Operation Type 
- [x] 4 - PAYMENT


## POST http://localhost/v1/payments
##### Payload Example
```
[
    {
        "account_id":"{accountId}", 
        "amount": 70.0
    }
]
```
##### Curl
```
curl --data '[{"account_id":"{accountId}", "amount": 70.0}]' -v -X POST -H 'Content-Type:application/json' http://localhost/v1/payments
```

## GET http://localhost/v1/payments/account/{accountId}
##### Curl
```
curl http://localhost/v1/payments/account/{accountId}
```
