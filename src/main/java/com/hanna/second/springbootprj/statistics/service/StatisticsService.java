package com.hanna.second.springbootprj.statistics.service;

import com.hanna.second.springbootprj.ledger.domain.Ledger;
import com.hanna.second.springbootprj.ledger.domain.LedgerRepositoryImpl;
import com.hanna.second.springbootprj.statistics.domain.Statistics;
import com.hanna.second.springbootprj.statistics.domain.StatisticsRepositoryImpl;
import com.hanna.second.springbootprj.statistics.dto.StatisticsRequestDto;
import com.hanna.second.springbootprj.statistics.dto.StatisticsResponseDto;
import com.hanna.second.springbootprj.support.EntityMapper;
import com.hanna.second.springbootprj.support.enums.CategoryType;
import com.hanna.second.springbootprj.support.enums.TransactionType;
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
    private final LedgerRepositoryImpl ledgerRepository;
    private final EntityMapper entityMapper;

    public StatisticsService(final StatisticsRepositoryImpl statisticsRepository, final LedgerRepositoryImpl ledgerRepository, EntityMapper entityMapper) {
        this.statisticsRepository = statisticsRepository;
        this.ledgerRepository = ledgerRepository;
        this.entityMapper = entityMapper;
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
     *  4. 원장내역 등록 이후 통계내역 등록
     **********************************/
    @Transactional
    public void saveStatistics(final Ledger ledger){
        // Ledger -> Statistics
        Statistics newStatistics = entityMapper.ledgerToStatistics(ledger);

        // 기존 데이터 조회
        String baseDate = newStatistics.getBaseDate();
        Long usersId = newStatistics.getUsersId();

        Statistics existingStatistics = statisticsRepository.findByBaseDateAndUsersId(baseDate, usersId)
                .orElse(new Statistics(baseDate, BigDecimal.ZERO, BigDecimal.ZERO, usersId));

        // 금액 업데이트
        if (existingStatistics.getId() != null) {
            // 기존 데이터가 있을 때 업데이트
            System.out.println("기존 데이터가 있을 때!!!");
            existingStatistics.addExpense(newStatistics.getDayExpenseAmount());
            existingStatistics.addIncome(newStatistics.getDayIncomeAmount());
            statisticsRepository.save(existingStatistics);
        } else {
            // 새로운 데이터일 때 삽입
            System.out.println("새로운 데이터일 때!!!");
            Statistics updatedStatistics = Statistics.builder()
                    .baseDate(newStatistics.getBaseDate()) // 새로운 baseDate 사용
                    .usersId(newStatistics.getUsersId()) // 새로운 usersId 사용
                    .dayExpenseAmount(newStatistics.getDayExpenseAmount())
                    .dayIncomeAmount(newStatistics.getDayIncomeAmount())
                    .build();
            statisticsRepository.save(updatedStatistics);
        }

    }

    /**********************************
     *  5. 수정된 원장내역을 통계에 반영
     **********************************/
    @Transactional
    public void updateStatistics(final Ledger ledger){
        System.out.println("updatestatistics--------------------");
        String baseDate = ledger.getBaseDate();
        Long usersId = ledger.getUsersId();
        TransactionType transactionType = ledger.getTransactionType();

        // 해당 날짜의 모든 Ledger의 금액 합계 계산
        BigDecimal totalExpenseAmount = null;
        BigDecimal totalIncomeAmount = null;

        switch (transactionType) {
            case EXPENSE:
                totalExpenseAmount = ledgerRepository.sumAmountByBaseDateAndUsersIdAndTransactionType(baseDate, usersId, transactionType);
                break;
            case INCOME:
                totalIncomeAmount = ledgerRepository.sumAmountByBaseDateAndUsersIdAndTransactionType(baseDate, usersId, transactionType);
                break;
        }

        // 기존 통계 데이터 조회
        Optional<Statistics> existingStatisticsOptional = statisticsRepository.findByBaseDateAndUsersId(baseDate, usersId);

        if (existingStatisticsOptional.isPresent()) {
            Statistics existingStatistics = existingStatisticsOptional.get();

            // 기존 통계 데이터 update: ledger의 데이터 합계로 update
            existingStatistics.update(existingStatistics.getBaseDate(),
                    totalExpenseAmount,
                    totalIncomeAmount);

            System.out.println("update 안--------->");
            statisticsRepository.save(existingStatistics);
        }
    }

    /**********************************
     *  6. 삭제된 원장내역을 통계에 반영
     **********************************/
    @Transactional
    public void updateDeleteStatistics(final Ledger ledger){
        String baseDate = ledger.getBaseDate();
        Long usersId = ledger.getUsersId();
        BigDecimal ledgerAmount = ledger.getAmount(); // 삭제할 Ledger의 금액
        TransactionType transactionType = ledger.getTransactionType();
        
        // 해당 내역 금액만 삭제
        BigDecimal totalExpenseAmount = null;
        BigDecimal totalIncomeAmount = null;

        switch (transactionType) {
            case EXPENSE:
                totalExpenseAmount = ledgerRepository.sumAmountByBaseDateAndUsersIdAndTransactionType(baseDate, usersId, transactionType).subtract(ledgerAmount);
                break;
            case INCOME:
                totalIncomeAmount = ledgerRepository.sumAmountByBaseDateAndUsersIdAndTransactionType(baseDate, usersId, transactionType).subtract(ledgerAmount);
                break;
        }

        // 기존 통계 데이터 조회
        Optional<Statistics> existingStatisticsOptional = statisticsRepository.findByBaseDateAndUsersId(baseDate, usersId);

        if (existingStatisticsOptional.isPresent()) {
            Statistics existingStatistics = existingStatisticsOptional.get();

            // 기존 통계 데이터 update: ledger의 데이터 합계에서 해당 내역의 금액만 뺀 값으로 update
            existingStatistics.update(existingStatistics.getBaseDate(),
                    totalExpenseAmount,
                    totalIncomeAmount);
            
            statisticsRepository.save(existingStatistics);
        }
    }
}
