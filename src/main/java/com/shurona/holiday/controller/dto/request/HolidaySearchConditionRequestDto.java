package com.shurona.holiday.controller.dto.request;

public record HolidaySearchConditionRequestDto(
    Integer year,
    String code
) {

    public HolidaySearchConditionRequestDto(Integer year, String code) {
        this.year = year;
        this.code = code != null ? code.toUpperCase() : null;
    }
}
