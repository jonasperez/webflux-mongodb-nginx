package com.example.credit.card.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Operation {

    @Id
    private int operationTypeId;
    private String description;
    private int chargeOrder;

    public Operation(){

    }

    public Operation(OperationType operationType){
        this.operationTypeId = operationType.getOperationTypeId();
        this.description = operationType.getDescription();
        this.chargeOrder = operationType.getChargeOrder();
    }

    public int getOperationTypeId() {
        return operationTypeId;
    }

    public void setOperationTypeId(int operationTypeId) {
        this.operationTypeId = operationTypeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getChargeOrder() {
        return chargeOrder;
    }

    public void setChargeOrder(int chargeOrder) {
        this.chargeOrder = chargeOrder;
    }
}
