package com.hometask.wk.management.controller.reponse;

import com.hometask.wk.management.exception.ErrorStatus;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author: weikai
 */
@Data
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> successData(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), "成功", data);
    }

    public static <T> ApiResponse<T> success() {
        return successData(null);
    }

    public static <T> ApiResponse<T> bizError(String message) {
        return new ApiResponse<>(ErrorStatus.BizError.getCode(), message, null);
    }
}
