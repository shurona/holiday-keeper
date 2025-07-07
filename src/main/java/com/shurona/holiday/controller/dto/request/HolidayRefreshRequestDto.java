package com.shurona.holiday.controller.dto.request;

import com.shurona.holiday.common.exception.HolidayErrorCode;
import com.shurona.holiday.common.exception.HolidayException;

public record HolidayRefreshRequestDto(
    String code,
    Integer year
) {

    public HolidayRefreshRequestDto(String code, Integer year) {
        // 업데이트는 국가와 연도가 모두 존재해야 한다.
        if (code == null || year == null) {
            throw new HolidayException(HolidayErrorCode.INVALID_REFRESH_CONDITION);
        }

        // code가 null이 아니면 대문자로 처리한다.
        this.code = code.toUpperCase();
        this.year = year;
    }
}
