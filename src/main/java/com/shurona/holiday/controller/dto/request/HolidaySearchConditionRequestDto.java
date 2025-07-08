package com.shurona.holiday.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record HolidaySearchConditionRequestDto(
    @Schema(name = "year", description = "연도", defaultValue = "2025")
    Integer year,
    @Schema(name = "code", description = "국가 코드", defaultValue = "KR")
    String code
) {

    public HolidaySearchConditionRequestDto(Integer year, String code) {
        this.year = year;
        this.code = code != null ? code.toUpperCase() : null;
    }
}
