package com.hometask.wk.management.exception;

import lombok.Data;

/**
 * 业务异常
 *
 * @author: weikai
 */
@Data
public class BizException extends RuntimeException {

    public BizException(String message) {
        super(message);
    }
}
