package com.hanna.second.springbootprj.ledger.infra;

import com.hanna.second.springbootprj.ledger.domain.Ledger;
import com.hanna.second.springbootprj.ledger.domain.LedgerRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerJpaRepository extends JpaRepository<Ledger, Long>, LedgerRepositoryCustom {

}
