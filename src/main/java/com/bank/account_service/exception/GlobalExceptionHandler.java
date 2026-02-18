package com.bank.account_service.exception;

import com.bank.account_service.dto.auth.response.BaseResponse;
import feign.FeignException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Void>> handleBusiness(BusinessException ex) {

        HttpStatus status = ex.getStatus();

        return ResponseEntity.status(status)
                .body(new BaseResponse<>(
                        null,
                        ex.getMessage(),
                        status.name()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {

        String msg = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return ResponseEntity.badRequest()
                .body(new BaseResponse<>(null, msg, "BAD_REQUEST"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<Void>> handleConstraint(ConstraintViolationException ex) {

        return ResponseEntity.badRequest()
                .body(new BaseResponse<>(
                        null,
                        ex.getMessage(),
                        "BAD_REQUEST"
                ));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<BaseResponse<Void>> handleFeign(FeignException ex) {

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new BaseResponse<>(
                        null,
                        "Dependent service unavailable",
                        "SERVICE_UNAVAILABLE"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGeneric(Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(
                        null,
                        "Internal server error",
                        "INTERNAL_SERVER_ERROR"
                ));
    }

    @RequestMapping("/error")
    public ResponseEntity<BaseResponse<Void>> handleError(HttpServletRequest request) {

        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = statusObj != null ? (int) statusObj : 500;

        HttpStatus status = HttpStatus.valueOf(statusCode);

        return ResponseEntity.status(status)
                .body(new BaseResponse<>(
                        null,
                        resolveMessage(status),
                        status.name()
                ));
    }

    private String resolveMessage(HttpStatus status) {
        return switch (status) {
            case NOT_FOUND -> "URL is wrong, please correct it";
            case METHOD_NOT_ALLOWED -> "HTTP method not allowed for this endpoint";
            case UNAUTHORIZED -> "Authentication required";
            case FORBIDDEN -> "Access denied";
            default -> "Internal server error";
        };
    }
}
