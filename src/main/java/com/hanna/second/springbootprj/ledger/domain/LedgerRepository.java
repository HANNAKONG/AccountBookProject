package com.hanna.second.springbootprj.ledger.domain;

import com.hanna.second.springbootprj.ledger.dto.LedgerRequestDto;

import java.util.List;
import java.util.Optional;

public interface LedgerRepository {
    void delete(Ledger ledger);
    Ledger save(Ledger ledger);
    Optional<Ledger> findById(Long Id);
    List<Ledger> findAllByFilter(LedgerRequestDto ledgerRequestDto);
}
