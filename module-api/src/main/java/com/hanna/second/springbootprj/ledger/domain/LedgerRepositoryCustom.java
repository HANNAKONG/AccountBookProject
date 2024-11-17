package com.hanna.second.springbootprj.ledger.domain;

import com.hanna.second.springbootprj.ledger.dto.LedgerRequestDto;
import com.hanna.second.springbootprj.support.Money;
import com.hanna.second.springbootprj.support.enums.PeriodType;
import com.hanna.second.springbootprj.support.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LedgerRepositoryCustom {
    Page<Ledger> findAllByFilter(LedgerRequestDto requestDto, Pageable pageable);
    List<Ledger> findCategoryAndAmount(String baseDate, Long usersId, TransactionType transactionType);
    List<Ledger> findWeeklyCategoryAndAmount(String baseDate, Long usersId, TransactionType transactionType);
    List<Ledger> findMonthlyCategoryAndAmount(String baseDate, Long usersId, TransactionType transactionType);
    BigDecimal findAmountSum(String baseDate, Long usersId, TransactionType transactionType);
    BigDecimal findWeeklyAmountSum(String baseDate, Long usersId, TransactionType transactionType);
    BigDecimal findMonthlyAmountSum(String baseDate, Long usersId, TransactionType transactionType);

}
