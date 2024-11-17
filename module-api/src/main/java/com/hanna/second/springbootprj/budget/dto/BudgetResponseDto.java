package com.hanna.second.springbootprj.budget.dto;

import com.hanna.second.springbootprj.budget.domain.Budget;
import com.hanna.second.springbootprj.ledger.domain.Ledger;
import com.hanna.second.springbootprj.support.Money;
import com.hanna.second.springbootprj.support.enums.AssetType;
import com.hanna.second.springbootprj.support.enums.CategoryType;
import com.hanna.second.springbootprj.support.enums.TransactionType;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Map;

public class BudgetResponseDto {

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

    ///////////////////////////////////////
    // statistics
    //////////////////////////////////////
    /** 월지출금액 */
    private BigDecimal monthExpenseAmount;

    /** 카테고리별지출금액 (월별) */
    private String monthlyCategoryExpenseAmount;

    /**********************************
     *  constructor
     **********************************/
    public BudgetResponseDto(Budget entity) {
        this.id = entity.getId();
        this.yearMonth = entity.getYearMonth();
        this.totalAmount = entity.getTotalAmount();
        this.categoryExpenseAmount = entity.getCategoryExpenseAmount();
        this.usersId = entity.getUsersId();
    }
    public BudgetResponseDto(Long id, String yearMonth, BigDecimal totalAmount, String categoryExpenseAmount,
                             BigDecimal monthExpenseAmount, String monthlyCategoryExpenseAmount, Long usersId) {
        this.id = id;
        this.yearMonth = yearMonth;
        this.totalAmount = totalAmount;
        this.categoryExpenseAmount = categoryExpenseAmount;
        this.monthExpenseAmount = monthExpenseAmount;
        this.monthlyCategoryExpenseAmount = monthlyCategoryExpenseAmount;
        this.usersId = usersId;
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

    public BigDecimal getMonthExpenseAmount() {
        return monthExpenseAmount;
    }

    public String getMonthlyCategoryExpenseAmount() {
        return monthlyCategoryExpenseAmount;
    }
}
