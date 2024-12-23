
package org.kokoatalkserver.global.util.exception.handler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@Getter
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(CustomException ex) {
        log.error("CustomException: {}", ex.getMessage());
        return createErrorResponse(ex.getExceptionCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,         // 유효성 검증 실패 필드
                        FieldError::getDefaultMessage // 에러 메시지
                ));

        throw new CustomException(ExceptionCode.VALIDATION_FAILED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex) {
        if (ex instanceof NoResourceFoundException && ((NoResourceFoundException) ex).getResourcePath().equals("/favicon.ico")) {
            return ResponseEntity.notFound().build();
        }
        log.error("Unexpected error occurred", ex);
        return createErrorResponse(ExceptionCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ExceptionResponse> createErrorResponse(ExceptionCode exceptionCode) {
        ExceptionResponse response = new ExceptionResponse(exceptionCode.getCode(), exceptionCode.getMessage());
        return new ResponseEntity<>(response, exceptionCode.getHttpStatus());
    }

    @Getter
    private static class ExceptionResponse {
        private final int code;
        private final String message;

        public ExceptionResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }

    }
}