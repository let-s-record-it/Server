package com.sillim.recordit.schedule.domain;

public enum WeekNumber {
    FIRST(1),
    SECOND(2),
    THIRD(3),
    FOURTH(4),
    FIFTH(5),
    LAST(-1),
    ;
    private final Integer value;

    WeekNumber(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
