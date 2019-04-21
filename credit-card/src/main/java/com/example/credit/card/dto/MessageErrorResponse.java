package com.example.credit.card.dto;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

public class MessageErrorResponse {

    public static Mono<ServerResponse> buildResponse(HttpStatus httpStatus, String message){
        return status(httpStatus)
            .contentType(APPLICATION_JSON)
            .body( fromObject( new MessageErrorResponse(httpStatus, message ) ) );
    }

    private int status;
    private String error;
    private String message;

    private MessageErrorResponse(HttpStatus httpStatus, String message) {
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
