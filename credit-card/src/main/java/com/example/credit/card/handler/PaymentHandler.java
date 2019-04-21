package com.example.credit.card.handler;

import com.example.credit.card.dto.payment.PaymentRequest;
import com.example.credit.card.model.Transaction;
import com.example.credit.card.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class PaymentHandler {

    @Autowired
    private TransactionService transactionService;

    public Mono<ServerResponse> payments(ServerRequest request){
        final Flux<PaymentRequest> fluxPaymentRequest = request.bodyToFlux(PaymentRequest.class);
        Flux.zip(Flux.interval(Duration.ofMillis(500)), fluxPaymentRequest).map(Tuple2::getT2)
                .flatMap(paymentRequest -> transactionService.doPayment(paymentRequest)).subscribe();
        return ok().contentType(TEXT_PLAIN).body(BodyInserters.fromObject("SUCCESS"));
    }

    public Mono<ServerResponse> paymentsByAccount(ServerRequest request){
        String accountId = request.pathVariable("id");
        Flux<Transaction> paymentFlux = transactionService.findPaymentsByAccount(accountId);
        return ok().contentType(APPLICATION_JSON).body(paymentFlux, Transaction.class);
    }

}
