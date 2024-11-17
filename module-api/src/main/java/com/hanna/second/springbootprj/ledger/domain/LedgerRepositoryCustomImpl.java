package com.hanna.second.springbootprj.ledger.domain;

import com.hanna.second.springbootprj.ledger.dto.LedgerRequestDto;
import com.hanna.second.springbootprj.support.enums.AssetType;
import com.hanna.second.springbootprj.support.enums.CategoryType;
import com.hanna.second.springbootprj.support.enums.PeriodType;
import com.hanna.second.springbootprj.support.enums.TransactionType;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public class LedgerRepositoryCustomImpl implements LedgerRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QLedger ledger = QLedger.ledger;

    public LedgerRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<Ledger> findAllByFilter(LedgerRequestDto requestDto, Pageable pageable) {
        // Ledger 쿼리 실행
        List<Ledger> content = jpaQueryFactory.selectFrom(ledger)
                .where(
                        dateBetween(requestDto.getStartDate(), requestDto.getEndDate()),
                        equalsTransactionType(requestDto.getTransactionType()),
                        equalsCategoryType(requestDto.getCategoryType()),
                        equalsAssetType(requestDto.getAssetType()),
                        equalsUsersId(requestDto.getUsersId()),
                        containsDescriptionOrMemo(requestDto.getDescription(), requestDto.getMemo())
                )
                .orderBy(getSortOrder(pageable.getSort()))  // 정렬 처리
                .offset(pageable.getOffset())  // 페이징 시작 위치
                .limit(pageable.getPageSize())  // 페이지 크기
                .fetch();  // 결과를 가져옴

        // 전체 데이터 수를 가져오는 카운트 쿼리
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(ledger.count())
                .from(ledger)
                .where(
                        dateBetween(requestDto.getStartDate(), requestDto.getEndDate()),
                        equalsTransactionType(requestDto.getTransactionType()),
                        equalsCategoryType(requestDto.getCategoryType()),
                        equalsAssetType(requestDto.getAssetType()),
                        equalsUsersId(requestDto.getUsersId()),
                        containsDescriptionOrMemo(requestDto.getDescription(), requestDto.getMemo())
                );

        // Page 객체 생성
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }
    @Override
    public List<Ledger> findCategoryAndAmount(String baseDate, Long usersId, TransactionType transactionType) {
        List<Ledger> result = jpaQueryFactory
                .selectFrom(ledger)
                .where(ledger.baseDate.eq(baseDate)
                        .and(equalsUsersId(usersId))
                        .and(equalsTransactionType(transactionType)))
                .fetch();

        return result;
    }
    @Override
    public List<Ledger> findWeeklyCategoryAndAmount(String baseDate, Long usersId, TransactionType transactionType) {
        BooleanExpression periodFilter = dateFilterByPeriodType(PeriodType.WEEKLY, baseDate);

        List<Ledger> result = jpaQueryFactory
                .selectFrom(ledger)
                .where(periodFilter,
                        equalsTransactionType(transactionType),
                        equalsUsersId(usersId)
                )
                .fetch();
        System.out.println("------------>findWeeklyCategoryAndAmount--->"+result.toString());
        return result;
    }
    @Override
    public List<Ledger> findMonthlyCategoryAndAmount(String baseDate, Long usersId, TransactionType transactionType) {
        BooleanExpression periodFilter = dateFilterByPeriodType(PeriodType.MONTHLY, baseDate);

        List<Ledger> result = jpaQueryFactory
                .selectFrom(ledger)
                .where(periodFilter,
                        equalsTransactionType(transactionType),
                        equalsUsersId(usersId)
                )
                .fetch();

        return result;
    }
    @Override
    public BigDecimal findAmountSum(String baseDate, Long usersId, TransactionType transactionType) {
        BigDecimal result = jpaQueryFactory
                .select(ledger.amount.sum())
                .from(ledger)
                .where(ledger.baseDate.eq(baseDate)
                        .and(equalsUsersId(usersId))
                        .and(equalsTransactionType(transactionType)))
                .fetchOne();

        return result;
    }
    @Override
    public BigDecimal findWeeklyAmountSum(String baseDate, Long usersId, TransactionType transactionType) {
        BooleanExpression periodFilter = dateFilterByPeriodType(PeriodType.WEEKLY, baseDate);

        BigDecimal result = jpaQueryFactory
                .select(ledger.amount.sum())
                .from(ledger)
                .where(periodFilter,
                        equalsTransactionType(transactionType),
                        equalsUsersId(usersId)
                )
                .fetchOne();

        return result;
    }

    @Override
    public BigDecimal findMonthlyAmountSum(String baseDate, Long usersId, TransactionType transactionType) {
        BooleanExpression periodFilter = dateFilterByPeriodType(PeriodType.MONTHLY, baseDate);

        BigDecimal result = jpaQueryFactory
                .select(ledger.amount.sum())
                .from(ledger)
                .where(periodFilter,
                        equalsTransactionType(transactionType),
                        equalsUsersId(usersId)
                )
                .fetchOne();

        return result;
    }

    //sorting
    private OrderSpecifier<?>[] getSortOrder(Sort sort) {
        return sort.stream()
                .map(order -> {
                    PathBuilder<?> pathBuilder = new PathBuilder<>(ledger.getType(), ledger.getMetadata());
                    return new OrderSpecifier(
                            order.isAscending() ? Order.ASC : Order.DESC,
                            pathBuilder.get(order.getProperty())
                    );
                })
                .toArray(OrderSpecifier[]::new);
    }

    // 기간별 필터
    private BooleanExpression dateBetween(String startDate, String endDate) {
        if (startDate != null && endDate != null) {
            return ledger.baseDate.between(startDate, endDate);
        }
        return null;
    }

    private BooleanExpression dateFilterByPeriodType(PeriodType periodType, String baseDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(baseDate, formatter);

        switch (periodType) {
            case WEEKLY:
                LocalDate startOfWeek = date.with(DayOfWeek.MONDAY); // 주의 시작일 (월요일)
                LocalDate endOfWeek = date.with(DayOfWeek.SUNDAY);   // 주의 종료일 (일요일)
                return ledger.baseDate.between(
                        startOfWeek.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                        endOfWeek.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                );
            case MONTHLY:
                LocalDate startOfMonth = date.withDayOfMonth(1);   // 달의 시작일
                LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth()); // 달의 마지막 날
                return ledger.baseDate.between(
                        startOfMonth.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                        endOfMonth.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                );
            default:
                throw new IllegalArgumentException("Unsupported period type: " + periodType);
        }
    }

    // 사용자 아이디별 필터
    private BooleanExpression equalsUsersId(Long usersId) {
        if (usersId != null) {
            return ledger.usersId.eq(usersId);
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
