package com.hanna.second.springbootprj.statistics.domain;

import com.hanna.second.springbootprj.statistics.dto.StatisticsRequestDto;
import com.hanna.second.springbootprj.support.enums.PeriodType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Repository
public class StatisticsRepositoryCustomImpl implements StatisticsRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QStatistics statistics = QStatistics.statistics;

    public StatisticsRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    /**********************************
     *  1-1. 통계 조회: 기간별(일/주/월) 지출금액
     **********************************/
    @Override
    public BigDecimal findAllByBaseDate(StatisticsRequestDto requestDto) {
        PeriodType periodType = requestDto.getPeriodType();
        String baseDate = requestDto.getBaseDate();

        BooleanExpression periodFilter = dateFilterByPeriodType(periodType, baseDate);
        BigDecimal result = null;

        switch (periodType) {
            case DAILY:
                result = jpaQueryFactory.select(statistics.dayExpenseAmount)
                        .from(statistics)
                        .where(periodFilter)
                        .fetchOne();
                break;
            case WEEKLY:
                result = jpaQueryFactory.select(statistics.weekExpenseAmount)
                        .from(statistics)
                        .where(periodFilter)
                        .fetchOne();
                break;
            case MONTHLY:
                result = jpaQueryFactory.select(statistics.monthExpenseAmount)
                        .from(statistics)
                        .where(periodFilter)
                        .fetchOne();
                break;
            default:
                throw new IllegalArgumentException("Unsupported period type: " + periodType);
        }

        return result;
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

}
