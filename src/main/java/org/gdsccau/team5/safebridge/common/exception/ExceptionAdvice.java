package org.gdsccau.team5.safebridge.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.code.ErrorCode;
import org.gdsccau.team5.safebridge.common.code.ErrorCodeResolver;
import org.gdsccau.team5.safebridge.common.response.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> validation(ConstraintViolationException e, WebRequest request) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(constraintViolation -> constraintViolation.getMessage())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ConstraintViolationException 추출 도중 에러 발생"));
        ErrorCode errorCode = ErrorCodeResolver.fromCodeName(errorMessage);
        return handleExceptionInternalConstraint(e, errorCode, HttpHeaders.EMPTY, request);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        e.getBindingResult().getFieldErrors()
                .forEach(fieldError -> {
                    String fieldName = fieldError.getField();
                    String errorMessage = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("");
                    errors.merge(fieldName, errorMessage,
                            (existingErrorMessage, newErrorMessage) -> existingErrorMessage + ", " + newErrorMessage);
                });
        ErrorCode errorCode = ErrorCodeResolver.fromCodeName("BAD_REQUEST");
        return handleExceptionInternalArgs(e, HttpHeaders.EMPTY, errorCode, request,
                errors);
    }

    @ExceptionHandler
    public ResponseEntity<Object> exception(Exception e, WebRequest request) {
        ErrorCode errorCode = ErrorCodeResolver.fromCodeName("INTERNAL_SERVER_ERROR");
        return handleExceptionInternalFalse(e, errorCode, HttpHeaders.EMPTY,
                errorCode.getHttpStatus(), request, e.getMessage());
    }

    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity onThrowException(GeneralException generalException, HttpServletRequest request) {
        ErrorCode errorCode = generalException.getErrorCode();
        return handleExceptionInternal(generalException, errorCode, null, request);
    }

    private ResponseEntity<Object> handleExceptionInternal(Exception e, ErrorCode reason,
                                                           HttpHeaders headers, HttpServletRequest request) {
        ApiResponse<Object> body = ApiResponse.onFailure(reason.getCode(), reason.getMessage(), null);
        WebRequest webRequest = new ServletWebRequest(request);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                reason.getHttpStatus(),
                webRequest
        );
    }

    private ResponseEntity<Object> handleExceptionInternalFalse(Exception e, ErrorCode errorCode,
                                                                HttpHeaders headers, HttpStatus status,
                                                                WebRequest request, String errorPoint) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorCode.getCode(), errorCode.getMessage(), errorPoint);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                status,
                request
        );
    }

    private ResponseEntity<Object> handleExceptionInternalArgs(Exception e, HttpHeaders headers,
                                                               ErrorCode errorCommonCode,
                                                               WebRequest request, Map<String, String> errorArgs) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorCommonCode.getCode(), errorCommonCode.getMessage(),
                errorArgs);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                errorCommonCode.getHttpStatus(),
                request
        );
    }

    private ResponseEntity<Object> handleExceptionInternalConstraint(Exception e, ErrorCode errorCode,
                                                                     HttpHeaders headers, WebRequest request) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorCode.getCode(), errorCode.getMessage(), null);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                errorCode.getHttpStatus(),
                request
        );
    }
}

