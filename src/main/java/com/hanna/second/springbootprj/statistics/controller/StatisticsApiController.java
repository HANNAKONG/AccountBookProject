package com.hanna.second.springbootprj.statistics.controller;

import com.hanna.second.springbootprj.statistics.dto.StatisticsRequestDto;
import com.hanna.second.springbootprj.statistics.dto.StatisticsResponseDto;
import com.hanna.second.springbootprj.statistics.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
public class StatisticsApiController {
    private final StatisticsService statisticsService;

    public StatisticsApiController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**********************************
     *  1-1. 통계 조회: 기간별 지출금액
     **********************************/
    @GetMapping("/expense-by-period")
    public StatisticsResponseDto getExpenseByPeriod(@RequestBody StatisticsRequestDto requestDto){
        return statisticsService.getExpenseByPeriod(requestDto);
    }

    /**********************************
     *  2. 통계 조회: 수입금액
     **********************************/
//    @GetMapping("/income")
//    public StatisticsResponseDto getTotalIncome(@RequestBody StatisticsRequestDto requestDto){
//        return statisticsService.getTotalIncome(requestDto);
//    }

    /**********************************
     *  2. 통계 조회: 지출금액 - 수입금액
     **********************************/
//    @GetMapping("/total")
//    public StatisticsResponseDto getIncomeMinusExpense(@RequestBody StatisticsRequestDto requestDto){
//        return statisticsService.getIncomeMinusExpense(requestDto);
//    }

}
