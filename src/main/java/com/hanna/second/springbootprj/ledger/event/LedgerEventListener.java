package com.hanna.second.springbootprj.ledger.event;

import com.hanna.second.springbootprj.ledger.domain.Ledger;
import com.hanna.second.springbootprj.ledger.infra.LedgerJpaRepository;
import com.hanna.second.springbootprj.statistics.service.StatisticsService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class LedgerEventListener {

    private final StatisticsService statisticsService;
    private final LedgerJpaRepository ledgerRepository;

    public LedgerEventListener(StatisticsService statisticsService, LedgerJpaRepository ledgerRepository) {
        this.statisticsService = statisticsService;
        this.ledgerRepository = ledgerRepository;
    }

    @EventListener
    public void handleLedgerSavedEvent(LedgerEvent.LedgerSavedEvent event) {
        Long ledgerId = event.getLedgerId();
        ledgerRepository.findById(ledgerId)
                .ifPresent(ledger -> statisticsService.saveStatistics(ledger));
    }

    @EventListener
    public void handleLedgerUpdatedEvent(LedgerEvent.LedgerUpdatedEvent event) {
        Long ledgerId = event.getLedgerId();
        ledgerRepository.findById(ledgerId)
                .ifPresent(ledger -> statisticsService.updateStatistics(ledger));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLedgerDeletedEvent(LedgerEvent.LedgerDeletedEvent event) {
        Ledger ledger = event.getLedger();
        statisticsService.afterDeleteUpdateStatistics(ledger);
    }

}
