package com.example.credit.card;

import com.example.credit.card.dto.transaction.TransactionRequest;
import com.example.credit.card.model.Account;
import com.example.credit.card.model.OperationType;
import com.example.credit.card.repository.AccountRepository;
import com.example.credit.card.repository.TransactionRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static com.example.credit.card.model.OperationType.*;
import static org.springframework.http.HttpStatus.*;
import static  org.springframework.http.MediaType.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class)
public class TransactionTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private WebTestClient client;

    @Before
    public void buildClient(){
        client = WebTestClient.bindToApplicationContext(context).build();
    }


    @Test
    public void transaction_account_id_not_found_test(){
        TransactionRequest request = new TransactionRequest("0000000000", CASH_PAYMENT, new BigDecimal(-300));

        client.post()
                .uri("/v1/transactions")
                .accept(APPLICATION_JSON)
                .body(Mono.just(request), TransactionRequest.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(NOT_FOUND.value())
                .jsonPath("$.error").isEqualTo(NOT_FOUND.getReasonPhrase())
                .jsonPath("$.message").isEqualTo(String.format("account_id: %s is Not Found", request.getAccountId()));

    }

    @Test
    public void transaction_payment_not_implemented_test(){
        Account account = accountRepository.insert(new Account(new BigDecimal(5000.0), new BigDecimal(5000.0))).block();
        TransactionRequest request = new TransactionRequest();
        request.setAccountId(account.getAccountId());
        request.setAmount(new BigDecimal(-300));
        request.setOperationTypeId(OperationType.PAYMENT.getOperationTypeId());

        client.post()
                .uri("/v1/transactions")
                .accept(APPLICATION_JSON)
                .body(Mono.just(request), TransactionRequest.class)
                .exchange()
                .expectStatus().isEqualTo(NOT_IMPLEMENTED)
                .expectBody()
                .jsonPath("$.status").isEqualTo(NOT_IMPLEMENTED.value())
                .jsonPath("$.error").isEqualTo(NOT_IMPLEMENTED.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Please, use POST /v1/payments instead /v1/transactions to do payments");
    }

    @Test
    public void transaction_operation_not_implemented_test(){
        Account account = accountRepository.insert(new Account(new BigDecimal(5000.0), new BigDecimal(5000.0))).block();
        TransactionRequest request = new TransactionRequest();
        request.setAccountId(account.getAccountId());
        request.setOperationTypeId(5);

        client.post()
                .uri("/v1/transactions")
                .accept(APPLICATION_JSON)
                .body(Mono.just(request), TransactionRequest.class)
                .exchange()
                .expectStatus().isEqualTo(NOT_IMPLEMENTED)
                .expectBody()
                .jsonPath("$.status").isEqualTo(NOT_IMPLEMENTED.value())
                .jsonPath("$.error").isEqualTo(NOT_IMPLEMENTED.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Operation Not Implemented");
    }

    @Test
    public void transaction_amount_is_required_test(){
        Account account = accountRepository.insert(new Account(new BigDecimal(5000.0), new BigDecimal(5000.0))).block();
        TransactionRequest request = new TransactionRequest();
        request.setAccountId(account.getAccountId());
        request.setOperationTypeId(OperationType.CASH_PAYMENT.getOperationTypeId());
        request.setAmount(null);

        client.post()
                .uri("/v1/transactions")
                .accept(APPLICATION_JSON)
                .body(Mono.just(request), TransactionRequest.class)
                .exchange()
                .expectStatus().isEqualTo(PRECONDITION_REQUIRED)
                .expectBody()
                .jsonPath("$.status").isEqualTo(PRECONDITION_REQUIRED.value())
                .jsonPath("$.error").isEqualTo(PRECONDITION_REQUIRED.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("amount is required");
    }

    @Test
    public void transaction_amount_need_be_a_negative_value_test(){
        Account account = accountRepository.insert(new Account(new BigDecimal(5000.0), new BigDecimal(5000.0))).block();
        TransactionRequest request = new TransactionRequest();
        request.setAccountId(account.getAccountId());
        request.setOperationTypeId(CASH_PAYMENT.getOperationTypeId());
        request.setAmount(new BigDecimal(300));

        client.post()
                .uri("/v1/transactions")
                .accept(APPLICATION_JSON)
                .body(Mono.just(request), TransactionRequest.class)
                .exchange()
                .expectStatus().isEqualTo(PRECONDITION_REQUIRED)
                .expectBody()
                .jsonPath("$.status").isEqualTo(PRECONDITION_REQUIRED.value())
                .jsonPath("$.error").isEqualTo(PRECONDITION_REQUIRED.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("amount need be a negative value");
    }

    @Test
    public void transaction_available_credit_limit_exceeded_test(){
        Account account = accountRepository.insert(new Account(new BigDecimal(5000.00), new BigDecimal(0))).block();
        TransactionRequest request = new TransactionRequest();
        request.setAccountId(account.getAccountId());
        request.setOperationTypeId(CASH_PAYMENT.getOperationTypeId());
        request.setAmount(new BigDecimal(-5000.01));

        client.post()
                .uri("/v1/transactions")
                .accept(APPLICATION_JSON)
                .body(Mono.just(request), TransactionRequest.class)
                .exchange()
                .expectStatus().isEqualTo(UNAUTHORIZED)
                .expectBody()
                .jsonPath("$.status").isEqualTo(UNAUTHORIZED.value())
                .jsonPath("$.error").isEqualTo(UNAUTHORIZED.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Available Credit Limit Exceeded");
    }

    @Test
    public void transaction_available_withdrawal_limit_exceeded_test(){
        Account account = accountRepository.insert(new Account(new BigDecimal(0), new BigDecimal(5000.00))).block();
        TransactionRequest request = new TransactionRequest();
        request.setAccountId(account.getAccountId());
        request.setOperationTypeId(WITHDRAWAL.getOperationTypeId());
        request.setAmount(new BigDecimal(-5000.01));

        client.post()
                .uri("/v1/transactions")
                .accept(APPLICATION_JSON)
                .body(Mono.just(request), TransactionRequest.class)
                .exchange()
                .expectStatus().isEqualTo(UNAUTHORIZED)
                .expectBody()
                .jsonPath("$.status").isEqualTo(UNAUTHORIZED.value())
                .jsonPath("$.error").isEqualTo(UNAUTHORIZED.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Available Withdrawal Limit Exceeded");
    }


}
