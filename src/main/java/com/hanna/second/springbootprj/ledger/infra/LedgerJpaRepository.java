package com.hanna.second.springbootprj.ledger.infra;

import com.hanna.second.springbootprj.ledger.domain.Ledger;
import com.hanna.second.springbootprj.ledger.domain.LedgerRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LedgerJpaRepository extends JpaRepository<Ledger, Long>, LedgerRepository {
    @Override
    void delete(Ledger entity);

    @Override
    Ledger save(Ledger entity);

    @Override
    Optional<Ledger> findById(Long Id);

}
