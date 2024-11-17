package com.hanna.second.springbootprj.budget.serivce;

import com.hanna.second.springbootprj.budget.domain.Budget;
import com.hanna.second.springbootprj.budget.dto.BudgetRequestDto;
import com.hanna.second.springbootprj.budget.dto.BudgetResponseDto;
import com.hanna.second.springbootprj.budget.infra.BudgetJpaRepository;
import com.hanna.second.springbootprj.support.ObjectConverter;
import com.hanna.second.springbootprj.support.enums.CategoryType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class BudgetService {
    private final BudgetJpaRepository budgetRepository;

    public BudgetService(BudgetJpaRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    /**********************************
     * 1. 예산내역과 지난 달 지출내역 함께 조회
     **********************************/
    public BudgetResponseDto getBudgetWithStatistics(final BudgetRequestDto requestDto){
        final BudgetResponseDto result = budgetRepository.findByYearMonthWithStatistics(requestDto);
        return result;
    }

    /**********************************
     *  2. 예산내역 등록 or 수정
     **********************************/
    @Transactional
    public void saveOrUpdateBudget(final BudgetRequestDto requestDto){
        // 카테고리별 지출금액 변환 (Json -> Map)
        Map<CategoryType, BigDecimal> mapData = ObjectConverter.convertJsonToMap(requestDto.getCategoryExpenseAmount());

        // 카테고리 지출 금액이 없을 경우: 미지정 카테고리에 금액 추가
        if (requestDto.getCategoryExpenseAmount() == null || requestDto.getCategoryExpenseAmount().isEmpty()) {
            mapData = new HashMap<>();
            mapData.put(CategoryType.UNDECIDED, requestDto.getTotalAmount());
        } else {
            // 유효한 금액만 남기기
            mapData.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().compareTo(BigDecimal.ZERO) <= 0);
        }

        // 예산 엔티티 검색 (yearMonth로)
        Optional<Budget> budgetResult = budgetRepository.findByYearMonthAndUsersId(requestDto.getYearMonth(), requestDto.getUsersId());

        if (budgetResult.isPresent()) {
            // 기존 예산 업데이트
            Budget existingBudget = budgetResult.get();

            // 기존 카테고리 금액 업데이트
            Map<CategoryType, BigDecimal> existingMapData = ObjectConverter.convertJsonToMap(existingBudget.getCategoryExpenseAmount());

            mapData.forEach((category, amount) -> {
                existingMapData.merge(category, amount, BigDecimal::add); // 기존 금액에 새 금액 추가
            });

            // 총합 검증
            BigDecimal newTotalAmount = existingMapData.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (newTotalAmount.compareTo(requestDto.getTotalAmount()) != 0) {
                throw new IllegalArgumentException("Total amount does not match the sum of category expenses.");
            }

            // 업데이트
            existingBudget.update(requestDto.getTotalAmount(), ObjectConverter.convertMapToJson(existingMapData));
        } else {
            // 카테고리별 지출금액 변환 (Map -> Json)
            String jsonCategoryExpense = ObjectConverter.convertMapToJson(mapData);
            // 새 예산 등록
            Budget entity = Budget.builder()
                    .yearMonth(requestDto.getYearMonth())
                    .totalAmount(requestDto.getTotalAmount())
                    .usersId(requestDto.getUsersId())
                    .categoryExpenseAmount(jsonCategoryExpense)
                    .build();

            budgetRepository.save(entity);
        }
    }
}

