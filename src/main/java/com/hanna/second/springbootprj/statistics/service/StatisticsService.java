package com.hanna.second.springbootprj.statistics.service;

import com.hanna.second.springbootprj.ledger.domain.Ledger;
import com.hanna.second.springbootprj.ledger.infra.LedgerJpaRepository;
import com.hanna.second.springbootprj.statistics.domain.Statistics;
import com.hanna.second.springbootprj.statistics.dto.StatisticsRequestDto;
import com.hanna.second.springbootprj.statistics.dto.StatisticsResponseDto;
import com.hanna.second.springbootprj.statistics.infra.StatisticsJpaRepository;
import com.hanna.second.springbootprj.support.ObjectConverter;
import com.hanna.second.springbootprj.support.WeekNumberConverter;
import com.hanna.second.springbootprj.support.enums.CategoryType;
import com.hanna.second.springbootprj.support.enums.PeriodType;
import com.hanna.second.springbootprj.support.enums.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private final LedgerJpaRepository ledgerRepository;
    private final ObjectConverter objectConverter;

    public StatisticsService(final StatisticsJpaRepository statisticsRepository, final LedgerJpaRepository ledgerRepository, ObjectConverter objectConverter) {
        this.statisticsRepository = statisticsRepository;
        this.ledgerRepository = ledgerRepository;
        this.objectConverter = objectConverter;
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
    public void saveStatistics(final Ledger ledger){
        String baseDate = ledger.getBaseDate();
        Long usersId = ledger.getUsersId();
        String categoryType = ledger.getCategoryType() != null && !ledger.getCategoryType().name().isEmpty()
                ? ledger.getCategoryType().name()
                : CategoryType.UNDECIDED.getCategoryType();
        BigDecimal dayExpenseAmount = ledger.getTransactionType() == TransactionType.EXPENSE
                ? ledger.getAmount()
                : BigDecimal.ZERO;
        BigDecimal dayIncomeAmount = ledger.getTransactionType() == TransactionType.INCOME
                ? ledger.getAmount()
                : BigDecimal.ZERO;

        // Ledger의 카테고리, 지출금액 -> Map으로 생성
        Map<String, BigDecimal> dailyCategoryExpenseAmount = new HashMap<>();
        dailyCategoryExpenseAmount.put(categoryType, dayExpenseAmount);

        Optional<Statistics> statisticsResult = statisticsRepository.findByBaseDateAndUsersId(baseDate, usersId);

        // 해당 일자, 사용자 Id에 데이터가 없을 때
        if (!statisticsResult.isPresent()) {
            // 새로운 데이터일 때 삽입
            System.out.println("새로운 데이터일 때!!!");

            // 카테고리별 지출금액 변환 (Map -> Json)
            String jsonDailyCategoryExpenseAmount = objectConverter.convertMapToJson(dailyCategoryExpenseAmount);

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
            Statistics existingStatistics = statisticsResult.get();
            System.out.println("기존 데이터가 있을 때!!!");
            existingStatistics.addExpense(dayExpenseAmount);
            existingStatistics.addIncome(dayIncomeAmount);
            existingStatistics.updateCategoryExpenseAmount(dailyCategoryExpenseAmount, existingStatistics, PeriodType.DAILY);
            statisticsRepository.save(existingStatistics);
        }

    }

    /**********************************
     *  5. 수정된 원장내역을 통계에 반영
     **********************************/
    @Transactional
    public void updateStatistics(final Ledger ledger){
        String baseDate = ledger.getBaseDate();
        Long usersId = ledger.getUsersId();
        TransactionType transactionType = ledger.getTransactionType();

        // 해당 날짜의 모든 Ledger의 금액 합계 계산
        BigDecimal totalExpenseAmount = null;
        BigDecimal totalIncomeAmount = null;

        switch (transactionType) {
            case EXPENSE:
                totalExpenseAmount = ledgerRepository.findAmountSum(baseDate, usersId, transactionType);
                break;
            case INCOME:
                totalIncomeAmount = ledgerRepository.findAmountSum(baseDate, usersId, transactionType);
                break;
        }

        // 카테고리별 금액을 가져오기 위한 메서드 호출
        List<Ledger> ledgers = ledgerRepository.findCategoryAndAmount(baseDate, usersId, transactionType);

        // 카테고리별 금액을 저장할 Map 생성
        Map<String, BigDecimal> dailyCategoryExpenseAmount = new HashMap<>();

        // Ledger에서 카테고리와 금액을 추출하여 Map에 저장
        for (Ledger ledgerItem : ledgers) {
            String categoryType = ledgerItem.getCategoryType().name(); // 카테고리 타입 추출
            BigDecimal amount = ledgerItem.getAmount(); // 금액 추출

            // Map에 카테고리별 금액 누적
            dailyCategoryExpenseAmount.merge(categoryType, amount, BigDecimal::add);
        }

        // Json으로 변환
        String dailyCategoryExpenseAmountJson = objectConverter.convertMapToJson(dailyCategoryExpenseAmount);

        // 기존 통계 데이터 조회
        Optional<Statistics> statisticsResult = statisticsRepository.findByBaseDateAndUsersId(baseDate, usersId);

        if (statisticsResult.isPresent()) {
            Statistics existingStatistics = statisticsResult.get();

            // 기존 통계 데이터 update: ledger의 데이터 합계로 update
            existingStatistics.update(existingStatistics.getBaseDate(),
                    totalExpenseAmount,
                    totalIncomeAmount,
                    dailyCategoryExpenseAmountJson);

        }
    }

    /**********************************
     *  6. 삭제된 원장내역을 통계에 반영
     **********************************/
    @Transactional(propagation = Propagation.REQUIRED)
    public void afterDeleteUpdateStatistics(final Ledger ledger){
        String baseDate = ledger.getBaseDate();
        Long usersId = ledger.getUsersId();
        TransactionType transactionType = ledger.getTransactionType();

        BigDecimal totalExpenseAmount = null;
        BigDecimal totalIncomeAmount = null;

        switch (transactionType) {
            case EXPENSE:
                totalExpenseAmount = ledgerRepository.findAmountSum(baseDate, usersId, transactionType) != null
                        ? ledgerRepository.findAmountSum(baseDate, usersId, transactionType)
                        : BigDecimal.ZERO;
                System.out.println("==========>totalExpenseAmount=========>"+totalExpenseAmount);
                break;
            case INCOME:
                totalIncomeAmount = ledgerRepository.findAmountSum(baseDate, usersId, transactionType) != null
                        ? ledgerRepository.findAmountSum(baseDate, usersId, transactionType)
                        : BigDecimal.ZERO;
                break;
        }

        // 카테고리별 금액을 가져오기 위한 메서드 호출
        List<Ledger> ledgers = ledgerRepository.findCategoryAndAmount(baseDate, usersId, transactionType);

        // 카테고리별 금액을 저장할 Map 생성
        Map<String, BigDecimal> dailyCategoryExpenseAmount = new HashMap<>();

        // Ledger에서 카테고리와 금액을 추출하여 Map에 저장
        for (Ledger ledgerItem : ledgers) {
            String categoryType = ledgerItem.getCategoryType().name(); // 카테고리 타입 추출
            BigDecimal amount = ledgerItem.getAmount(); // 금액 추출

            // Map에 카테고리별 금액 누적
            dailyCategoryExpenseAmount.merge(categoryType, amount, BigDecimal::add);
        }

        // Json으로 변환
        String dailyCategoryExpenseAmountJson = objectConverter.convertMapToJson(dailyCategoryExpenseAmount);

        // 기존 통계 데이터 조회
        Optional<Statistics> statisticsResult = statisticsRepository.findByBaseDateAndUsersId(baseDate, usersId);

        if (statisticsResult.isPresent()) {
            Statistics existingStatistics = statisticsResult.get();
            System.out.println("==========>totalExpenseAmount2222222=========>"+totalExpenseAmount);
            // 기존 통계 데이터 update
            existingStatistics.update(existingStatistics.getBaseDate(),
                    totalExpenseAmount,
                    totalIncomeAmount,
                    dailyCategoryExpenseAmountJson);
            System.out.println("==========>totalExpenseAmount3333333=========>"+totalExpenseAmount);

            //statisticsRepository.saveAndFlush(existingStatistics);
        }
    }

    /**********************************
     *  7. 주별금액 저장 (배치)
     **********************************/
    @Transactional
    public void updateWeeklyStatistics(final String baseDate, final Long usersId, final TransactionType transactionType){

        // 해당 날짜의 모든 Ledger의 금액 합계 계산
        BigDecimal totalExpenseAmount = null;
        BigDecimal totalIncomeAmount = null;

        switch (transactionType) {
            case EXPENSE:
                totalExpenseAmount = ledgerRepository.findWeeklyAmountSum(baseDate, usersId, transactionType);
                break;
            case INCOME:
                totalIncomeAmount = ledgerRepository.findWeeklyAmountSum(baseDate, usersId, transactionType);
                break;
        }

        // 카테고리별 금액을 가져오기 위한 메서드 호출
        List<Ledger> ledgers = ledgerRepository.findWeeklyCategoryAndAmount(baseDate, usersId, transactionType);

        // 카테고리별 금액을 저장할 Map 생성
        Map<String, BigDecimal> weeklyCategoryExpenseAmount = new HashMap<>();

        // Ledger에서 카테고리와 금액을 추출하여 Map에 저장
        for (Ledger ledgerItem : ledgers) {
            String categoryType = ledgerItem.getCategoryType().name(); // 카테고리 타입 추출
            BigDecimal amount = ledgerItem.getAmount(); // 금액 추출

            // Map에 카테고리별 금액 누적
            weeklyCategoryExpenseAmount.merge(categoryType, amount, BigDecimal::add);
        }

        // Json으로 변환
        String weeklyCategoryExpenseAmountJson = objectConverter.convertMapToJson(weeklyCategoryExpenseAmount);


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

        // 해당 날짜의 모든 Ledger의 금액 합계 계산
        BigDecimal totalExpenseAmount = null;
        BigDecimal totalIncomeAmount = null;

        switch (transactionType) {
            case EXPENSE:
                totalExpenseAmount = ledgerRepository.findMonthlyAmountSum(baseDate, usersId, transactionType);
                break;
            case INCOME:
                totalIncomeAmount = ledgerRepository.findMonthlyAmountSum(baseDate, usersId, transactionType);
                break;
        }

        // 카테고리별 금액을 가져오기 위한 메서드 호출
        List<Ledger> ledgers = ledgerRepository.findMonthlyCategoryAndAmount(baseDate, usersId, transactionType);

        // 카테고리별 금액을 저장할 Map 생성
        Map<String, BigDecimal> monthlyCategoryExpenseAmount = new HashMap<>();

        // Ledger에서 카테고리와 금액을 추출하여 Map에 저장
        for (Ledger ledgerItem : ledgers) {
            String categoryType = ledgerItem.getCategoryType().name(); // 카테고리 타입 추출
            BigDecimal amount = ledgerItem.getAmount(); // 금액 추출

            // Map에 카테고리별 금액 누적
            monthlyCategoryExpenseAmount.merge(categoryType, amount, BigDecimal::add);
        }

        // Json으로 변환
        String monthlyCategoryExpenseAmountJson = objectConverter.convertMapToJson(monthlyCategoryExpenseAmount);


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
