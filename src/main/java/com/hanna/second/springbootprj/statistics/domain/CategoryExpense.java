package com.hanna.second.springbootprj.statistics.domain;

import com.hanna.second.springbootprj.support.enums.CategoryType;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class CategoryExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "statistics_id")
    private Statistics statistics;

    @Column
    private CategoryType categoryType;

    @Column(nullable = false)
    private BigDecimal expenseAmount;

    /**********************************
     *  constructor
     **********************************/
    public CategoryExpense(){
    }
    public CategoryExpense(Long id, CategoryType categoryType, BigDecimal expenseAmount) {
        this.id = id;
        this.categoryType = categoryType;
        this.expenseAmount = expenseAmount;
    }

    /**********************************
     *  update method
     **********************************/
    public void update(CategoryType categoryType,
                       BigDecimal expenseAmount){
        if(categoryType != null){
            this.categoryType = categoryType;
        }
        if(expenseAmount != null){
            this.expenseAmount = expenseAmount;
        }

    }

    /**********************************
     *  getter
     **********************************/
    public Long getId() {
        return id;
    }

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public BigDecimal getExpenseAmount() {
        return expenseAmount;
    }

    /**********************************
     *  builder
     **********************************/
    public static class Builder {
        private Long id;
        private CategoryType categoryType;
        private BigDecimal expenseAmount;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder categoryType(CategoryType categoryType) {
            this.categoryType = categoryType;
            return this;
        }

        public Builder expenseAmount(BigDecimal expenseAmount) {
            this.expenseAmount = expenseAmount;
            return this;
        }

        public CategoryExpense build() {
            return new CategoryExpense(id, categoryType, expenseAmount);
        }

    }

    public static Builder builder() {
        return new Builder();
    }


}
