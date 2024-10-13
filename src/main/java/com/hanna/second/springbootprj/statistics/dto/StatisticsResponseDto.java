package com.hanna.second.springbootprj.statistics.dto;

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
