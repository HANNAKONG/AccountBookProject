package com.hanna.second.springbootprj.statistics.domain;

import com.hanna.second.springbootprj.statistics.dto.StatisticsRequestDto;
import com.hanna.second.springbootprj.statistics.infra.StatisticsJpaRepository;
import com.hanna.second.springbootprj.support.enums.CategoryType;
import com.hanna.second.springbootprj.support.enums.PeriodType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Repository
public class StatisticsRepositoryImpl implements StatisticsRepository {

    private final StatisticsJpaRepository statisticsJpaRepository;

    private final JPAQueryFactory jpaQueryFactory;
    private final QStatistics statistics = QStatistics.statistics;
    private final QCategoryExpense categoryExpense = QCategoryExpense.categoryExpense;

    public StatisticsRepositoryImpl(StatisticsJpaRepository statisticsJpaRepository, JPAQueryFactory jpaQueryFactory) {
        this.statisticsJpaRepository = statisticsJpaRepository;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    /**********************************
     *  1-1. 통계 조회: 기간별(일/주/월) 지출금액
     **********************************/
    @Override
    public Optional<BigDecimal> findExpenseAmountByFilter(StatisticsRequestDto requestDto) {
        BooleanExpression periodFilter = dateFilterByPeriodType(requestDto.getPeriodType(), requestDto.getBaseDate());
        BigDecimal result = null;

        switch (requestDto.getPeriodType()) {
            case DAILY:
                result = jpaQueryFactory.select(statistics.dayExpenseAmount)
                        .from(statistics)
                        .where(periodFilter)
                        .fetchOne();
                break;
            case WEEKLY:
            case MONTHLY:
                result = jpaQueryFactory.select(statistics.dayExpenseAmount.sum())
                        .from(statistics)
                        .where(periodFilter)
                        .fetchOne();
                break;
            default:
                throw new IllegalArgumentException("Unsupported period type: " + requestDto.getPeriodType());
        }

        return Optional.ofNullable(result);
    }

    /**********************************
     *  1-2. 통계 조회: 카테고리별 지출금액
     **********************************/
    @Override
    public Optional<BigDecimal> findExpenseAmountByCategoryType(StatisticsRequestDto requestDto, CategoryType categoryType) {

        BooleanExpression periodFilter = dateFilterByPeriodType(requestDto.getPeriodType(), requestDto.getBaseDate());
        BigDecimal result = null;

        switch (requestDto.getPeriodType()) {
            case DAILY:
                result = jpaQueryFactory.select(categoryExpense.expenseAmount)
                        .from(categoryExpense)
                        .join(categoryExpense.statistics, statistics)
                        .where(periodFilter, categoryExpense.categoryType.eq(categoryType))
                        .fetchOne();
                break;
            case WEEKLY:
            case MONTHLY:
                result = jpaQueryFactory.select(categoryExpense.expenseAmount.sum())
                        .from(categoryExpense)
                        .join(categoryExpense.statistics, statistics)
                        .where(periodFilter, categoryExpense.categoryType.eq(categoryType))
                        .fetchOne();
                break;
            default:
                throw new IllegalArgumentException("Unsupported period type: " + requestDto.getPeriodType());
        }

        return Optional.ofNullable(result);

    }

    // 기간별 필터
    private BooleanExpression dateFilterByPeriodType(PeriodType periodType, String baseDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(baseDate, formatter);

        switch (periodType) {
            case DAILY:
                return statistics.baseDate.eq(date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            case WEEKLY:
                LocalDate startOfWeek = date.with(DayOfWeek.MONDAY); // 주의 시작일 (월요일)
                LocalDate endOfWeek = date.with(DayOfWeek.SUNDAY);   // 주의 종료일 (일요일)
                return statistics.baseDate.between(
                        startOfWeek.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                        endOfWeek.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                );
            case MONTHLY:
                LocalDate startOfMonth = date.withDayOfMonth(1);   // 달의 시작일
                LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth()); // 달의 마지막 날
                return statistics.baseDate.between(
                        startOfMonth.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                        endOfMonth.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                );
            default:
                throw new IllegalArgumentException("Unsupported period type: " + periodType);
        }
    }

    @Override
    public void delete(Statistics statistics) {
        statisticsJpaRepository.delete(statistics);
    }

    @Override
    public Statistics save(Statistics statistics) {
        return statisticsJpaRepository.save(statistics);
    }

    @Override
    public Optional<Statistics> findById(Long Id) {
        return statisticsJpaRepository.findById(Id);
    }

    @Override
    public Optional<Statistics> findByBaseDateAndUsersId(String baseDate,Long usersId) {
        return statisticsJpaRepository.findByBaseDateAndUsersId(baseDate, usersId);
    }
}
