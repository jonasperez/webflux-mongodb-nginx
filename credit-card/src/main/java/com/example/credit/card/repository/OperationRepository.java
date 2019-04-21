package com.example.credit.card.repository;

import com.example.credit.card.model.Operation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationRepository extends ReactiveMongoRepository<Operation, Integer> { }
