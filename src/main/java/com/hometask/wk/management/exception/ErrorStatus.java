package com.hometask.wk.management.exception;

/**
 * 特殊错误码
 * @author: weikai
 */
public enum ErrorStatus {

    //相关的错误码和httpStatus区别 从1000开始
    BizError(1000, "业务通用错误");

    private final int code;
    private final String message;

    ErrorStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
