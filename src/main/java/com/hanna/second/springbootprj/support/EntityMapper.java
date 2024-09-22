package com.hanna.second.springbootprj.support;

import com.hanna.second.springbootprj.ledger.domain.Ledger;
import com.hanna.second.springbootprj.statistics.domain.Statistics;
import com.hanna.second.springbootprj.support.enums.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EntityMapper {

    public Statistics ledgerToStatistics(Ledger ledger) {

        String baseDate = ledger.getBaseDate();
        Long usersId = ledger.getUsersId();
        BigDecimal amount = ledger.getAmount();
        TransactionType transactionType = ledger.getTransactionType();

        // Statistics 객체 빌드
        Statistics statistics = Statistics.builder()
                .baseDate(baseDate)
                .usersId(usersId)
                .dayExpenseAmount(transactionType == TransactionType.EXPENSE ? amount : BigDecimal.ZERO)  // 지출 금액
                .dayIncomeAmount(transactionType == TransactionType.INCOME ? amount : BigDecimal.ZERO)    // 수입 금액
                .build();

        return statistics;
    }
}
