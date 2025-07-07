package com.shurona.holiday.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;

    /**
     * 성공 시 메서드
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data);
    }

    /**
     * 실패시 메서드 - data 필드가 필요 없는 경우
     */
    public static ApiResponse<Void> error(HttpStatus status, String message) {

        return new ApiResponse<>(status.value(), message, null);
    }

    /**
     * 실패시 메서드 - data 필드가 필요한 경우
     */
    public static <T> ApiResponse<T> error(HttpStatus status, String message, T data) {
        return new ApiResponse<>(status.value(), message, data);
    }

}
