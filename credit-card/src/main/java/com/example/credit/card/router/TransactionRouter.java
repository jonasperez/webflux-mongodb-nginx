package com.example.credit.card.router;

import com.example.credit.card.handler.TransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class TransactionRouter {

    @Autowired
    private TransactionHandler handler;

    @Bean
    public RouterFunction<ServerResponse> transactionRoute(){
        return route(
                POST("/v1/transactions").and( accept(APPLICATION_JSON) ), handler::transaction )
            .andRoute(
                GET("/v1/transactions"), handler::transactions )
            .andRoute(
                GET("/v1/transactions/account/{id}"), handler::transactionsByAccount );
    }
}
