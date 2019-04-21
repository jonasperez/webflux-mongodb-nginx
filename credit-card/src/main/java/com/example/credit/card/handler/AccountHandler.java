package com.example.credit.card.handler;

import com.example.credit.card.dto.account.AccountRequest;
import com.example.credit.card.model.Account;
import com.example.credit.card.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.example.credit.card.dto.MessageErrorResponse.buildResponse;
import static org.springframework.http.HttpStatus.PRECONDITION_REQUIRED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class AccountHandler {

    @Autowired
    private AccountService accountService;

    public Mono<ServerResponse> save(ServerRequest request){
        String accountId = request.pathVariable("id");
        return accountService.findById(accountId).flatMap(account ->
            request.bodyToMono(AccountRequest.class).flatMap(accountRequest ->
                accountService.save(account, accountRequest)
                        .flatMap(accountResponse -> ok().contentType(APPLICATION_JSON).body(fromObject(accountResponse)))
            ).switchIfEmpty(noContent().build()));
    }

    public Mono<ServerResponse> limits(ServerRequest request){
        Flux accounts = accountService.findAll();
        return ok().contentType(APPLICATION_JSON).body(accounts, Account.class);
    }

    public Mono<ServerResponse> insert(ServerRequest request){
        Mono<AccountRequest> monoAccountRequest = request.bodyToMono(AccountRequest.class);
        return monoAccountRequest.flatMap(accountRequest ->
            accountService.insert(accountRequest)
                .flatMap(accountResponse -> ok().contentType(APPLICATION_JSON).body(fromObject(accountResponse)))
        )
        .onErrorResume(error -> buildResponse(PRECONDITION_REQUIRED, error.getMessage()))
        .switchIfEmpty(noContent().build());
    }

}
