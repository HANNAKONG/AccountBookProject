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
    Page<Ledger> findCustomCriteria(LedgerRequestDto requestDto, Pageable pageable);
    BigDecimal getSumTotalAmountByBaseDateAndUsersIdAndTransactionType(String baseDate, Long usersId, TransactionType transactionType);
    BigDecimal getSumTotalAmountWeeklyByBaseDateAndUsersIdAndTransactionType(String baseDate, Long usersId, TransactionType transactionType);
    BigDecimal getSumTotalAmountMonthlyByBaseDateAndUsersIdAndTransactionType(String baseDate, Long usersId, TransactionType transactionType);

}
