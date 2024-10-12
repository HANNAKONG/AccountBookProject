package com.hanna.second.springbootprj.budget.domain;

import com.hanna.second.springbootprj.budget.dto.BudgetRequestDto;
import com.hanna.second.springbootprj.budget.dto.BudgetResponseDto;

public interface BudgetRepositoryCustom {
    BudgetResponseDto findByYearMonthWithStatistics(BudgetRequestDto budgetRequestDto);
}
