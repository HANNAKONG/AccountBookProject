package com.hanna.second.springbootprj.ledger.domain;

import com.hanna.second.springbootprj.support.BaseTime;
import com.hanna.second.springbootprj.support.Money;
import com.hanna.second.springbootprj.support.enums.AssetType;
import com.hanna.second.springbootprj.support.enums.CategoryType;
import com.hanna.second.springbootprj.support.enums.TransactionType;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Ledger extends BaseTime {

    /** 아이디 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 기준날짜 */
    @Column(nullable = false)
    private String baseDate;

    /** 거래유형 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    /** 자산유형 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetType assetType;

    /** 카테고리 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType categoryType;

    /** 금액 */
    private BigDecimal amount;
    
    /** 내역명 */
    @Column
    private String description;

    /** 메모 */
    @Column
    private String memo;

    /** Users Id */
    private Long usersId;

    /**********************************
     *  constructor
     **********************************/
    public Ledger() {
    }
    public Ledger(Long id, String baseDate, TransactionType transactionType, AssetType assetType, CategoryType categoryType, BigDecimal amount, String description, String memo, Long usersId) {
        this.id = id;
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
     *  update method
     **********************************/
    public void update(String baseDate, TransactionType transactionType, AssetType assetType, CategoryType categoryType, BigDecimal amount, String description, String memo){
        if(baseDate != null){
            this.baseDate = baseDate;
        }
        if(transactionType != null){
            this.transactionType = transactionType;
        }
        if(assetType != null){
            this.assetType = assetType;
        }
        if(categoryType != null){
            this.categoryType = categoryType;
        }
        if(amount != null){
            this.amount = amount;
        }
        if(description != null){
            this.description = description;
        }
        if(memo != null){
            this.memo = memo;
        }
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

        public Ledger build() {
            return new Ledger(id, baseDate, transactionType, assetType, categoryType, amount, description, memo, usersId);
        }

    }

    public static Builder builder() {
        return new Builder();
    }
}
