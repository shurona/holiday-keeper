package com.shurona.holiday.controller.dto.request;

import com.shurona.holiday.common.exception.HolidayErrorCode;
import com.shurona.holiday.common.exception.HolidayException;
import jakarta.annotation.Nullable;

public record HolidayDeleteRequestDto(
    @Nullable String code,
    @Nullable Integer year
) {

    public HolidayDeleteRequestDto(String code, Integer year) {
        if (code == null && year == null) {
            throw new HolidayException(HolidayErrorCode.INVALID_DELETE_CONDITION);
        }

        // code가 null이 아니면 대문자로 처리한다.
        this.code = code != null ? code.toUpperCase() : null;
        this.year = year;
    }
}
