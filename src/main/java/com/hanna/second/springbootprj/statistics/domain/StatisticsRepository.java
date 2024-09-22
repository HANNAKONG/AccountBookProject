package com.hanna.second.springbootprj.statistics.domain;

import com.hanna.second.springbootprj.statistics.dto.StatisticsRequestDto;
import com.hanna.second.springbootprj.support.enums.CategoryType;

import java.math.BigDecimal;
import java.util.Optional;

public interface StatisticsRepository {
    void delete(Statistics statistics);
    Statistics save(Statistics statistics);
    Optional<Statistics> findById(Long Id);
    Optional<Statistics> findByBaseDateAndUsersId(String baseDate, Long usersId);
    Optional<BigDecimal> findExpenseAmountByFilter(StatisticsRequestDto statisticsRequestDto);
    Optional<BigDecimal> findExpenseAmountByCategoryType(StatisticsRequestDto statisticsRequestDto, CategoryType categoryType);
    //Optional<BigDecimal> findIncomeAmountByFilter(StatisticsRequestDto statisticsRequestDto);
}
