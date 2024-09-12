package com.hanna.second.springbootprj.statistics.dto;

import com.hanna.second.springbootprj.ledger.domain.Ledger;
import com.hanna.second.springbootprj.statistics.domain.Statistics;
import com.hanna.second.springbootprj.support.enums.AssetType;
import com.hanna.second.springbootprj.support.enums.CategoryType;
import com.hanna.second.springbootprj.support.enums.TransactionType;

import java.math.BigDecimal;

public class StatisticsRequestDto {

    /** 아이디 */
    private Long id;

    /** 기준날짜 */
    private String baseDate;

    /** 거래유형 */
    private TransactionType transactionType;

    /** 자산유형 */
    private AssetType assetType;

    /** 카테고리 */
    private CategoryType categoryType;

    /** 금액 */
    private BigDecimal amount;

    /** Users Id */
    private Long usersId;

    /**********************************
     *  constructor
     **********************************/
    public StatisticsRequestDto() {
    }
    public StatisticsRequestDto(Long id, String baseDate, TransactionType transactionType, AssetType assetType, CategoryType categoryType, BigDecimal amount, Long usersId) {
        this.id = id;
        this.baseDate = baseDate;
        this.transactionType = transactionType;
        this.assetType = assetType;
        this.categoryType = categoryType;
        this.amount = amount;
        this.usersId = usersId;
    }

    /**********************************
     *  toEntity
     **********************************/
    public Statistics toEntity(){
        return Statistics.builder()
                .baseDate(baseDate)
                .transactionType(transactionType)
                .assetType(assetType)
                .categoryType(categoryType)
                .amount(amount)
                .usersId(usersId)
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

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public BigDecimal getAmount() {
        return amount;
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
        /** 거래유형 */
        private TransactionType transactionType;
        /** 자산유형 */
        private AssetType assetType;
        /** 카테고리 */
        private CategoryType categoryType;
        /** 금액 */
        private BigDecimal amount;
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


        public Builder transactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public Builder assetType(AssetType assetType) {
            this.assetType = assetType;
            return this;
        }

        public Builder categoryType(CategoryType categoryType) {
            this.categoryType = categoryType;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder usersId(Long usersId) {
            this.usersId = usersId;
            return this;
        }

        public StatisticsRequestDto build() {
            return new StatisticsRequestDto(id, baseDate, transactionType, assetType, categoryType, amount, usersId);
        }

    }

    public static Builder builder() {
        return new Builder();
    }
}
