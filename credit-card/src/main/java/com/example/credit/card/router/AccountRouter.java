package com.example.credit.card.router;

import com.example.credit.card.handler.AccountHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import static org.springframework.web.reactive.function.server.RouterFunctions.*;

@Configuration
public class AccountRouter {

    @Autowired
    private AccountHandler handler;

    @Bean
    public RouterFunction<ServerResponse> accountRoute(){
        return route( PATCH("/v1/accounts/{id}").and( accept(APPLICATION_JSON) ), handler::save )
                .andRoute( GET("/v1/accounts/limits"), handler::limits )
                .andRoute( POST("/v1/accounts"), handler::insert );
    }
}
