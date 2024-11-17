package com.hanna.second.springbootprj.support;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;

public class WeekNumberConverter {

    public static String convertToWeekNumber(String baseDate) {
        // yyyyMMdd 형식의 문자열을 LocalDate로 변환
        LocalDate date = LocalDate.parse(baseDate, DateTimeFormatter.BASIC_ISO_DATE);

        // ISO 주 번호 계산
        WeekFields weekFields = WeekFields.ISO;
        int weekOfYear = date.get(weekFields.weekOfWeekBasedYear());
        int year = date.get(weekFields.weekBasedYear());

        // yyyyWww 형식으로 반환
        return String.format("%dW%02d", year, weekOfYear);
    }

}
