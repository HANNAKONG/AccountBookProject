package com.hanna.second.springbootprj.budget.domain;

import com.hanna.second.springbootprj.budget.dto.BudgetRequestDto;
import com.hanna.second.springbootprj.budget.dto.BudgetResponseDto;
import com.hanna.second.springbootprj.statistics.domain.QStatistics;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Repository
public class BudgetRepositoryCustomImpl implements BudgetRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QBudget budget = QBudget.budget;
    private final QStatistics statistics = QStatistics.statistics;

    public BudgetRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public BudgetResponseDto findByYearMonthWithStatistics(BudgetRequestDto requestDto) {

        String previousMonth = getPreviousMonth(requestDto.getYearMonth());

        BudgetResponseDto content = jpaQueryFactory.select(
                        Projections.constructor(BudgetResponseDto.class,
                                budget.id,
                                budget.yearMonth,
                                budget.totalAmount,
                                budget.categoryExpenseAmount,
                                statistics.monthExpenseAmount,
                                statistics.monthlyCategoryExpenseAmount,
                                budget.usersId
                        ))
                .from(budget)
                .leftJoin(statistics)
                .on(budget.usersId.eq(requestDto.getUsersId())
                        .and(statistics.baseDate.eq(previousMonth)))
                .where(budget.yearMonth.eq(requestDto.getYearMonth())) // yearMonth 조건 추가
                .fetchOne();

        return content;
    }

    private String getPreviousMonth(String yearMonth) {
        LocalDate date = LocalDate.parse(yearMonth + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
        date = date.minusMonths(1); // 1개월 전
        return date.format(DateTimeFormatter.ofPattern("yyyyMM")); // yyyyMM 형식으로 반환
    }

}
