package com.example.credit.card;

import com.example.credit.card.model.Operation;
import com.example.credit.card.model.OperationType;
import com.example.credit.card.repository.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;

@Component
public class InitialLoad {

    @Autowired
    private OperationRepository operationRepository;

    @PostConstruct
    public void initialLoad(){
        operationRepository.saveAll(
            Flux.just(
                new Operation(OperationType.CASH_PAYMENT),
                new Operation(OperationType.DEFERRED_PAYMENT),
                new Operation(OperationType.WITHDRAWAL),
                new Operation(OperationType.PAYMENT)
            )
        ).subscribe();
    }
}
