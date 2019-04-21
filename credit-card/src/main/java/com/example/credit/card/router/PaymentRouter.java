package com.example.credit.card.router;

import com.example.credit.card.handler.PaymentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class PaymentRouter {

    @Autowired
    private PaymentHandler handler;

    @Bean
    public RouterFunction<ServerResponse> paymentRoute(){
        return route(
                POST("/v1/payments").and( accept(APPLICATION_JSON) ), handler::payments)
            .andRoute(
                GET("/v1/payments/account/{id}"), handler::paymentsByAccount);
    }
}
