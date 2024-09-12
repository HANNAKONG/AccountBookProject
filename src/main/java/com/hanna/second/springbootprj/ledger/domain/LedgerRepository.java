package com.hanna.second.springbootprj.ledger.domain;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface LedgerRepository extends JpaSpecificationExecutor<Ledger> {
    void delete(Ledger ledger);
    Ledger save(Ledger ledger);
    Optional<Ledger> findById(Long Id);
}
