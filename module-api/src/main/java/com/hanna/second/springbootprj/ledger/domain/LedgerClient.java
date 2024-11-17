package com.hanna.second.springbootprj.ledger.domain;

import com.hanna.second.springbootprj.support.enums.TransactionType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LedgerClient {
    Optional<Ledger> findById(Long ledgerId);
    List<Ledger> findCategoryAndAmount(String baseDate, Long usersId, TransactionType transactionType);
    List<Ledger> findWeeklyCategoryAndAmount(String baseDate, Long usersId, TransactionType transactionType);
    List<Ledger> findMonthlyCategoryAndAmount(String baseDate, Long usersId, TransactionType transactionType);
    BigDecimal findAmountSum(String baseDate, Long usersId, TransactionType transactionType);
    BigDecimal findWeeklyAmountSum(String baseDate, Long usersId, TransactionType transactionType);
    BigDecimal findMonthlyAmountSum(String baseDate, Long usersId, TransactionType transactionType);
}
