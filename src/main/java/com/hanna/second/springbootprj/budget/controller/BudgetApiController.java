package com.hanna.second.springbootprj.budget.controller;

import com.hanna.second.springbootprj.budget.dto.BudgetRequestDto;
import com.hanna.second.springbootprj.budget.dto.BudgetResponseDto;
import com.hanna.second.springbootprj.budget.serivce.BudgetService;
import com.hanna.second.springbootprj.ledger.dto.LedgerRequestDto;
import com.hanna.second.springbootprj.ledger.dto.LedgerResponseDto;
import com.hanna.second.springbootprj.ledger.service.LedgerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/budgets")
public class BudgetApiController {
    private final BudgetService budgetService;

    public BudgetApiController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    /**********************************
     *  1. 예산내역과 지난 달 지출내역 함께 조회
     **********************************/
    @GetMapping()
    public BudgetResponseDto getBudget(@RequestBody BudgetRequestDto requestDto){
        return budgetService.getBudgetWithStatistics(requestDto);
    }

    /**********************************
     *  2. 예산내역(월별, 카테고리별) 등록 or 수정
     **********************************/
    @PostMapping()
    public void saveOrUpdateBudget(@RequestBody BudgetRequestDto requestDto){
        budgetService.saveOrUpdateBudget(requestDto);
    }

}
