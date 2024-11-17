package com.hanna.second.springbootprj.ledger.event;

import com.hanna.second.springbootprj.ledger.domain.Ledger;

public class LedgerEvent {

    public static LedgerSavedEvent ledgerSavedEvent(Long ledgerId) {
        return new LedgerSavedEvent(ledgerId);
    }

    public static LedgerUpdatedEvent ledgerUpdatedEvent(Long ledgerId) {
        return new LedgerUpdatedEvent(ledgerId);
    }

    public static LedgerDeletedEvent ledgerDeletedEvent(Ledger ledger) {
        return new LedgerDeletedEvent(ledger);
    }

    // 등록 후
    public static class LedgerSavedEvent {
        private final Long ledgerId;

        public LedgerSavedEvent(Long ledgerId) {
            this.ledgerId = ledgerId;
        }

        public Long getLedgerId() {
            return ledgerId;
        }
    }

    // 수정 후
    public static class LedgerUpdatedEvent {
        private final Long ledgerId;

        public LedgerUpdatedEvent(Long ledgerId) {
            this.ledgerId = ledgerId;
        }

        public Long getLedgerId() {
            return ledgerId;
        }
    }

    // 삭제 후
    public static class LedgerDeletedEvent {
        private final Ledger entity;

        public LedgerDeletedEvent(Ledger entity) {
            this.entity = entity;
        }

        public Ledger getLedger() {
            return entity;
        }
    }
}