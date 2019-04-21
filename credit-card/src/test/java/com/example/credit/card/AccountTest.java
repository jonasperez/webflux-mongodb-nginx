package com.example.credit.card;

import com.example.credit.card.dto.account.AccountRequest;
import com.example.credit.card.dto.account.AvailableCreditLimit;
import com.example.credit.card.dto.account.AvailableWithdrawalLimit;
import com.example.credit.card.model.Account;
import com.example.credit.card.service.AccountService;
import com.example.credit.card.utils.BigDecimalUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.HttpStatus.PRECONDITION_REQUIRED;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class)
public class AccountTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private AccountService accountService;

    private WebTestClient client;

    @Before
    public void buildClient(){
        client = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    public void accounts_post_test() {
        AccountRequest request = new AccountRequest(new AvailableCreditLimit(5000.00), new AvailableWithdrawalLimit(5000.00));

        client.post()
                .uri("/v1/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), AccountRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Account.class)
                .consumeWith(response -> {
                    Account account = response.getResponseBody();

                    Assertions.assertThat(account.getAvailableCreditLimit())
                            .isEqualTo(new BigDecimal(5000.00).setScale(2, BigDecimal.ROUND_HALF_EVEN));

                    Assertions.assertThat(account.getAvailableWithdrawalLimit())
                            .isEqualTo(new BigDecimal(5000.00).setScale(2, BigDecimal.ROUND_HALF_EVEN));
                });
    }

    @Test
    public void accounts_patch_test(){
        Account newAccount = new Account(5000.00, 5000.00);
        Account account = accountService.insert(newAccount).block();

        AccountRequest request = new AccountRequest();
        request.setAvailableCreditLimit(new AvailableCreditLimit(new BigDecimal(-2000.13)));
        request.setAvailableWithdrawalLimit(new AvailableWithdrawalLimit(new BigDecimal(-3000.123456)));

        client.patch()
            .uri(String.format("/v1/accounts/%s", account.getAccountId()))
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), AccountRequest.class)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Account.class)
            .consumeWith(response -> {
               Account accountResponse = response.getResponseBody();

                Assertions.assertThat(accountResponse.getAccountId())
                        .isEqualTo(account.getAccountId());

                Assertions.assertThat(accountResponse.getAvailableCreditLimit())
                        .isEqualTo(BigDecimalUtils.build(2999.87));

                Assertions.assertThat(accountResponse.getAvailableWithdrawalLimit())
                        .isEqualTo(BigDecimalUtils.build(1999.88));
            });

    }

    @Test
    public void accounts_limits_get_test(){
        AccountRequest request = new AccountRequest();
        request.setAvailableCreditLimit(new AvailableCreditLimit(new BigDecimal(5000)));
        request.setAvailableWithdrawalLimit(new AvailableWithdrawalLimit(new BigDecimal(5000)));

        accountService.insert(request).subscribe();

        client.get()
                .uri("/v1/accounts/limits")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Account.class)
                .consumeWith(response -> {
                    List<Account> accountResponse = response.getResponseBody();
                    Account account = accountResponse.get(0);

                    Assertions.assertThat(account.getAvailableCreditLimit())
                            .isEqualByComparingTo(new BigDecimal(5000));

                    Assertions.assertThat(account.getAvailableWithdrawalLimit())
                            .isEqualByComparingTo(new BigDecimal(5000));

                });
    }

    @Test
    public void available_credit_limit_is_required_test(){
        AccountRequest request = new AccountRequest();
        request.setAvailableCreditLimit(new AvailableCreditLimit());
        request.setAvailableWithdrawalLimit(new AvailableWithdrawalLimit(new BigDecimal(5000)));

        client.post()
                .uri("/v1/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), AccountRequest.class)
                .exchange()
                .expectStatus().isEqualTo(PRECONDITION_REQUIRED)
                .expectBody()
                .jsonPath("$.status").isEqualTo(PRECONDITION_REQUIRED.value())
                .jsonPath("$.error").isEqualTo(PRECONDITION_REQUIRED.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Available Credit Limit Is Required");
    }

    @Test
    public void available_withdrawal_limit_is_required_test(){

        AccountRequest request = new AccountRequest();
        request.setAvailableCreditLimit(new AvailableCreditLimit(new BigDecimal(5000)));
        request.setAvailableWithdrawalLimit(new AvailableWithdrawalLimit());

        client.post()
                .uri("/v1/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), AccountRequest.class)
                .exchange()
                .expectStatus().isEqualTo(PRECONDITION_REQUIRED)
                .expectBody()
                .jsonPath("$.status").isEqualTo(PRECONDITION_REQUIRED.value())
                .jsonPath("$.error").isEqualTo(PRECONDITION_REQUIRED.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Available Withdrawal Limit Is Required");
    }


}
