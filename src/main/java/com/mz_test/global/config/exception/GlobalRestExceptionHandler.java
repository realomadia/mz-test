package com.mz_test.global.config.exception;

import com.mz_test.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalRestExceptionHandler {

    private static final String ERROR_LOG_MESSAGE = "[ERROR] {} : {}";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error(ERROR_LOG_MESSAGE, e.getClass().getSimpleName(), e.getMessage(), e);
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(status, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(DuplicateException e) {
        log.warn(ERROR_LOG_MESSAGE, e.getClass().getSimpleName(), e.getMessage(), e);

        int status = HttpStatus.CONFLICT.value(); // 409 Conflict
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(status, e.getMessage()));
    }

    @ExceptionHandler(IdNotMatchException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(IdNotMatchException e) {
        log.warn(ERROR_LOG_MESSAGE, e.getClass().getSimpleName(), e.getMessage(), e);

        int status = HttpStatus.BAD_REQUEST.value();
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(status, e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(NotFoundException e) {
        log.warn(ERROR_LOG_MESSAGE, e.getClass().getSimpleName(), e.getMessage(), e);

        int status = HttpStatus.NOT_FOUND.value(); // 404 Not Found
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(status, e.getMessage()));
    }

    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(NotAllowedException e) {
        log.warn(ERROR_LOG_MESSAGE, e.getClass().getSimpleName(), e.getMessage(), e);

        int status = HttpStatus.FORBIDDEN.value(); // 403 Forbidden
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(status, e.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) throws Exception {
        log.warn(ERROR_LOG_MESSAGE, e.getClass().getSimpleName(), e.getMessage(), e);
        int status = HttpStatus.BAD_REQUEST.value();
        String errorMessage = "";
        try {
            errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        } catch (NullPointerException ex) {
            throw new Exception("INTERNAL_SERVER_ERROR");
        }
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(status, errorMessage));
    }

}