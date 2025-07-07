package com.shurona.holiday.common.exception;

import lombok.Getter;

@Getter
public class HolidayException extends RuntimeException {

    private final HolidayErrorCode errorCode;

    public HolidayException(HolidayErrorCode error) {
        super(error.getMessage());
        errorCode = error;
    }

}
