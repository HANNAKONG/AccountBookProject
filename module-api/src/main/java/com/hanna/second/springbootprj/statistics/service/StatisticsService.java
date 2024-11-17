package com.hanna.second.springbootprj.statistics.service;

import com.hanna.second.springbootprj.ledger.domain.Ledger;
import com.hanna.second.springbootprj.ledger.domain.LedgerClient;
import com.hanna.second.springbootprj.statistics.domain.Statistics;
import com.hanna.second.springbootprj.statistics.dto.StatisticsRequestDto;
import com.hanna.second.springbootprj.statistics.dto.StatisticsResponseDto;
import com.hanna.second.springbootprj.statistics.infra.StatisticsJpaRepository;
import com.hanna.second.springbootprj.support.ObjectConverter;
import com.hanna.second.springbootprj.support.WeekNumberConverter;
import com.hanna.second.springbootprj.support.enums.CategoryType;
import com.hanna.second.springbootprj.support.enums.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    // 월별 지출금액(금월),
    // 월별 지출금액(전월),
    // 주별 지출금액,
    // 카테고리별 지출금액(금월),
    // 카테고리별 지출금액(전월)

    // 월별 수입금액(금월)
    // 일별 지출금액, 일별 수입금액, 일별 합계금액

    private final StatisticsJpaRepository statisticsRepository;
    private final LedgerClient ledgerClient;

    public StatisticsService(final StatisticsJpaRepository statisticsRepository, final LedgerClient ledgerClient) {
        this.statisticsRepository = statisticsRepository;
        this.ledgerClient = ledgerClient;
    }

    /**********************************
     *  1-1. 통계 조회: 기간별 지출금액
     **********************************/
    public StatisticsResponseDto getExpenseByPeriod(final StatisticsRequestDto requestDto){
        BigDecimal expenseAmount = statisticsRepository.findAllByBaseDate(requestDto);
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
    public void saveStatistics(final Long ledgerId){
        Ledger ledger = ledgerClient.findById(ledgerId)
                .orElseThrow(() -> new EntityNotFoundException("Ledger not found for ID: " + ledgerId));

        String baseDate = ledger.getBaseDate();
        Long usersId = ledger.getUsersId();
        CategoryType categoryType = ledger.getCategoryType();
        BigDecimal dayExpenseAmount = ledger.getTransactionType() == TransactionType.EXPENSE ? ledger.getAmount() : BigDecimal.ZERO;
        BigDecimal dayIncomeAmount = ledger.getTransactionType() == TransactionType.INCOME ? ledger.getAmount() : BigDecimal.ZERO;

        // Ledger의 카테고리, 지출금액 -> Map으로 생성
        Map<CategoryType, BigDecimal> dailyCategoryExpenseAmount = new HashMap<>();
        dailyCategoryExpenseAmount.put(categoryType, dayExpenseAmount);

        Optional<Statistics> statisticsOpt = statisticsRepository.findByBaseDateAndUsersId(baseDate, usersId);

        // 해당 일자, 사용자 Id에 데이터가 없을 때
        if (statisticsOpt.isEmpty()) {
            // 새로운 데이터일 때 삽입
            System.out.println("새로운 데이터일 때!!!");

            // 카테고리별 지출금액 변환 (Map -> Json)
            String jsonDailyCategoryExpenseAmount = ObjectConverter.convertMapToJson(dailyCategoryExpenseAmount);

            Statistics updatedStatistics = Statistics.builder()
                    .baseDate(baseDate)
                    .usersId(usersId)
                    .dayExpenseAmount(dayExpenseAmount)
                    .dayIncomeAmount(dayIncomeAmount)
                    .dailyCategoryExpenseAmount(jsonDailyCategoryExpenseAmount)
                    .build();

            statisticsRepository.save(updatedStatistics);
        } else {
            // 기존 데이터가 있을 때 업데이트
            Statistics existingStatistics = statisticsOpt.get();
            System.out.println("기존 데이터가 있을 때!!!");
            existingStatistics.addExpense(dayExpenseAmount);
            existingStatistics.addIncome(dayIncomeAmount);
            //여기로 뺄까...
            existingStatistics.updateCategoryExpenseAmount(dailyCategoryExpenseAmount, dayExpenseAmount, existingStatistics);
            statisticsRepository.save(existingStatistics);
        }

    }

    /**********************************
     *  5. 수정된 원장내역을 통계에 반영
     **********************************/
    @Transactional
    public void updateStatistics(final Long ledgerId){
        Ledger ledger = ledgerClient.findById(ledgerId)
                .orElseThrow(() -> new EntityNotFoundException("Ledger not found for ID: " + ledgerId));

        String baseDate = ledger.getBaseDate();
        Long usersId = ledger.getUsersId();
        TransactionType transactionType = ledger.getTransactionType();

        // 반영할 금액(지출or수입) 가져오기
        BigDecimal totalAmount = ledgerClient.findAmountSum(baseDate, usersId, transactionType);

        // 카테고리별 금액 가져오기
        List<Ledger> ledgers = ledgerClient.findCategoryAndAmount(baseDate, usersId, transactionType);

        // 카테고리별 금액을 합산하여 맵으로 생성
        Map<CategoryType, BigDecimal> dailyCategoryExpenseAmount =
                ledgers.stream()
                        .collect(Collectors.toMap(
                                Ledger::getCategoryType, // 키: 카테고리 타입
                                Ledger::getAmount, // 값: 금액
                                BigDecimal::add // 중복된 키가 있을 경우 합산
                        ));

        // Json으로 변환
        String dailyCategoryExpenseAmountJson = ObjectConverter.convertMapToJson(dailyCategoryExpenseAmount);

        // 기존 통계 데이터 조회
        Statistics existingStatistics = statisticsRepository.findByBaseDateAndUsersId(baseDate, usersId)
                .orElseThrow(() -> new EntityNotFoundException("Statistics not found for baseDate: " + baseDate + " and userId: " + usersId));

        // 업데이트할 값을 결정
        BigDecimal dayExpenseAmount = transactionType == TransactionType.INCOME ? existingStatistics.getDayExpenseAmount() : totalAmount;
        BigDecimal dayIncomeAmount = transactionType == TransactionType.EXPENSE ? existingStatistics.getDayIncomeAmount() : totalAmount;

        // 업데이트 호출
        existingStatistics.update(existingStatistics.getBaseDate(), dayExpenseAmount, dayIncomeAmount, dailyCategoryExpenseAmountJson);
    }

    /**********************************
     *  6. 삭제된 원장내역을 통계에 반영
     **********************************/
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void afterDeleteUpdateStatistics(final Ledger ledger){
        String baseDate = ledger.getBaseDate();
        Long usersId = ledger.getUsersId();
        TransactionType transactionType = ledger.getTransactionType();

        // 반영할 금액(지출or수입) 가져오기
        BigDecimal totalAmount = ledgerClient.findAmountSum(baseDate, usersId, transactionType);

        // 카테고리별 금액 가져오기
        List<Ledger> ledgers = ledgerClient.findCategoryAndAmount(baseDate, usersId, transactionType);

        // 카테고리별 금액을 합산하여 맵으로 생성
        Map<CategoryType, BigDecimal> dailyCategoryExpenseAmount =
                ledgers.stream()
                        .collect(Collectors.toMap(
                                Ledger::getCategoryType, // 키: 카테고리 타입
                                Ledger::getAmount, // 값: 금액
                                BigDecimal::add // 중복된 키가 있을 경우 합산
                        ));

        // Json으로 변환
        String dailyCategoryExpenseAmountJson = ObjectConverter.convertMapToJson(dailyCategoryExpenseAmount);

        // 기존 통계 데이터 조회
        Statistics existingStatistics = statisticsRepository.findByBaseDateAndUsersId(baseDate, usersId)
                .orElseThrow(() -> new EntityNotFoundException("Statistics not found for baseDate: " + baseDate + " and userId: " + usersId));

        // 업데이트할 값을 결정
        BigDecimal dayExpenseAmount = transactionType == TransactionType.INCOME ? existingStatistics.getDayExpenseAmount() : totalAmount;
        BigDecimal dayIncomeAmount = transactionType == TransactionType.EXPENSE ? existingStatistics.getDayIncomeAmount() : totalAmount;

        // 업데이트 호출
        existingStatistics.update(existingStatistics.getBaseDate(), dayExpenseAmount, dayIncomeAmount, dailyCategoryExpenseAmountJson);
    }

    /**********************************
     *  7. 주별금액 저장 (배치)
     **********************************/
    @Transactional
    public void updateWeeklyStatistics(final String baseDate, final Long usersId, final TransactionType transactionType){

        BigDecimal totalExpenseAmount = ledgerClient.findWeeklyAmountSum(baseDate, usersId, TransactionType.EXPENSE);
        BigDecimal totalIncomeAmount = ledgerClient.findWeeklyAmountSum(baseDate, usersId, TransactionType.INCOME);

        // 카테고리별 금액을 가져오기 위한 메서드 호출
        List<Ledger> ledgers = ledgerClient.findWeeklyCategoryAndAmount(baseDate, usersId, transactionType);

        Map<CategoryType, BigDecimal> weeklyCategoryExpenseAmount =
                ledgers.stream()
                        .collect(Collectors.toMap(
                                Ledger::getCategoryType, // 키: 카테고리 타입
                                Ledger::getAmount, // 값: 금액
                                BigDecimal::add // 중복된 키가 있을 경우 합산
                        ));

        // Json으로 변환
        String weeklyCategoryExpenseAmountJson = ObjectConverter.convertMapToJson(weeklyCategoryExpenseAmount);


        // 기존 통계 데이터 조회
        Optional<Statistics> statisticsResult = statisticsRepository.findByBaseDateAndUsersId(WeekNumberConverter.convertToWeekNumber(baseDate), usersId);

        if (statisticsResult.isPresent()) {
            Statistics existingStatistics = statisticsResult.get();

            // 기존 통계 데이터 update
            existingStatistics.updateForWeekly(existingStatistics.getBaseDate(),
                    totalExpenseAmount,
                    totalIncomeAmount,
                    weeklyCategoryExpenseAmountJson);
        } else {
            Statistics createdStatistics = Statistics.builder()
                    .baseDate(WeekNumberConverter.convertToWeekNumber(baseDate))
                    .usersId(usersId)
                    .weekExpenseAmount(totalExpenseAmount)
                    .weekIncomeAmount(totalIncomeAmount)
                    .weeklyCategoryExpenseAmount(weeklyCategoryExpenseAmountJson)
                    .build();

            statisticsRepository.save(createdStatistics);
        }
    }


    /**********************************
     *  8. 월별금액 저장 (배치)
     **********************************/
    @Transactional
    public void updateMonthlyStatistics(final String baseDate, final Long usersId, final TransactionType transactionType){

        BigDecimal totalExpenseAmount = ledgerClient.findMonthlyAmountSum(baseDate, usersId, TransactionType.EXPENSE);
        BigDecimal totalIncomeAmount = ledgerClient.findMonthlyAmountSum(baseDate, usersId, TransactionType.INCOME);

        // 카테고리별 금액을 가져오기 위한 메서드 호출
        List<Ledger> ledgers = ledgerClient.findMonthlyCategoryAndAmount(baseDate, usersId, transactionType);

        Map<CategoryType, BigDecimal> monthlyCategoryExpenseAmount =
                ledgers.stream()
                        .collect(Collectors.toMap(
                                Ledger::getCategoryType, // 키: 카테고리 타입
                                Ledger::getAmount, // 값: 금액
                                BigDecimal::add // 중복된 키가 있을 경우 합산
                        ));

        // Json으로 변환
        String monthlyCategoryExpenseAmountJson = ObjectConverter.convertMapToJson(monthlyCategoryExpenseAmount);


        // 기존 통계 데이터 조회
        Optional<Statistics> statisticsResult = statisticsRepository.findByBaseDateAndUsersId(baseDate.substring(0,6), usersId);

        if (statisticsResult.isPresent()) {
            Statistics existingStatistics = statisticsResult.get();

            // 기존 통계 데이터 update
            existingStatistics.updateForMonthly(existingStatistics.getBaseDate(),
                    totalExpenseAmount,
                    totalIncomeAmount,
                    monthlyCategoryExpenseAmountJson);
        } else {
            Statistics createdStatistics = Statistics.builder()
                    .baseDate(baseDate.substring(0, 6))
                    .usersId(usersId)
                    .monthExpenseAmount(totalExpenseAmount)
                    .monthIncomeAmount(totalIncomeAmount)
                    .monthlyCategoryExpenseAmount(monthlyCategoryExpenseAmountJson)
                    .build();

            statisticsRepository.save(createdStatistics);
        }

    }

}
