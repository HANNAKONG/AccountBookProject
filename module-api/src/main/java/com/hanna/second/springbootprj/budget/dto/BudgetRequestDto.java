package com.hanna.second.springbootprj.budget.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanna.second.springbootprj.budget.domain.Budget;
import com.hanna.second.springbootprj.statistics.domain.Statistics;
import com.hanna.second.springbootprj.support.Money;

import java.math.BigDecimal;
import java.util.Map;

public class BudgetRequestDto {

    /** 아이디 */
    private Long id;

    /** 적용연월 */
    private String yearMonth;

    /** 총예산금액(월별) */
    private BigDecimal totalAmount;

    /** 카테고리별지출금액 */
    private String categoryExpenseAmount;

    /** Users Id */
    private Long usersId;

    /**********************************
     *  constructor
     **********************************/
    public BudgetRequestDto() {
    }

    public BudgetRequestDto(Long id, String yearMonth, BigDecimal totalAmount, String categoryExpenseAmount, Long usersId) {
        this.id = id;
        this.yearMonth = yearMonth;
        this.totalAmount = totalAmount;
        this.categoryExpenseAmount = categoryExpenseAmount;
        this.usersId = usersId;
    }
    /**********************************
     *  toEntity
     **********************************/
    public Budget toEntity(){
        return Budget.builder()
                .id(id)
                .yearMonth(yearMonth)
                .totalAmount(totalAmount)
                .categoryExpenseAmount(categoryExpenseAmount)
                .usersId(usersId)
                .build();
    }

    /**********************************
     *  getter
     **********************************/
    public Long getId() {
        return id;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getCategoryExpenseAmount() {
        return categoryExpenseAmount;
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
        /** 적용연월 */
        private String yearMonth;
        /** 총예산금액(월별) */
        private BigDecimal totalAmount;
        /** 카테고리별지출금액 */
        private String categoryExpenseAmount;
        /** Users Id */
        private Long usersId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder yearMonth(String yearMonth) {
            this.yearMonth = yearMonth;
            return this;
        }

        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder categoryExpenseAmount(String categoryExpenseAmount) {
            this.categoryExpenseAmount = categoryExpenseAmount;
            return this;
        }


        public Builder usersId(Long usersId) {
            this.usersId = usersId;
            return this;
        }

        public BudgetRequestDto build() {
            return new BudgetRequestDto (id, yearMonth, totalAmount, categoryExpenseAmount, usersId);
        }

    }

    public static Builder builder() {
        return new Builder();
    }

}
