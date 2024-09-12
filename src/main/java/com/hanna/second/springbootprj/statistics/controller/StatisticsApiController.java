package com.hanna.second.springbootprj.statistics.controller;

import com.hanna.second.springbootprj.ledger.dto.LedgerRequestDto;
import com.hanna.second.springbootprj.ledger.dto.LedgerResponseDto;
import com.hanna.second.springbootprj.ledger.service.LedgerService;
import com.hanna.second.springbootprj.statistics.dto.StatisticsRequestDto;
import com.hanna.second.springbootprj.statistics.dto.StatisticsResponseDto;
import com.hanna.second.springbootprj.statistics.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StatisticsApiController {
    private final StatisticsService statisticsService;

    public StatisticsApiController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**********************************
     *  1. 통계 조회: 지출금액
     **********************************/
    @GetMapping("/statistics/expense")
    public StatisticsResponseDto getTotalExpense(@RequestBody StatisticsRequestDto requestDto){
        return statisticsService.getTotalExpense(requestDto);
    }

    /**********************************
     *  2. 통계 조회: 수입금액
     **********************************/
    @GetMapping("/statistics/income")
    public StatisticsResponseDto getTotalIncome(@RequestBody StatisticsRequestDto requestDto){
        return statisticsService.getTotalIncome(requestDto);
    }

    /**********************************
     *  2. 통계 조회: 지출금액 - 수입금액
     **********************************/
    @GetMapping("/statistics/total")
    public StatisticsResponseDto getIncomeMinusExpense(@RequestBody StatisticsRequestDto requestDto){
        return statisticsService.getIncomeMinusExpense(requestDto);
    }

}
