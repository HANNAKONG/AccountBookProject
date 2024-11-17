package com.hanna.second.springbootprj.budget.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hanna.second.springbootprj.support.BaseTime;
import com.hanna.second.springbootprj.support.Money;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Map;

@Entity
public class Budget extends BaseTime {

    /** 아이디 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 적용연월 */
    @Column(nullable = false)
    private String yearMonth;

    /** 총예산금액(월별) */
    @Column(nullable = true)
    private BigDecimal totalAmount;

    /** 카테고리별지출금액 */
    @Column(columnDefinition = "TEXT")
    private String categoryExpenseAmount;

    /** Users Id */
    private Long usersId;

    /**********************************
     *  constructor
     **********************************/
    public Budget(){
    }

    public Budget(Long id, String yearMonth, BigDecimal totalAmount, String categoryExpenseAmount, Long usersId) {
        this.id = id;
        this.yearMonth = yearMonth;
        this.totalAmount = totalAmount;
        this.categoryExpenseAmount = categoryExpenseAmount;
        this.usersId = usersId;
    }

    /**********************************
     *  update method
     **********************************/
    public void update(BigDecimal totalAmount,
                       String categoryExpenseAmount){
        if(totalAmount != null){
            this.totalAmount = totalAmount;
        }
        if(categoryExpenseAmount != null){
            this.categoryExpenseAmount = categoryExpenseAmount;
        }

    }

    /**********************************
     *  categoryExpenseAmount update method
     **********************************/
    public void updateCategoryExpenseAmount(String categoryExpenseAmount) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        if (categoryExpenseAmount != null) {
            // 기존 JSON 데이터를 파싱하여 수정
            JsonNode currentCategoryData = objectMapper.readTree(this.categoryExpenseAmount);
            JsonNode newCategoryData = objectMapper.readTree(categoryExpenseAmount);

            // 기존 데이터에 새로운 데이터를 병합 (단순 덮어쓰기)
            ((ObjectNode) currentCategoryData).setAll((ObjectNode) newCategoryData);

            // 업데이트된 JSON 데이터를 문자열로 다시 저장
            this.categoryExpenseAmount = currentCategoryData.toString();
        }
    }

    /**********************************
     *  addAmount, subtractAmount
     **********************************/
    public void addBudget(BigDecimal amount) {
        this.totalAmount = this.totalAmount.add(amount);
    }
    public void subtractBudget(BigDecimal amount) {
        this.totalAmount = this.totalAmount.subtract(amount);
    }

    /**********************************
     *  getter
     **********************************/
    public Long getId() {
        return id;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getCategoryExpenseAmount() {
        return categoryExpenseAmount;
    }

    public Long getUsersId() {
        return usersId;
    }

    /**********************************
     *  builder
     **********************************/
    public static class Builder {
        /** 아이디 */
        private Long id;
        /** 적용연월 */
        private String yearMonth;
        /** 총예산금액(월별) */
        private BigDecimal totalAmount;
        /** 카테고리별지출금액 */
        private String categoryExpenseAmount;
        /** Users Id */
        private Long usersId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder yearMonth(String yearMonth) {
            this.yearMonth = yearMonth;
            return this;
        }

        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder categoryExpenseAmount(String categoryExpenseAmount) {
            this.categoryExpenseAmount = categoryExpenseAmount;
            return this;
        }


        public Builder usersId(Long usersId) {
            this.usersId = usersId;
            return this;
        }

        public Budget build() {
            return new Budget(id, yearMonth, totalAmount, categoryExpenseAmount, usersId);
        }

    }

    public static Builder builder() {
        return new Builder();
    }
}
