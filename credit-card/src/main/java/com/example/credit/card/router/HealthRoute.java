package com.example.credit.card.router;

import com.example.credit.card.handler.HealthHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class HealthRoute {

    @Autowired
    private HealthHandler handler;

    @Bean
    public RouterFunction<ServerResponse> healthCheckRoute(){
        return route(GET("/health"), handler::health);
    }


}
