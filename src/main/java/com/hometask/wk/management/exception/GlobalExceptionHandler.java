package com.hometask.wk.management.exception;

import com.hometask.wk.management.controller.reponse.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * @author: weikai
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String msg = e.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(";"));

        return ResponseEntity.ok(ApiResponse.bizError(msg));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolationException(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(";"));
        return ResponseEntity.ok(ApiResponse.bizError(msg));
    }

}
