package com.hanna.second.springbootprj.statistics.infra;

import com.hanna.second.springbootprj.statistics.domain.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatisticsJpaRepository extends JpaRepository<Statistics, Long> {
    @Override
    void delete(Statistics entity);

    @Override
    Statistics save(Statistics entity);

    @Override
    Optional<Statistics> findById(Long Id);

    Optional<Statistics> findByBaseDateAndUsersId(String baseDate, Long usersId);
}

