package com.hanna.second.springbootprj.statistics.dto;

import com.hanna.second.springbootprj.ledger.domain.Ledger;
import com.hanna.second.springbootprj.statistics.domain.Statistics;
import com.hanna.second.springbootprj.support.enums.AssetType;
import com.hanna.second.springbootprj.support.enums.CategoryType;
import com.hanna.second.springbootprj.support.enums.TransactionType;

import java.math.BigDecimal;

public class StatisticsResponseDto {

    /** 합계금액 */
    private BigDecimal amountSum;

    /**********************************
     *  constructor
     **********************************/
    public StatisticsResponseDto(BigDecimal amountSum) {
        this.amountSum = amountSum;
    }

    /**********************************
     *  getter
     **********************************/
    public BigDecimal getAmountSum() {
        return amountSum;
    }

}
