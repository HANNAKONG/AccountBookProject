package com.hanna.second.springbootprj.support.enums;

public enum PeriodType {
    DAILY,
    WEEKLY,
    MONTHLY;

    public String getPeriodType() {
        return name();
    }
}
