package com.hanna.second.springbootprj.support.enums;

public enum TransactionType {
    INCOME,
    EXPENSE;

    public String getTransactionType() {
        return name();
    }
}
