package com.hanna.second.springbootprj.statistics.domain;

import com.hanna.second.springbootprj.statistics.dto.StatisticsRequestDto;
import com.hanna.second.springbootprj.support.Money;
import com.hanna.second.springbootprj.support.enums.CategoryType;
import com.hanna.second.springbootprj.support.enums.PeriodType;

import java.math.BigDecimal;
import java.util.Optional;

public interface StatisticsRepositoryCustom {
    BigDecimal findAllByBaseDate(StatisticsRequestDto requestDto);
    //Optional<BigDecimal> findIncomeAmountByFilter(StatisticsRequestDto statisticsRequestDto);
}
