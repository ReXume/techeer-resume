package com.techeer.backend.global.error;

import jakarta.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

@Getter
public class ErrorResponse {
    private HttpStatus httpStatus;
    private String code;
    private String errorMessage;
    private List<FieldError> errors;


    public ErrorResponse(ErrorCode ErrorCode) {
        this.errorMessage = ErrorCode.getMessage();
        this.httpStatus = ErrorCode.getHttpStatus();
        this.code = ErrorCode.getCode();
    }

    public ErrorResponse(ErrorCode ErrorCode, final List<FieldError> fieldErrors) {
        this.httpStatus = ErrorCode.getHttpStatus();
        this.code = ErrorCode.getCode();
        this.errorMessage = ErrorCode.getMessage();
        this.errors = fieldErrors;
    }


    public static ErrorResponse of(final ErrorCode code, final Set<ConstraintViolation<?>> constraintViolations) {
        return new ErrorResponse(code, FieldError.of(constraintViolations));
    }

    public static ErrorResponse of(final ErrorCode code, String errorMessage) {
        return new ErrorResponse(code, FieldError.of("", "", errorMessage));
    }

    public static ErrorResponse of(final ErrorCode code, String errorMessage, final String missingParameterName) {
        return new ErrorResponse(code, FieldError.of(missingParameterName, "", "parameter가 필요합니다"));
    }

    public static ErrorResponse of(final ErrorCode code) {
        return new ErrorResponse(code);
    }

    public static ErrorResponse of(final ErrorCode code, final List<FieldError> errors) {
        return new ErrorResponse(code, errors);
    }


    public static ErrorResponse of(final ErrorCode ErrorCode, final BindingResult bindingResult) {
        return new ErrorResponse(ErrorCode, FieldError.of(bindingResult));
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;
        private String value;
        private String reason;

        public FieldError(final String field, final String value, final String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldError> of(final String field, final String value, final String reason) {
            final List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(field, value, reason));
            return fieldErrors;
        }

        private static List<FieldError> of(final BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }


        private static List<FieldError> of(final Set<ConstraintViolation<?>> constraintViolations) {
            final List<ConstraintViolation<?>> lists = new ArrayList<>(constraintViolations);
            return lists.stream()
                    .map(error -> {
                        final String invalidValue =
                                error.getInvalidValue() == null ? "" : error.getInvalidValue().toString();
                        final int index = error.getPropertyPath().toString().indexOf(".");
                        final String propertyPath = error.getPropertyPath().toString().substring(index + 1);
                        return new FieldError(propertyPath, invalidValue, error.getMessage());
                    })
                    .collect(Collectors.toList());
        }
    }
}