package com.hanna.second.springbootprj.statistics.infra;

import com.hanna.second.springbootprj.statistics.domain.Statistics;
import com.hanna.second.springbootprj.statistics.domain.StatisticsRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatisticsJpaRepository extends JpaRepository<Statistics, Long>, StatisticsRepositoryCustom {
    Optional<Statistics> findByBaseDateAndUsersId(String baseDate, Long usersId);
}

