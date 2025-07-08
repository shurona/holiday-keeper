package com.shurona.holiday.controller.dto.request;

import com.shurona.holiday.common.exception.HolidayErrorCode;
import com.shurona.holiday.common.exception.HolidayException;
import io.swagger.v3.oas.annotations.media.Schema;

public record HolidayRefreshRequestDto(
    @Schema(name = "year", description = "연도", defaultValue = "2025")
    Integer year,
    @Schema(name = "code", description = "국가 코드", defaultValue = "KR")
    String code
) {

    public HolidayRefreshRequestDto(Integer year, String code) {
        // 업데이트는 국가와 연도가 모두 존재해야 한다.
        if (code == null || year == null) {
            throw new HolidayException(HolidayErrorCode.INVALID_REFRESH_CONDITION);
        }

        // code가 null이 아니면 대문자로 처리한다.
        this.code = code.toUpperCase();
        this.year = year;
    }
}
