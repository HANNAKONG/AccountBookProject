package com.hanna.second.springbootprj.statistics.dto;

import com.hanna.second.springbootprj.statistics.domain.Statistics;
import com.hanna.second.springbootprj.support.enums.PeriodType;

import java.math.BigDecimal;

public class StatisticsRequestDto {

    /** 아이디 */
    private Long id;
    /** 기준날짜 */
    private String baseDate;
    /** 일별주별월별구분 */
    private PeriodType periodType;
    /** 일지출금액 */
    private BigDecimal dayExpenseAmount;
    /** 일수입금액 */
    private BigDecimal dayIncomeAmount;
    /** Users Id */
    private Long usersId;

    /**********************************
     *  constructor
     **********************************/
    public StatisticsRequestDto() {
    }

    public StatisticsRequestDto(Long id, String baseDate, PeriodType periodType, BigDecimal dayExpenseAmount, BigDecimal dayIncomeAmount, Long usersId) {
        this.id = id;
        this.baseDate = baseDate;
        this.periodType = periodType;
        this.dayExpenseAmount = dayExpenseAmount;
        this.dayIncomeAmount = dayIncomeAmount;
        this.usersId = usersId;
    }

    /**********************************
     *  toEntity
     **********************************/
    public Statistics toEntity(){
        return Statistics.builder()
                .baseDate(baseDate)
                .dayExpenseAmount(dayExpenseAmount)
                .dayIncomeAmount(dayIncomeAmount)
                .build();
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

    public PeriodType getPeriodType() {
        return periodType;
    }

    public BigDecimal getDayExpenseAmount() {
        return dayExpenseAmount;
    }

    public BigDecimal getDayIncomeAmount() {
        return dayIncomeAmount;
    }

    public Long getUsersId() {
        return usersId;
    }

    /**********************************
     *  builder
     **********************************/
    public static class Builder {
        /** 아이디 */
        private Long id;
        /** 기준날짜 */
        private String baseDate;
        /** 일별주별월별구분 */
        private PeriodType periodType;
        /** 일지출금액 */
        private BigDecimal dayExpenseAmount;
        /** 일수입금액 */
        private BigDecimal dayIncomeAmount;
        /** Users Id */
        private Long usersId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder baseDate(String baseDate) {
            this.baseDate = baseDate;
            return this;
        }

        public Builder periodType(PeriodType periodType) {
            this.periodType = periodType;
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

        public Builder usersId(Long usersId) {
            this.usersId = usersId;
            return this;
        }

        public StatisticsRequestDto build() {
            return new StatisticsRequestDto(id, baseDate, periodType, dayExpenseAmount, dayIncomeAmount, usersId);
        }

    }

    public static Builder builder() {
        return new Builder();
    }
}
