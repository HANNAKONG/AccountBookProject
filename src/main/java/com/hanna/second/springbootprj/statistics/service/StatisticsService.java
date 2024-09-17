package com.hanna.second.springbootprj.statistics.service;

import com.hanna.second.springbootprj.statistics.domain.Statistics;
import com.hanna.second.springbootprj.statistics.domain.StatisticsRepositoryImpl;
import com.hanna.second.springbootprj.statistics.dto.StatisticsRequestDto;
import com.hanna.second.springbootprj.statistics.dto.StatisticsResponseDto;
import com.hanna.second.springbootprj.support.enums.CategoryType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class StatisticsService {

    // 월별 지출금액(금월),
    // 월별 지출금액(전월),
    // 주별 지출금액,
    // 카테고리별 지출금액(금월),
    // 카테고리별 지출금액(전월)
    // -> substr(baseDate, 0, 4), transactionType, categoryType
    // -> 지난월, 주별 구하는 util 필요

    // 월별 수입금액 => substr(baseDate, 0, 4), transactionType
    // 일별 지출금액, 일별 수입금액, 일별 합계금액 => baseDate, transactionType

    private final StatisticsRepositoryImpl statisticsRepository;

    public StatisticsService(final StatisticsRepositoryImpl statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    /**********************************
     *  1-1. 통계 조회: 기간별 지출금액
     **********************************/
    public StatisticsResponseDto getExpenseByPeriod(final StatisticsRequestDto requestDto){
        Optional<BigDecimal> expenseAmount = statisticsRepository.findExpenseAmountByFilter(requestDto);
        return new StatisticsResponseDto(expenseAmount);
    }

    /**********************************
     *  1-2. 통계 조회: 카테고리별 지출금액
     **********************************/
    public StatisticsResponseDto getExpenseByCategory(final StatisticsRequestDto requestDto, final CategoryType categoryType){
        Optional<BigDecimal> expenseAmount = statisticsRepository.findExpenseAmountByCategoryType(requestDto, categoryType);
        return new StatisticsResponseDto(expenseAmount);
    }

    /**********************************
     *  2. 통계 조회: 수입금액
     *  - 일별수입금액, 주별수입금액, 월별수입금액, 카테고리별수입금액
     **********************************/
//    public StatisticsResponseDto getTotalIncome(final StatisticsRequestDto requestDto){
//        return Optional<BigDecimal> BigDecimal.ZERO;
//    }

    /**********************************
     *  3. 통계 조회: 지출금액 - 수입금액
     **********************************/
//    public StatisticsResponseDto getIncomeMinusExpense(final StatisticsRequestDto requestDto){
//        StatisticsResponseDto totalIncome = getTotalIncome(requestDto);
//        StatisticsResponseDto totalExpense = getTotalExpense(requestDto);
//        BigDecimal incomeMinusExpense = totalIncome.getAmountSum().subtract(totalExpense.getAmountSum());
//
//        return new StatisticsResponseDto(incomeMinusExpense);
//    }

    /**********************************
     *  3. 통계내역 등록
     **********************************/
    @Transactional
    public void saveStatistics(final Statistics statistics){
        statisticsRepository.save(statistics);
    }

    /**********************************
     *  4. 통계내역 수정
     **********************************/
    @Transactional
    public void updateStatistics(final Statistics statistics){
        final Statistics entity = statisticsRepository.findById(statistics.getId()).orElseThrow(
                () -> new IllegalArgumentException("해당 내역이 없습니다.")
        );

        entity.update(statistics.getBaseDate(),
                statistics.getDayExpenseAmount(),
                statistics.getDayIncomeAmount()
                    );
    }

    /**********************************
     *  5. 통계내역 삭제
     **********************************/
    @Transactional
    public void deleteStatistics(final Long id){
        final Statistics entity = statisticsRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 내역이 없습니다.")
        );

        statisticsRepository.delete(entity);
    }
}
