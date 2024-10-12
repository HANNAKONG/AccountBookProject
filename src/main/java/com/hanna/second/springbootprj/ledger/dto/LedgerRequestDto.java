package com.hanna.second.springbootprj.ledger.dto;

import com.hanna.second.springbootprj.ledger.domain.Ledger;
import com.hanna.second.springbootprj.support.Money;
import com.hanna.second.springbootprj.support.enums.AssetType;
import com.hanna.second.springbootprj.support.enums.CategoryType;
import com.hanna.second.springbootprj.support.enums.TransactionType;

import java.math.BigDecimal;

public class LedgerRequestDto {

    /** 아이디 */
    private Long id;

    /** 시작날짜 */
    private String startDate;

    /** 종료날짜 */
    private String endDate;

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

    /** 내역명 */
    private String description;

    /** 메모 */
    private String memo;

    /** Users Id */
    private Long usersId;

    /**********************************
     *  constructor
     **********************************/
    public LedgerRequestDto() {
    }
    public LedgerRequestDto(Long id, String startDate, String endDate, String baseDate, TransactionType transactionType, AssetType assetType, CategoryType categoryType, BigDecimal amount, String description, String memo, Long usersId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.baseDate = baseDate;
        this.transactionType = transactionType;
        this.assetType = assetType;
        this.categoryType = categoryType;
        this.amount = amount;
        this.description = description;
        this.memo = memo;
        this.usersId = usersId;
    }

    /**********************************
     *  toEntity
     **********************************/
    public Ledger toEntity(){
        return Ledger.builder()
                .baseDate(baseDate)
                .transactionType(transactionType)
                .assetType(assetType)
                .categoryType(categoryType)
                .amount(amount)
                .description(description)
                .memo(memo)
                .usersId(usersId)
                .build();
    }

    /**********************************
     *  getter
     **********************************/
    public Long getId() {
        return id;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
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

    public String getDescription() {
        return description;
    }

    public String getMemo() {
        return memo;
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
        /** 시작날짜 */
        private String startDate;
        /** 종료날짜 */
        private String endDate;
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
        /** 내역명 */
        private String description;
        /** 메모 */
        private String memo;
        /** Users Id */
        private Long usersId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder startDate(String startDate) { // 추가
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(String endDate) { // 추가
            this.endDate = endDate;
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

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder memo(String memo) {
            this.memo = memo;
            return this;
        }

        public Builder usersId(Long usersId) {
            this.usersId = usersId;
            return this;
        }

        public LedgerRequestDto build() {
            return new LedgerRequestDto(id, startDate, endDate, baseDate, transactionType, assetType, categoryType, amount, description, memo, usersId);
        }

    }

    public static Builder builder() {
        return new Builder();
    }
}
