package com.hanna.second.springbootprj.statistics.domain;

import com.hanna.second.springbootprj.ledger.domain.Ledger;
import com.hanna.second.springbootprj.statistics.dto.StatisticsResponseDto;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface StatisticsRepository extends JpaSpecificationExecutor<Statistics> {
    void delete(Statistics statistics);
    Statistics save(Statistics statistics);
    Optional<Statistics> findById(Long Id);
}
