package com.hanna.second.springbootprj.statistics.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanna.second.springbootprj.support.enums.PeriodType;

import javax.persistence.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Statistics {

    /** 아이디 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 기준날짜 */
    @Column(nullable = false)
    private String baseDate;

    /** 일지출금액 */
    @Column(nullable = true)
    private BigDecimal dayExpenseAmount;

    /** 일수입금액 */
    @Column(nullable = true)
    private BigDecimal dayIncomeAmount;

    /** 주지출금액 */
    @Column(nullable = true)
    private BigDecimal weekExpenseAmount;

    /** 주수입금액 */
    @Column(nullable = true)
    private BigDecimal weekIncomeAmount;

    /** 월지출금액 */
    @Column(nullable = true)
    private BigDecimal monthExpenseAmount;

    /** 월수입금액 */
    @Column(nullable = true)
    private BigDecimal monthIncomeAmount;

    /** 카테고리별지출금액 (일별) */
    @Column(columnDefinition = "TEXT")
    private String dailyCategoryExpenseAmount;

    /** 카테고리별지출금액 (주별) */
    @Column(columnDefinition = "TEXT")
    private String weeklyCategoryExpenseAmount;

    /** 카테고리별지출금액 (월별) */
    @Column(columnDefinition = "TEXT")
    private String monthlyCategoryExpenseAmount;

    /** Users Id */
    private Long usersId;

    /**********************************
     *  constructor
     **********************************/
    public Statistics(){
    }

    public Statistics(Long id, String baseDate, BigDecimal dayExpenseAmount, BigDecimal dayIncomeAmount, BigDecimal weekExpenseAmount, BigDecimal weekIncomeAmount, BigDecimal monthExpenseAmount, BigDecimal monthIncomeAmount, String dailyCategoryExpenseAmount, String weeklyCategoryExpenseAmount, String monthlyCategoryExpenseAmount, Long usersId) {
        this.id = id;
        this.baseDate = baseDate;
        this.dayExpenseAmount = dayExpenseAmount;
        this.dayIncomeAmount = dayIncomeAmount;
        this.weekExpenseAmount = weekExpenseAmount;
        this.weekIncomeAmount = weekIncomeAmount;
        this.monthExpenseAmount = monthExpenseAmount;
        this.monthIncomeAmount = monthIncomeAmount;
        this.dailyCategoryExpenseAmount = dailyCategoryExpenseAmount;
        this.weeklyCategoryExpenseAmount = weeklyCategoryExpenseAmount;
        this.monthlyCategoryExpenseAmount = monthlyCategoryExpenseAmount;
        this.usersId = usersId;
    }

    /**********************************
     *  update method
     **********************************/
    public void update(String baseDate,
                       BigDecimal dayExpenseAmount,
                       BigDecimal dayIncomeAmount,
                       String dailyCategoryExpenseAmount){
        if(baseDate != null){
            this.baseDate = baseDate;
        }
        if(dayExpenseAmount != null){
            this.dayExpenseAmount = dayExpenseAmount;
        }
        if(dayIncomeAmount != null){
            this.dayIncomeAmount = dayIncomeAmount;
        }
        if(dailyCategoryExpenseAmount != null){
            this.dailyCategoryExpenseAmount = dailyCategoryExpenseAmount;
        }
    }

    /**********************************
     *  update method - 주배치
     **********************************/
    public void updateForWeekly(String baseDate,
                       BigDecimal weekExpenseAmount,
                       BigDecimal weekIncomeAmount,
                       String weeklyCategoryExpenseAmount){
        if(baseDate != null){
            this.baseDate = baseDate;
        }
        if(weekExpenseAmount != null){
            this.weekExpenseAmount = weekExpenseAmount;
        }
        if(weekIncomeAmount != null){
            this.weekIncomeAmount = weekIncomeAmount;
        }
        if(weeklyCategoryExpenseAmount != null){
            this.weeklyCategoryExpenseAmount = weeklyCategoryExpenseAmount;
        }

    }

    /**********************************
     *  update method - 월배치
     **********************************/
    public void updateForMonthly(String baseDate,
                       BigDecimal monthExpenseAmount,
                       BigDecimal monthIncomeAmount,
                       String monthlyCategoryExpenseAmount){
        if(baseDate != null){
            this.baseDate = baseDate;
        }
        if(monthExpenseAmount != null){
            this.monthExpenseAmount = monthExpenseAmount;
        }
        if(monthIncomeAmount != null){
            this.monthIncomeAmount = monthIncomeAmount;
        }
        if(monthlyCategoryExpenseAmount != null){
            this.monthlyCategoryExpenseAmount = monthlyCategoryExpenseAmount;
        }

    }

    /**********************************
     *  categoryExpenseAmount update method
     **********************************/
    public void updateCategoryExpenseAmount(Map<String, BigDecimal> dailyCategoryExpenseAmount, Statistics existingStatistics, PeriodType periodType) {
        String existingCategoryExpenseAmount = null;
        if (periodType == PeriodType.DAILY) {
            existingCategoryExpenseAmount = existingStatistics.getDailyCategoryExpenseAmount();
        } else if (periodType == PeriodType.WEEKLY) {
            existingCategoryExpenseAmount = existingStatistics.getWeeklyCategoryExpenseAmount();
        } else if (periodType == PeriodType.MONTHLY) {
            existingCategoryExpenseAmount = existingStatistics.getMonthlyCategoryExpenseAmount();
        }

        // 기존 카테고리 지출 금액을 Map으로 변환할 준비
        Map<String, BigDecimal> existingMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 기존의 카테고리 지출 금액이 있는 경우, JSON 문자열을 맵으로 변환
            if (existingCategoryExpenseAmount != null && !existingCategoryExpenseAmount.isEmpty()) {
                existingMap = objectMapper.readValue(existingCategoryExpenseAmount, new TypeReference<Map<String, BigDecimal>>() {});
            }
            for (Map.Entry<String, BigDecimal> entry : dailyCategoryExpenseAmount.entrySet()) {
                String categoryType = entry.getKey();
                BigDecimal amount = entry.getValue();


                BigDecimal existingAmount = existingMap.getOrDefault(categoryType, BigDecimal.ZERO);
                BigDecimal updatedAmount = existingAmount.add(amount);  // 기존 금액에 새 금액 더하기
                existingMap.put(categoryType, updatedAmount); // 맵에 업데이트된 금액 저장
            }

            String jsonCategoryExpenseAmount = objectMapper.writeValueAsString(existingMap);

            if (periodType == PeriodType.DAILY) {
                existingStatistics.setDailyCategoryExpenseAmount(jsonCategoryExpenseAmount);
            } else if (periodType == PeriodType.WEEKLY) {
                existingStatistics.setWeeklyCategoryExpenseAmount(jsonCategoryExpenseAmount);
            } else if (periodType == PeriodType.MONTHLY) {
                existingStatistics.setMonthlyCategoryExpenseAmount(jsonCategoryExpenseAmount);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error processing category expenses", e);
        }

    }

    /**********************************
     * getCategoryExpenseAmount
     **********************************/
    public BigDecimal getCategoryExpenseAmount(String existingCategoryExpenseAmount, String categoryType) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, BigDecimal> categoryExpenseMap = objectMapper.readValue(existingCategoryExpenseAmount, new TypeReference<Map<String, BigDecimal>>() {});
            BigDecimal value = categoryExpenseMap.getOrDefault(categoryType, BigDecimal.ZERO);

            return value;
        } catch (IOException e) {
            throw new RuntimeException("Error reading dailyCategoryExpenseAmount from JSON", e);
        }
    }


    /**********************************
     *  addAmount, subtractAmount
     **********************************/
    public void addExpense(BigDecimal amount) {
        this.dayExpenseAmount = this.dayExpenseAmount.add(amount);
    }
    public void addIncome(BigDecimal amount) {
        this.dayIncomeAmount = this.dayIncomeAmount.add(amount);
    }
    public void subtractExpense(BigDecimal amount) {
        this.dayExpenseAmount = this.dayExpenseAmount.subtract(amount);
    }
    public void subtractIncome(BigDecimal amount) {
        this.dayIncomeAmount = this.dayIncomeAmount.subtract(amount);
    }


    /**********************************
     *  getter
     **********************************/
    public Long getId() {
        return id;
    }

    public String getBaseDate() {
        return baseDate;
    }

    public BigDecimal getDayExpenseAmount() {
        return dayExpenseAmount;
    }

    public BigDecimal getDayIncomeAmount() {
        return dayIncomeAmount;
    }

    public BigDecimal getWeekExpenseAmount() {
        return weekExpenseAmount;
    }

    public BigDecimal getWeekIncomeAmount() {
        return weekIncomeAmount;
    }

    public BigDecimal getMonthExpenseAmount() {
        return monthExpenseAmount;
    }

    public BigDecimal getMonthIncomeAmount() {
        return monthIncomeAmount;
    }

    public String getDailyCategoryExpenseAmount() {
        return dailyCategoryExpenseAmount;
    }

    public String getWeeklyCategoryExpenseAmount() {
        return weeklyCategoryExpenseAmount;
    }

    public String getMonthlyCategoryExpenseAmount() {
        return monthlyCategoryExpenseAmount;
    }

    public Long getUsersId() {
        return usersId;
    }

    /**********************************
     *  Setter
     **********************************/
    public void setDailyCategoryExpenseAmount(String dailyCategoryExpenseAmount) {
        this.dailyCategoryExpenseAmount = dailyCategoryExpenseAmount;
    }

    public void setWeeklyCategoryExpenseAmount(String weeklyCategoryExpenseAmount) {
        this.weeklyCategoryExpenseAmount = weeklyCategoryExpenseAmount;
    }

    public void setMonthlyCategoryExpenseAmount(String monthlyCategoryExpenseAmount) {
        this.monthlyCategoryExpenseAmount = monthlyCategoryExpenseAmount;
    }

    /**********************************
     *  builder
     **********************************/
    public static class Builder {
        /** 아이디 */
        private Long id;
        /** 기준날짜 */
        private String baseDate;
        /** 일지출금액 */
        private BigDecimal dayExpenseAmount;
        /** 일수입금액 */
        private BigDecimal dayIncomeAmount;
        /** 주지출금액 */
        private BigDecimal weekExpenseAmount;
        /** 주수입금액 */
        private BigDecimal weekIncomeAmount;
        /** 월지출금액 */
        private BigDecimal monthExpenseAmount;
        /** 월수입금액 */
        private BigDecimal monthIncomeAmount;
        /** Users Id */
        private Long usersId;
        /** 카테고리별지출금액 (일별) */
        private String dailyCategoryExpenseAmount;
        /** 카테고리별지출금액 (주별) */
        private String weeklyCategoryExpenseAmount;
        /** 카테고리별지출금액 (월별) */
        private String monthlyCategoryExpenseAmount;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder baseDate(String baseDate) {
            this.baseDate = baseDate;
            return this;
        }

        public Builder dayExpenseAmount(BigDecimal dayExpenseAmount) {
            this.dayExpenseAmount = dayExpenseAmount;
            return this;
        }

        public Builder dayIncomeAmount(BigDecimal dayIncomeAmount) {
            this.dayIncomeAmount = dayIncomeAmount;
            return this;
        }

        public Builder weekExpenseAmount(BigDecimal weekExpenseAmount) {
            this.weekExpenseAmount = weekExpenseAmount;
            return this;
        }

        public Builder weekIncomeAmount(BigDecimal weekIncomeAmount) {
            this.weekIncomeAmount = weekIncomeAmount;
            return this;
        }

        public Builder monthExpenseAmount(BigDecimal monthExpenseAmount) {
            this.monthExpenseAmount = monthExpenseAmount;
            return this;
        }

        public Builder monthIncomeAmount(BigDecimal monthIncomeAmount) {
            this.monthIncomeAmount = monthIncomeAmount;
            return this;
        }

        public Builder dailyCategoryExpenseAmount(String dailyCategoryExpenseAmount) {
            this.dailyCategoryExpenseAmount = dailyCategoryExpenseAmount;
            return this;
        }

        public Builder weeklyCategoryExpenseAmount(String weeklyCategoryExpenseAmount) {
            this.weeklyCategoryExpenseAmount = weeklyCategoryExpenseAmount;
            return this;
        }

        public Builder monthlyCategoryExpenseAmount(String monthlyCategoryExpenseAmount) {
            this.monthlyCategoryExpenseAmount = monthlyCategoryExpenseAmount;
            return this;
        }

        public Builder usersId(Long usersId) {
            this.usersId = usersId;
            return this;
        }

        public Statistics build() {

            Statistics statistics = new Statistics(id, baseDate, dayExpenseAmount, dayIncomeAmount, weekExpenseAmount, weekIncomeAmount, monthExpenseAmount, monthIncomeAmount, dailyCategoryExpenseAmount, weeklyCategoryExpenseAmount, monthlyCategoryExpenseAmount, usersId);

            return statistics;
        }

    }

    public static Builder builder() {
        return new Builder();
    }
}
