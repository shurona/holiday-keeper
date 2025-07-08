package com.shurona.holiday.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record HolidaySearchConditionRequestDto(
    @Schema(name = "year", description = "연도", defaultValue = "2025")
    Integer year,
    @Schema(name = "code", description = "국가 코드", defaultValue = "KR")
    String code,
    @Schema(name = "from", description = "조회 시작일 (YYYY-MM-DD)", example = "2025-01-01")
    LocalDate from,
    @Schema(name = "to", description = "조회 종료일 (YYYY-MM-DD)", example = "2025-12-31")
    LocalDate to,
    @Schema(name = "type", description = "공휴일 타입 (Public, Bank, School, Authorities, Optional, Observance 중 선택 가능)",
        nullable = true)
    String type
) {

    public HolidaySearchConditionRequestDto(
        Integer year, String code, LocalDate from, LocalDate to, String type) {
        this.year = year;
        this.code = code != null ? code.toUpperCase() : null;
        this.from = from;
        this.to = to;
        this.type = type;
    }
}
