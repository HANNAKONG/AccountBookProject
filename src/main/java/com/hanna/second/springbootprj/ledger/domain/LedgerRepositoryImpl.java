package com.hanna.second.springbootprj.ledger.domain;

import com.hanna.second.springbootprj.ledger.dto.LedgerRequestDto;
import com.hanna.second.springbootprj.ledger.infra.LedgerJpaRepository;
import com.hanna.second.springbootprj.support.enums.AssetType;
import com.hanna.second.springbootprj.support.enums.CategoryType;
import com.hanna.second.springbootprj.support.enums.TransactionType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class LedgerRepositoryImpl implements LedgerRepository {

    private final LedgerJpaRepository ledgerJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final QLedger ledger = QLedger.ledger;

    public LedgerRepositoryImpl(LedgerJpaRepository ledgerJpaRepository, JPAQueryFactory jpaQueryFactory) {
        this.ledgerJpaRepository = ledgerJpaRepository;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Optional<Ledger> findById(Long Id) {
        return ledgerJpaRepository.findById(Id);
    }

    @Override
    public Ledger save(Ledger ledger) {
        return ledgerJpaRepository.save(ledger);
    }

    @Override
    public void delete(Ledger ledger) {
        ledgerJpaRepository.delete(ledger);
    }

    @Override
    public List<Ledger> findAllByFilter(LedgerRequestDto requestDto) {
        return jpaQueryFactory.selectFrom(ledger)
                .where(
                        dateBetween(requestDto.getStartDate(), requestDto.getEndDate()),
                        equalsTransactionType(requestDto.getTransactionType()),
                        equalsCategoryType(requestDto.getCategoryType()),
                        equalsAssetType(requestDto.getAssetType()),
                        containsDescriptionOrMemo(requestDto.getDescription(), requestDto.getMemo())
                )
                .fetch();
    }

    @Override
    public BigDecimal sumAmountByBaseDateAndUsersIdAndTransactionType(String baseDate, Long usersId, TransactionType transactionType) {
        return jpaQueryFactory
                .select(ledger.amount.sum())
                .from(ledger)
                .where(ledger.baseDate.eq(baseDate)
                        .and(ledger.usersId.eq(usersId))
                        .and(ledger.transactionType.eq(transactionType)))
                .fetchOne();
    }

    // 기간별 필터
    private BooleanExpression dateBetween(String startDate, String endDate) {
        if (startDate != null && endDate != null) {
            return ledger.baseDate.between(startDate, endDate);
        }
        return null;
    }

    // 거래 유형별 필터
    private BooleanExpression equalsTransactionType(TransactionType transactionType) {
        if (transactionType != null) {
            return ledger.transactionType.eq(transactionType);
        }
        return null;
    }

    // 카테고리별 필터
    private BooleanExpression equalsCategoryType(CategoryType categoryType) {
        if (categoryType != null) {
            return ledger.categoryType.eq(categoryType);
        }
        return null;
    }

    // 자산(결재수단)별 필터
    private BooleanExpression equalsAssetType(AssetType assetType) {
        if (assetType != null) {
            return ledger.assetType.eq(assetType);
        }
        return null;
    }

    // 키워드 필터 (내역명, 메모)
    private BooleanExpression containsDescriptionOrMemo(String description, String memo) {
        BooleanExpression descriptionPredicate = null;
        BooleanExpression memoPredicate = null;

        // description이 null이 아니고, 비어있지 않은 경우 처리
        if (description != null && !description.isEmpty()) {
            descriptionPredicate = ledger.description.containsIgnoreCase(description);
        }

        // memo가 null이 아니고, 비어있지 않은 경우 처리
        if (memo != null && !memo.isEmpty()) {
            memoPredicate = ledger.memo.containsIgnoreCase(memo);
        }

        // 둘 다 null이 아닌 경우 OR 조건으로 묶어서 반환
        if (descriptionPredicate != null && memoPredicate != null) {
            return descriptionPredicate.or(memoPredicate);
        }

        // 하나만 존재하는 경우 해당 Predicate 반환
        return descriptionPredicate != null ? descriptionPredicate : memoPredicate;
    }
}
