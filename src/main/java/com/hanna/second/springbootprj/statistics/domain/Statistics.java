package com.hanna.second.springbootprj.statistics.domain;

import javax.persistence.*;
import java.math.BigDecimal;

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
    @Column(nullable = false)
    private BigDecimal dayExpenseAmount;

    /** 일수입금액 */
    @Column(nullable = false)
    private BigDecimal dayIncomeAmount;

    /** Users Id */
    private Long usersId;

    /**********************************
     *  constructor
     **********************************/
    public Statistics(){
    }

    public Statistics(Long id, String baseDate, BigDecimal dayExpenseAmount, BigDecimal dayIncomeAmount, Long usersId) {
        this.id = id;
        this.baseDate = baseDate;
        this.dayExpenseAmount = dayExpenseAmount;
        this.dayIncomeAmount = dayIncomeAmount;
        this.usersId = usersId;
    }

    /**********************************
     *  update method
     **********************************/
    public void update(String baseDate,
                       BigDecimal dayExpenseAmount,
                       BigDecimal dayIncomeAmount){
        if(baseDate != null){
            this.baseDate = baseDate;
        }
        if(dayExpenseAmount != null){
            this.dayExpenseAmount = dayExpenseAmount;
        }
        if(dayIncomeAmount != null){
            this.dayIncomeAmount = dayIncomeAmount;
        }

    }

    /**********************************
     *  toStatistics
     **********************************/
//    public Statistics toStatistics(Ledger ledger){
//        return Statistics.builder()
//                .baseDate(ledger.getBaseDate())
//                .transactionType(ledger.getTransactionType())
//                .assetType(ledger.getAssetType())
//                .categoryType(ledger.getCategoryType())
//                .amount(ledger.getAmount())
//                .build();
//    }

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

        public Statistics build() {
            return new Statistics(id, baseDate, dayExpenseAmount, dayIncomeAmount, usersId);
        }

    }

    public static Builder builder() {
        return new Builder();
    }
}
