package com.hanna.second.springbootprj.ledger.event;

import com.hanna.second.springbootprj.ledger.domain.Ledger;
import com.hanna.second.springbootprj.ledger.domain.LedgerRepositoryImpl;
import com.hanna.second.springbootprj.statistics.domain.Statistics;
import com.hanna.second.springbootprj.statistics.service.StatisticsService;
import com.hanna.second.springbootprj.support.EntityMapper;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LedgerEventListener {

    private final StatisticsService statisticsService;
    private final LedgerRepositoryImpl ledgerRepository;

    public LedgerEventListener(StatisticsService statisticsService, LedgerRepositoryImpl ledgerRepository) {
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

    @EventListener
    public void handleLedgerDeletedEvent(LedgerEvent.LedgerDeletedEvent event) {
        Ledger ledger = event.getLedger();
        statisticsService.updateDeleteStatistics(ledger);
    }

}
