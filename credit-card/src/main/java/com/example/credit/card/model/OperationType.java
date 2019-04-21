package com.example.credit.card.model;

public enum OperationType {

    CASH_PAYMENT(1, "CASH PAYMENT", 2),
    DEFERRED_PAYMENT(2, "DEFERRED PAYMENT", 1),
    WITHDRAWAL(3, "WITHDRAWAL", 0),
    PAYMENT(4, "PAYMENT", 0);

    private int operationTypeId;
    private String description;
    private int chargeOrder;

    OperationType(int operationTypeId, String description, int chargeOrder){
        this.operationTypeId = operationTypeId;
        this.description = description;
        this.chargeOrder = chargeOrder;
    }

    public static OperationType getById(int id){
        OperationType operationType = null;
        for (OperationType operation : values()){
            if (operation.operationTypeId == id){
                operationType = operation;
                break;
            }
        }
        return operationType;
    }

    public int getOperationTypeId() {
        return operationTypeId;
    }

    public String getDescription() {
        return description;
    }

    public int getChargeOrder() {
        return chargeOrder;
    }
}
