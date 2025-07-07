package com.shurona.holiday.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum HolidayErrorCode {

    COUNTRY_INVALID_INPUT(HttpStatus.BAD_REQUEST, "존재하지 않는 국가코드에 대한 접근입니다."),
    INVALID_DELETE_CONDITION(HttpStatus.BAD_REQUEST, "잘못된 공휴일 삭제 조건 입니다."),
    INVALID_REFRESH_CONDITION(HttpStatus.BAD_REQUEST, "잘못된 공휴일 갱신 조건 입니다."),
    ;

    private HttpStatus status;
    private String message;

}
