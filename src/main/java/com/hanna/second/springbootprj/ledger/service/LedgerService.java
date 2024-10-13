package com.hanna.second.springbootprj.ledger.service;

import com.hanna.second.springbootprj.ledger.domain.Ledger;
import com.hanna.second.springbootprj.ledger.dto.LedgerRequestDto;
import com.hanna.second.springbootprj.ledger.dto.LedgerResponseDto;
import com.hanna.second.springbootprj.ledger.event.LedgerEvent;
import com.hanna.second.springbootprj.ledger.infra.LedgerJpaRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LedgerService {

//    private final BudgetClient budgetClient;
//
//    public LedgerService(BudgetClient budgetClient) {
//        this.budgetClient = budgetClient;
//    }

    private final LedgerJpaRepository ledgerRepository;
    private final ApplicationEventPublisher eventPublisher;

    public LedgerService(final LedgerJpaRepository ledgerRepository, ApplicationEventPublisher eventPublisher) {
        this.ledgerRepository = ledgerRepository;
        this.eventPublisher = eventPublisher;
    }

    /**********************************
     *  1. 수입/지출내역 목록 조회
     *  - 필터 적용: 기간별, 거래유형별, 카테고리별, 자산(결재수단)별
     *  - 키워드 조회: 내역명, 메모
     **********************************/
    @Transactional(readOnly = true)
    public List<LedgerResponseDto> getLedgerList(final LedgerRequestDto requestDto) {

        Sort sortInfo = Sort.by(Sort.Direction.DESC, "baseDate", "id");
        Pageable pageable = PageRequest.of(0, 10, sortInfo);

        Page<Ledger> entityLedgerList = ledgerRepository.findAllByFilter(requestDto, pageable);

        return entityLedgerList.stream()
                .map(LedgerResponseDto::new)
                .collect(Collectors.toList());

    }


    /**********************************
     *  2. 수입/지출내역 단건 조회
     **********************************/
    public LedgerResponseDto getLedger(final Long id){
        final Ledger entity = ledgerRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 내역이 없습니다.")
        );

        return new LedgerResponseDto(entity);
    }

    /**********************************
     *  3. 수입/지출내역 등록
     **********************************/
    @Transactional
    public void saveLedger(final LedgerRequestDto requestDto){
        final Ledger entity = requestDto.toEntity();
        ledgerRepository.save(entity);

        eventPublisher.publishEvent(LedgerEvent.ledgerSavedEvent(entity.getId()));
    }

    /**********************************
     *  4. 수입/지출내역 수정
     **********************************/
    @Transactional
    public void updateLedger(final Long id, final LedgerRequestDto requestDto){
        final Ledger entity = ledgerRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 내역이 없습니다.")
        );

        entity.update(requestDto.getBaseDate(),
                        requestDto.getTransactionType(),
                        requestDto.getAssetType(),
                        requestDto.getCategoryType(),
                        requestDto.getAmount(),
                        requestDto.getDescription(),
                        requestDto.getMemo()
                    );

        eventPublisher.publishEvent(LedgerEvent.ledgerUpdatedEvent(entity.getId()));
    }

    /**********************************
     *  5. 수입/지출내역 삭제
     **********************************/
    @Transactional
    public void deleteLedger(final Long id){
        final Ledger entity = ledgerRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 내역이 없습니다.")
        );
        ledgerRepository.delete(entity);

        ledgerRepository.flush();

        System.out.println("delete===============>"+entity.getAmount());

        eventPublisher.publishEvent(LedgerEvent.ledgerDeletedEvent(entity));
    }
}
