package com.hanna.second.springbootprj.budget.infra;

import com.hanna.second.springbootprj.budget.domain.Budget;
import com.hanna.second.springbootprj.budget.domain.BudgetRepositoryCustom;
import com.hanna.second.springbootprj.statistics.domain.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetJpaRepository extends JpaRepository<Budget, Long>, BudgetRepositoryCustom {
    Budget findByYearMonthAndUsersId(String yearMonth, Long usersId);
}
