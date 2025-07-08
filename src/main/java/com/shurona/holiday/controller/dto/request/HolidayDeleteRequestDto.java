package com.shurona.holiday.controller.dto.request;

import com.shurona.holiday.common.exception.HolidayErrorCode;
import com.shurona.holiday.common.exception.HolidayException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;

public record HolidayDeleteRequestDto(
    @Schema(name = "year", description = "연도", defaultValue = "2025")
    @Nullable Integer year,
    @Schema(name = "code", description = "국가 코드", defaultValue = "KR")
    @Nullable String code
) {

    public HolidayDeleteRequestDto(Integer year, String code) {
        if (code == null && year == null) {
            throw new HolidayException(HolidayErrorCode.INVALID_DELETE_CONDITION);
        }

        // code가 null이 아니면 대문자로 처리한다.
        this.code = code != null ? code.toUpperCase() : null;
        this.year = year;
    }
}
