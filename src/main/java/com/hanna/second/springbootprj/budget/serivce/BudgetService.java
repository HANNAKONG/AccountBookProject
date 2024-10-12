package com.hanna.second.springbootprj.budget.serivce;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hanna.second.springbootprj.budget.domain.Budget;
import com.hanna.second.springbootprj.budget.dto.BudgetRequestDto;
import com.hanna.second.springbootprj.budget.dto.BudgetResponseDto;
import com.hanna.second.springbootprj.budget.infra.BudgetJpaRepository;
import com.hanna.second.springbootprj.support.ObjectConverter;
import com.hanna.second.springbootprj.support.enums.CategoryType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BudgetService {
    private final BudgetJpaRepository budgetRepository;
    private final ObjectConverter objectConverter;

    public BudgetService(BudgetJpaRepository budgetRepository, ObjectConverter objectConverter) {
        this.budgetRepository = budgetRepository;
        this.objectConverter = objectConverter;
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
        // 카테고리별 지출금액 변환 (Json -> ObjectNode) null처리 해야겠군
        ObjectNode objectNodeData = objectConverter.convertJsonToObjectNode(requestDto.getCategoryExpenseAmount());

        // 카테고리 지출 금액이 없을 경우: 미지정 카테고리에 금액 추가
        if(requestDto.getCategoryExpenseAmount() == null){
            objectNodeData.put(CategoryType.UNDECIDED.getCategoryType(), requestDto.getTotalAmount());
        }
        // 카테고리 지출 금액이 있을 경우: 카테고리와 금액 추가
        else if (requestDto.getCategoryExpenseAmount() != null && !requestDto.getCategoryExpenseAmount().isEmpty()) {
            // 기존 카테고리와 금액을 ObjectNode에 추가
            objectNodeData.fieldNames().forEachRemaining(fieldName -> {
                BigDecimal amountValue = new BigDecimal(objectNodeData.get(fieldName).asText());
                if (amountValue.compareTo(BigDecimal.ZERO) > 0) {
                    // 기존에 있는 금액과 합산
                    if (objectNodeData.has(fieldName)) {
                        BigDecimal existingAmount = objectNodeData.get(fieldName).decimalValue();
                        objectNodeData.put(fieldName, existingAmount.add(amountValue));
                    } else {
                        objectNodeData.put(fieldName, amountValue);
                    }
                }
            });
        }

        // 카테고리별 지출금액 변환 (ObjectNode -> Json)
        String jsonCategoryExpense = objectConverter.convertObjectNodeToJson(objectNodeData);

        // 예산 엔티티 검색 (yearMonth로)
        Budget existingBudget = budgetRepository.findByYearMonthAndUsersId(requestDto.getYearMonth(), requestDto.getUsersId());

        // BudgetRequestDto의 빌더 사용
        BudgetRequestDto budgetRequestDto = BudgetRequestDto.builder()
                .id(requestDto.getId())
                .yearMonth(requestDto.getYearMonth())
                .totalAmount(requestDto.getTotalAmount())
                .usersId(requestDto.getUsersId())
                .categoryExpenseAmount(jsonCategoryExpense)
                .build();

        // BudgetRequestDto에서 Budget 엔티티로 변환
        final Budget entity = budgetRequestDto.toEntity();

        if (existingBudget != null) {
            // 기존 예산 업데이트
            existingBudget.update(requestDto.getTotalAmount(), jsonCategoryExpense);
        } else {
            // 새 예산 등록
            budgetRepository.save(entity);
        }
    }
}

