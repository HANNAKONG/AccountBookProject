package com.hanna.second.springbootprj.ledger.domain;

import com.hanna.second.springbootprj.ledger.dto.LedgerRequestDto;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class LedgerSpecification {

    public static Specification<Ledger> byFilters(LedgerRequestDto requestDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 기간별 필터
            if (requestDto.getStartDate() != null && requestDto.getEndDate() != null) {
                predicates.add(criteriaBuilder.between(root.get("baseDate"), requestDto.getStartDate(), requestDto.getEndDate()));
            }

            // 거래 유형별 필터
            if (requestDto.getTransactionType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("transactionType"), requestDto.getTransactionType()));
            }

            // 카테고리별 필터
            if (requestDto.getCategoryType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoryType"), requestDto.getCategoryType()));
            }

            // 자산(결재수단)별 필터
            if (requestDto.getAssetType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("assetType"), requestDto.getAssetType()));
            }

            // 키워드 필터 (내역명, 메모)
            // 둘 중 하나만 null인 경우 로직 추가
            if (requestDto.getDescription() != null && !requestDto.getMemo().isEmpty()) {
                String description = "%" + requestDto.getDescription() + "%";
                String memo = "%" + requestDto.getMemo() + "%";
                Predicate descriptionPredicate = criteriaBuilder.like(root.get("description"), description);
                Predicate memoPredicate = criteriaBuilder.like(root.get("memo"), memo);
                predicates.add(criteriaBuilder.or(descriptionPredicate, memoPredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}