
package org.kokoatalkserver.global.util.exception.handler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.domain.chatMessage.controller.ChatController;
import org.kokoatalkserver.domain.chatRoom.controller.ChatRoomRestController;
import org.kokoatalkserver.domain.friend.controller.FriendRestController;
import org.kokoatalkserver.domain.member.Controller.MemberRestController;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 필드별 에러 메시지 수집
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            // 이미 존재하는 키가 없을 경우에만 추가
            fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }

        // 응답 생성
        ValidationErrorResponse response = new ValidationErrorResponse(
                ExceptionCode.VALIDATION_FAILED.getCode(),
                ExceptionCode.VALIDATION_FAILED.getMessage(),
                fieldErrors
        );
        return ResponseEntity.status(ExceptionCode.VALIDATION_FAILED.getHttpStatus()).body(response);
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

    @Getter
    private static class ValidationErrorResponse extends ExceptionResponse {
        private final Map<String, String> fieldErrors;

        public ValidationErrorResponse(int code, String message, Map<String, String> fieldErrors) {
            super(code, message);
            this.fieldErrors = fieldErrors;
        }
    }
}