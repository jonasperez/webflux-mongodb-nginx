package com.example.credit.card.handler;

import com.example.credit.card.dto.transaction.TransactionRequest;
import com.example.credit.card.model.OperationType;
import com.example.credit.card.model.Transaction;
import com.example.credit.card.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.example.credit.card.dto.MessageErrorResponse.buildResponse;
import static java.math.BigDecimal.ZERO;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class TransactionHandler {

    @Autowired
    private TransactionService transactionService;

    public Mono<ServerResponse> transaction(ServerRequest request){
        return request.bodyToMono(TransactionRequest.class).flatMap( transactionRequest -> {
            OperationType operationType = OperationType.getById(transactionRequest.getOperationTypeId());

            if (operationType == null) return buildResponse(NOT_IMPLEMENTED, "Operation Not Implemented");
            if (transactionRequest.getAmount().compareTo(ZERO) == 0) return buildResponse(PRECONDITION_REQUIRED, "amount is required");
            if (transactionRequest.getAmount().compareTo(ZERO) > 0) return buildResponse(PRECONDITION_REQUIRED, "amount need be a negative value");

            switch (operationType){
                case PAYMENT:
                    return buildResponse(NOT_IMPLEMENTED, "Please, use POST /v1/payments instead /v1/transactions to do payments");
                default:
                    return transactionService.insert(transactionRequest)
                        .flatMap(transactionResponse -> ok().contentType(APPLICATION_JSON).body(fromObject(transactionResponse)))
                        .onErrorResume(error -> buildResponse(UNAUTHORIZED, error.getMessage()))
                        .switchIfEmpty(buildResponse(NOT_FOUND, String.format("account_id: %s is Not Found", transactionRequest.getAccountId())));
            }
        });
    }

    public Mono<ServerResponse> transactions(ServerRequest request){
        Flux<Transaction> transactionFlux = transactionService.findAllOrdered();
        return ok().contentType(APPLICATION_JSON).body(transactionFlux, Transaction.class);
    }

    public Mono<ServerResponse> transactionsByAccount(ServerRequest request){
        String accountId = request.pathVariable("id");
        Flux<Transaction> transactionFlux = transactionService.findTransactionsdByAccountOrdered(accountId);
        return ok().contentType(APPLICATION_JSON).body(transactionFlux, Transaction.class);
    }

}
