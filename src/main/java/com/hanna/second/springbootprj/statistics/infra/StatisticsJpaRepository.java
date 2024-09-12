package com.hanna.second.springbootprj.statistics.infra;

import com.hanna.second.springbootprj.ledger.domain.Ledger;
import com.hanna.second.springbootprj.ledger.domain.LedgerRepository;
import com.hanna.second.springbootprj.statistics.domain.Statistics;
import com.hanna.second.springbootprj.statistics.domain.StatisticsRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatisticsJpaRepository extends JpaRepository<Statistics, Long>, StatisticsRepository {
    @Override
    void delete(Statistics entity);

    @Override
    Statistics save(Statistics entity);

    @Override
    Optional<Statistics> findById(Long Id);

}
