package com.shurona.holiday.domain;

import lombok.Getter;

@Getter
public enum HolidaySortType {
    DATE("date"),
    NAME("name"),
    COUNTRY("countryCode");

    private final String fieldName;

    HolidaySortType(String fieldName) {
        this.fieldName = fieldName;
    }
}
