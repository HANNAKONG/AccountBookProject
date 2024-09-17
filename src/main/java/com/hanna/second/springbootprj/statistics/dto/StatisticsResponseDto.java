package com.hanna.second.springbootprj.statistics.dto;

import java.math.BigDecimal;
import java.util.Optional;

public class StatisticsResponseDto {

    /** 합계금액 */
    private Optional<BigDecimal> amountSum;

    /**********************************
     *  constructor
     **********************************/
    public StatisticsResponseDto(Optional<BigDecimal> amountSum) {
        this.amountSum = amountSum;
    }

    /**********************************
     *  getter
     **********************************/
    public Optional<BigDecimal> getAmountSum() {
        return amountSum;
    }

}
