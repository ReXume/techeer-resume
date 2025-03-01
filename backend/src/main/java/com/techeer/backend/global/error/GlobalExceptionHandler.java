package com.techeer.backend.global.error;

import com.techeer.backend.global.error.exception.BusinessException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleRuntimeException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, e.getErrors());

        // 상태 코드에 따라 로그 레벨 설정
        if (errorCode.getHttpStatus().is5xxServerError()) {
            log.error(e.getMessage(), e); // 5xx 상태 코드일 경우 error 로그
        } else if (errorCode.getHttpStatus().is4xxClientError()) {
            log.warn(e.getMessage(), e); // 4xx 상태 코드일 경우 warn 로그
        } else {
            log.info(e.getMessage(), e); // 기타 상태 코드일 경우 info 로그
        }

        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }


    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_REQUEST, e.getBindingResult());
        log.warn(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    // 예: ?name=value에서 name이 없는 경우
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        // 입력 값이 잘못되었음을 나타내는 응답 생성
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INPUT_VALUE_INVALID, e.getMessage(), e.getParameterName());
        // BAD_REQUEST(400) 상태 코드로 응답 반환
        log.warn(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        final ErrorResponse response = ErrorResponse.of(ErrorCode.HTTP_MESSAGE_NOT_READABLE, e.getMessage());
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    // 입력 값이 Bean Validation 제약 조건(예: @NotNull, @Size)을 위반한 경우 처리
    protected ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INPUT_VALUE_INVALID, e.getConstraintViolations());
        log.warn(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(
            MissingServletRequestPartException e) {
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INPUT_VALUE_INVALID, e.getMessage(), e.getRequestPartName());
        log.warn(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(
            MissingRequestCookieException e) {
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INPUT_VALUE_INVALID, e.getMessage(), e.getCookieName());
        log.warn(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}