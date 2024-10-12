package com.hanna.second.springbootprj.support.enums;

public enum CategoryType {
    FOOD,
    TRAVEL,
    ENTERTAINMENT,
    SHOPPING,
    UNDECIDED;

    public String getCategoryType() {
        return name();
    }
}
