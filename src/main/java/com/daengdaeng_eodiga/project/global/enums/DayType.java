package com.daengdaeng_eodiga.project.global.enums;

import lombok.Getter;

@Getter
public enum DayType {
    EVERYDAY("연중무휴"),
    MONDAY("월"),
    TUESDAY("화"),
    WEDNESDAY("수"),
    THURSDAY("목"),
    FRIDAY("금"),
    SATURDAY("토"),
    SUNDAY("일");

    private final String value;

    DayType(String value) {
        this.value = value;
    }
}