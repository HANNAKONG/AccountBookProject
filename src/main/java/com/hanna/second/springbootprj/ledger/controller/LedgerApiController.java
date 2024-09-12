package com.hanna.second.springbootprj.ledger.controller;

import com.hanna.second.springbootprj.ledger.dto.LedgerRequestDto;
import com.hanna.second.springbootprj.ledger.dto.LedgerResponseDto;
import com.hanna.second.springbootprj.ledger.service.LedgerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LedgerApiController {
    private final LedgerService ledgerService;

    public LedgerApiController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    /**********************************
     *  1. 수입/지출내역 목록 조회
     *  - 필터 적용: 기간별, 거래유형별, 카테고리별, 자산(결재수단)별
     *  - 키워드 조회: 내역명, 메모
     **********************************/
    @GetMapping("/ledger")
    public List<LedgerResponseDto> getLedgerList(@RequestBody LedgerRequestDto requestDto) {
        return ledgerService.getLedgerList(requestDto);
    }

    /**********************************
     *  2. 수입/지출내역 단건 조회
     **********************************/
    @GetMapping("/ledger/{id}")
    public LedgerResponseDto getLedger(@PathVariable Long id){
        return ledgerService.getLedger(id);
    }

    /**********************************
     *  3. 수입/지출내역 등록
     **********************************/
    @PostMapping("/ledger")
    public void saveLedger(@RequestBody LedgerRequestDto requestDto){
        ledgerService.saveLedger(requestDto);
    }

    /**********************************
     *  4. 수입/지출내역 수정
     **********************************/
    @PatchMapping("/ledger/{id}")
    public void updateLedger(@PathVariable Long id, @RequestBody LedgerRequestDto requestDto){
        ledgerService.updateLedger(id, requestDto);
    }

    /**********************************
     *  5. 수입/지출내역 삭제
     **********************************/
    @DeleteMapping("/ledger/{id}")
    public void deleteLedger(@PathVariable Long id){
        ledgerService.deleteLedger(id);
    }

}
