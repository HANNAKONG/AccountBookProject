package com.hanna.second.springbootprj.statistics.service;

import com.hanna.second.springbootprj.ledger.domain.Ledger;
import com.hanna.second.springbootprj.ledger.domain.LedgerRepository;
import com.hanna.second.springbootprj.ledger.domain.LedgerSpecification;
import com.hanna.second.springbootprj.ledger.dto.LedgerRequestDto;
import com.hanna.second.springbootprj.ledger.dto.LedgerResponseDto;
import com.hanna.second.springbootprj.statistics.domain.Statistics;
import com.hanna.second.springbootprj.statistics.domain.StatisticsRepository;
import com.hanna.second.springbootprj.statistics.domain.StatisticsSpecification;
import com.hanna.second.springbootprj.statistics.dto.StatisticsRequestDto;
import com.hanna.second.springbootprj.statistics.dto.StatisticsResponseDto;
import com.hanna.second.springbootprj.support.enums.TransactionType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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

    private final StatisticsRepository statisticsRepository;

    public StatisticsService(final StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    /**********************************
     *  1. 통계 조회: 지출금액
     *  - 구현: 일별, 카테고리별
     *  - 미구현: 월별, 주별 + 카테고리별
     **********************************/
    public StatisticsResponseDto getTotalExpense(final StatisticsRequestDto requestDto){
        Specification<Statistics> specification = StatisticsSpecification.byFilters(requestDto);

        List<Statistics> statisticsList = statisticsRepository.findAll(specification);
        BigDecimal amountSum = statisticsList.stream()
                .filter(statistics -> statistics.getTransactionType() == TransactionType.EXPENSE)
                .map(Statistics::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new StatisticsResponseDto(amountSum);
    }

    /**********************************
     *  2. 통계 조회: 수입금액
     *  - 구현: 일별, 카테고리별
     *  - 미구현: 월별, 주별 + 카테고리별
     **********************************/
    public StatisticsResponseDto getTotalIncome(final StatisticsRequestDto requestDto){
        Specification<Statistics> specification = StatisticsSpecification.byFilters(requestDto);

        List<Statistics> statisticsList = statisticsRepository.findAll(specification);
        BigDecimal amountSum = statisticsList.stream()
                .filter(statistics -> statistics.getTransactionType() == TransactionType.INCOME)
                .map(Statistics::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new StatisticsResponseDto(amountSum);
    }

    /**********************************
     *  3. 통계 조회: 지출금액 - 수입금액
     **********************************/
    public StatisticsResponseDto getIncomeMinusExpense(final StatisticsRequestDto requestDto){
        StatisticsResponseDto totalIncome = getTotalIncome(requestDto);
        StatisticsResponseDto totalExpense = getTotalExpense(requestDto);
        BigDecimal incomeMinusExpense = totalIncome.getAmountSum().subtract(totalExpense.getAmountSum());

        return new StatisticsResponseDto(incomeMinusExpense);
    }

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
                statistics.getTransactionType(),
                statistics.getAssetType(),
                statistics.getCategoryType(),
                statistics.getAmount()
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
