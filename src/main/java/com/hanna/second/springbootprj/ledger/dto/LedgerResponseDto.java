package com.hanna.second.springbootprj.ledger.dto;

import com.hanna.second.springbootprj.ledger.domain.Ledger;
import com.hanna.second.springbootprj.support.Money;
import com.hanna.second.springbootprj.support.enums.AssetType;
import com.hanna.second.springbootprj.support.enums.CategoryType;
import com.hanna.second.springbootprj.support.enums.TransactionType;

import java.math.BigDecimal;

public class LedgerResponseDto {

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

    /** 정기거래여부 */
    private Boolean recurringYn;

    /** Users Id */
    private Long usersId;

    /**********************************
     *  constructor
     **********************************/
    public LedgerResponseDto(Ledger entity) {
        this.id = entity.getId();
        this.baseDate = entity.getBaseDate();
        this.transactionType = entity.getTransactionType();
        this.assetType = entity.getAssetType();
        this.categoryType = entity.getCategoryType();
        this.amount = entity.getAmount();
        this.description = entity.getDescription();
        this.memo = entity.getMemo();
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

    public Boolean getRecurringYn() {
        return recurringYn;
    }

    public Long getUsersId() {
        return usersId;
    }
}
