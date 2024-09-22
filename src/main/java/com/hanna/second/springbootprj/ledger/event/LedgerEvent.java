package com.hanna.second.springbootprj.ledger.event;

import com.hanna.second.springbootprj.ledger.domain.Ledger;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

@Component
public class LedgerEvent {

    public static LedgerSavedEvent ledgerSavedEvent(Object source, Long ledgerId) {
        return new LedgerSavedEvent(source, ledgerId);
    }

    public static LedgerUpdatedEvent ledgerUpdatedEvent(Object source, Long ledgerId) {
        return new LedgerUpdatedEvent(source, ledgerId);
    }

    public static LedgerDeletedEvent ledgerDeletedEvent(Object source, Ledger ledger) {
        return new LedgerDeletedEvent(source, ledger);
    }

    // 등록 후
    public static class LedgerSavedEvent extends ApplicationEvent {
        private final Long ledgerId;

        public LedgerSavedEvent(Object source, Long ledgerId) {
            super(source);
            this.ledgerId = ledgerId;
        }

        public Long getLedgerId() {
            return ledgerId;
        }
    }

    // 수정 후
    public static class LedgerUpdatedEvent extends ApplicationEvent {
        private final Long ledgerId;

        public LedgerUpdatedEvent(Object source, Long ledgerId) {
            super(source);
            this.ledgerId = ledgerId;
        }

        public Long getLedgerId() {
            return ledgerId;
        }
    }

    // 삭제 후
    public static class LedgerDeletedEvent extends ApplicationEvent {
        private final Ledger entity;

        public LedgerDeletedEvent(Object source, Ledger entity) {
            super(source);
            this.entity = entity;
        }

        public Ledger getLedger() {
            return entity;
        }
    }
}