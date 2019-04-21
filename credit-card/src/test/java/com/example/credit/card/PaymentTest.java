package com.example.credit.card;

import com.example.credit.card.dto.payment.PaymentRequest;
import com.example.credit.card.model.Account;
import com.example.credit.card.model.Transaction;
import com.example.credit.card.repository.AccountRepository;
import com.example.credit.card.repository.TransactionRepository;
import com.example.credit.card.service.TransactionService;
import com.example.credit.card.utils.BigDecimalUtils;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static com.example.credit.card.model.OperationType.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class)
public class PaymentTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    private WebTestClient client;

    @Before
    public void buildClient(){
        client = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    public void do_payments1_test() throws Exception {
        Account account = accountRepository.insert(new Account(new BigDecimal(5000.0), new BigDecimal(5000.0))).block();

        Flux<Transaction> transactionFlux = Flux.just(
            new Transaction(account, CASH_PAYMENT,
                new BigDecimal(-18.70),
                new BigDecimal(-18.70)
            ),
            new Transaction(account, DEFERRED_PAYMENT,
                new BigDecimal(-23.50),
                new BigDecimal(-23.50)
            ),
            new Transaction(account, WITHDRAWAL,
                new BigDecimal(-50.00),
                new BigDecimal(-50.00)
            )
        );

        transactionRepository.insert(transactionFlux).blockLast();

        Flux<PaymentRequest> paymentFlux = Flux.just(new PaymentRequest(account.getAccountId(), new BigDecimal(70.00)));

        client.post()
            .uri("/v1/payments")
            .contentType(APPLICATION_JSON)
            .body(paymentFlux, PaymentRequest.class)
            .exchange()
            .expectHeader().contentType(MediaType.TEXT_PLAIN)
            .expectStatus().isOk()
            .expectBody().consumeWith(response -> {

                Assertions.assertThat(new String(response.getResponseBody()))
                        .isEqualTo("SUCCESS");

                Mono.delay(Duration.ofSeconds(2)).block();

                List<Transaction> transactionList = transactionService
                        .findTransactionsdByAccountOrdered(account)
                            .collectList().block();

                Transaction transaction1 = transactionList.get(0);
                Assertions.assertThat(transaction1.getAmount())
                        .isEqualByComparingTo(BigDecimalUtils.build(-50.00));

                Assertions.assertThat(transaction1.getBalance())
                        .isEqualByComparingTo(BigDecimal.ZERO);


                Transaction transaction2 = transactionList.get(1);
                Assertions.assertThat(transaction2.getAmount())
                        .isEqualByComparingTo(BigDecimalUtils.build(-23.50));

                Assertions.assertThat(transaction2.getBalance())
                        .isEqualByComparingTo(BigDecimalUtils.build(-3.50));


                Transaction transaction3 = transactionList.get(2);
                Assertions.assertThat(transaction3.getAmount())
                        .isEqualByComparingTo(BigDecimalUtils.build(-18.70));

                Assertions.assertThat(transaction3.getBalance())
                        .isEqualByComparingTo(BigDecimalUtils.build(-18.70));


                List<Transaction> paymentList = transactionService
                            .findPaymentsByAccount(account).collectList().block();

                Transaction payment = paymentList.get(0);
                Assertions.assertThat(payment.getAmount())
                        .isEqualByComparingTo(BigDecimalUtils.build(70.0));

                Assertions.assertThat(payment.getBalance())
                        .isEqualByComparingTo(BigDecimal.ZERO);

        });
    }

    @Test
    public void do_payments2_test() {
        Account account = accountRepository.insert(new Account(new BigDecimal(5000.0), new BigDecimal(5000.0))).block();

        Flux<Transaction> transactionFlux = Flux.just(
            new Transaction(account, CASH_PAYMENT,
                new BigDecimal(-20.00),
                new BigDecimal(-20.00)
            ),
            new Transaction(account, DEFERRED_PAYMENT,
                new BigDecimal(-30.00),
                new BigDecimal(-30.00)
            ),
            new Transaction(account, WITHDRAWAL,
                new BigDecimal(-50.00),
                new BigDecimal(-50.00)
            )
        );

        transactionRepository.insert(transactionFlux).blockLast();

        Flux<PaymentRequest> paymentFlux = Flux.just(
            new PaymentRequest(account.getAccountId(), new BigDecimal(150.00)),
            new PaymentRequest(account.getAccountId(), new BigDecimal(50.00))
        );

        client.post()
                .uri("/v1/payments")
                .contentType(APPLICATION_JSON)
                .body(paymentFlux, PaymentRequest.class)
                .exchange()
                .expectHeader().contentType(MediaType.TEXT_PLAIN)
                .expectStatus().isOk()
                .expectBody().consumeWith(response -> {

                    Assertions.assertThat(new String(response.getResponseBody()))
                            .isEqualTo("SUCCESS");

                    Mono.delay(Duration.ofSeconds(3)).block();

                    List<Transaction> transactionList = transactionService
                            .findTransactionsdByAccountOrdered(account)
                                .collectList().block();

                    Transaction transaction1 = transactionList.get(0);
                    Assertions.assertThat(transaction1.getAmount())
                            .isEqualByComparingTo(BigDecimalUtils.build(-50.00));

                    Assertions.assertThat(transaction1.getBalance())
                            .isEqualByComparingTo(BigDecimal.ZERO);


                    Transaction transaction2 = transactionList.get(1);
                    Assertions.assertThat(transaction2.getAmount())
                            .isEqualByComparingTo(BigDecimalUtils.build(-30.00));

                    Assertions.assertThat(transaction2.getBalance())
                            .isEqualByComparingTo(BigDecimal.ZERO);


                    Transaction transaction3 = transactionList.get(2);
                    Assertions.assertThat(transaction3.getAmount())
                            .isEqualByComparingTo(BigDecimalUtils.build(-20.00));

                    Assertions.assertThat(transaction3.getBalance())
                            .isEqualByComparingTo(BigDecimal.ZERO);


                    List<Transaction> paymentList = transactionService
                            .findPaymentsByAccount(account).collectList().block();

                    Transaction payment1 = paymentList.get(0);
                    Assertions.assertThat(payment1.getAmount())
                            .isEqualByComparingTo(BigDecimalUtils.build(150.00));

                    Assertions.assertThat(payment1.getBalance())
                            .isEqualByComparingTo(BigDecimalUtils.build(50.00));

                    Transaction payment2 = paymentList.get(1);
                    Assertions.assertThat(payment2.getAmount())
                            .isEqualByComparingTo(BigDecimalUtils.build(50.00));

                    Assertions.assertThat(payment2.getBalance())
                            .isEqualByComparingTo(BigDecimalUtils.build(50.00));
            });
    }

}
