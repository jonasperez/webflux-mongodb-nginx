package com.example.credit.card.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.BodyInserters.*;

@Component
public class HealthHandler {

    public Mono<ServerResponse> health(ServerRequest request){
        return ok().contentType(APPLICATION_JSON).body(fromObject("{\"status\":\"up\"}"));
    }
}
