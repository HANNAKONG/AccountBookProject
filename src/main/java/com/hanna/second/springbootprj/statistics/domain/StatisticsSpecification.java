package com.hanna.second.springbootprj.statistics.domain;

import com.hanna.second.springbootprj.ledger.domain.Ledger;
import com.hanna.second.springbootprj.ledger.dto.LedgerRequestDto;
import com.hanna.second.springbootprj.statistics.dto.StatisticsRequestDto;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class StatisticsSpecification {

    /*
     *  - 월별, 주별, 일별, 카테고리별 지출금액
     *  - 월별, 일별 수입금액
     *  - 일별 지출 대비 수입금액
     */
    public static Specification<Statistics> byFilters(StatisticsRequestDto requestDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 일별 필터링
            if (requestDto.getBaseDate() != null) {
                predicates.add(criteriaBuilder.equal(root.get("baseDate"), requestDto.getBaseDate()));
            }

            // 거래 유형 필터링
            if (requestDto.getTransactionType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("transactionType"), requestDto.getTransactionType()));
            }

            // 카테고리 필터링
            if (requestDto.getCategoryType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoryType"), requestDto.getCategoryType()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}