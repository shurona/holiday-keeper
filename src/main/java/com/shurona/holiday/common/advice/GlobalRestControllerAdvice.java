package com.shurona.holiday.common.advice;

import static com.shurona.holiday.common.exception.HolidayErrorCode.INVALID_DELETE_CONDITION;
import static com.shurona.holiday.common.exception.HolidayErrorCode.INVALID_REFRESH_CONDITION;

import com.shurona.holiday.common.exception.HolidayException;
import com.shurona.holiday.common.response.ApiResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalRestControllerAdvice {

    @ExceptionHandler(HolidayException.class)
    public ApiResponse<?> handleHolidayException(HolidayException ex) {
        return ApiResponse.error(ex.getErrorCode().getStatus(), ex.getMessage());
    }

    /**
     * 일반적인 400대 에러
     */
    @ExceptionHandler({
        IllegalArgumentException.class,
        HttpMessageNotReadableException.class})
    public ApiResponse<Void> handleIllegalArgumentException(
        Exception ex) {
        String message = ex.getMessage();

        // HolidayException이 감싸진 경우
        if (message.contains(INVALID_DELETE_CONDITION.getMessage())) {
            return ApiResponse.error(
                HttpStatus.BAD_REQUEST, INVALID_DELETE_CONDITION.getMessage());
        } else if (message.contains(INVALID_REFRESH_CONDITION.getMessage())) {
            return ApiResponse.error(
                HttpStatus.BAD_REQUEST, INVALID_REFRESH_CONDITION.getMessage());
        }

        return ApiResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * 입력값이 잘 못 들어온 경우 처리하는 에러핸들링
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException ex) {
        List<String> wrongKeys = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            wrongKeys.add(error.getField()));

        return ApiResponse.error(HttpStatus.BAD_REQUEST, "입력값 검증에 실패했습니다. " + wrongKeys);
    }

    /**
     * Runtime 500에러
     */
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<Void> handleRuntimeException(RuntimeException ex) {

        // 500 runtime은 로그를 남긴다.
        log.error("e: ", ex);

        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

}
