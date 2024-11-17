package com.hanna.second.springbootprj.ledger.domain;

import com.hanna.second.springbootprj.ledger.infra.LedgerJpaRepository;
import com.hanna.second.springbootprj.support.enums.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class LedgerClientImpl implements LedgerClient {
    private final LedgerJpaRepository ledgerRepository;

    public LedgerClientImpl(LedgerJpaRepository ledgerRepository) {
        this.ledgerRepository = ledgerRepository;
    }

    @Override
    public Optional<Ledger> findById(Long ledgerId) {
        return ledgerRepository.findById(ledgerId);
    }

    @Override
    public List<Ledger> findCategoryAndAmount(String baseDate, Long usersId, TransactionType transactionType) {
        return ledgerRepository.findCategoryAndAmount(baseDate, usersId, transactionType);
    }

    @Override
    public List<Ledger> findWeeklyCategoryAndAmount(String baseDate, Long usersId, TransactionType transactionType) {
        return ledgerRepository.findWeeklyCategoryAndAmount(baseDate, usersId, transactionType);
    }

    @Override
    public List<Ledger> findMonthlyCategoryAndAmount(String baseDate, Long usersId, TransactionType transactionType) {
        return ledgerRepository.findMonthlyCategoryAndAmount(baseDate, usersId, transactionType);
    }

    @Override
    public BigDecimal findAmountSum(String baseDate, Long usersId, TransactionType transactionType) {
        return Objects.requireNonNullElse(ledgerRepository.findAmountSum(baseDate, usersId, transactionType), BigDecimal.ZERO);
    }

    @Override
    public BigDecimal findWeeklyAmountSum(String baseDate, Long usersId, TransactionType transactionType) {
        return Objects.requireNonNullElse(ledgerRepository.findWeeklyAmountSum(baseDate, usersId, transactionType), BigDecimal.ZERO);
    }

    @Override
    public BigDecimal findMonthlyAmountSum(String baseDate, Long usersId, TransactionType transactionType) {
        return Objects.requireNonNullElse(ledgerRepository.findMonthlyAmountSum(baseDate, usersId, transactionType), BigDecimal.ZERO);
    }
}
