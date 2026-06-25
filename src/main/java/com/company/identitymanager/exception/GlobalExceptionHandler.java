package com.company.identitymanager.exception;

import com.company.identitymanager.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TenantNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTenantNotFound(
            TenantNotFoundException ex,
            HttpServletRequest request) {

        log.warn(
                "Tenant not found: endpoint={} message={}",
                request.getRequestURI(),
                ex.getMessage()
        );

        return buildError(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.warn(
                "Resource not found: endpoint={} message={}",
                request.getRequestURI(),
                ex.getMessage()
        );

        return buildError(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request) {

        log.warn(
                "Bad request: endpoint={} message={}",
                request.getRequestURI(),
                ex.getMessage()
        );

        return buildError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request) {

        log.warn(
                "Unauthorized request: endpoint={} message={}",
                request.getRequestURI(),
                ex.getMessage()
        );

        return buildError(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            ForbiddenException ex,
            HttpServletRequest request) {

        log.warn(
                "Forbidden request: endpoint={} message={}",
                request.getRequestURI(),
                ex.getMessage()
        );

        return buildError(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn(
                "Access denied: endpoint={} message={}",
                request.getRequestURI(),
                ex.getMessage()
        );

        return buildError(
                HttpStatus.FORBIDDEN,
                "Access is denied"
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        log.warn(
                "Validation failed: endpoint={} errors={}",
                request.getRequestURI(),
                errors
        );

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Request validation failed")
                .details(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        log.warn(
                "Constraint violation: endpoint={} message={}",
                request.getRequestURI(),
                ex.getMessage()
        );

        return buildError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error(
                "Unhandled exception: endpoint={}",
                request.getRequestURI(),
                ex
        );

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
    }

    private ResponseEntity<ErrorResponse> buildError(
            HttpStatus status,
            String message) {

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .details(List.of())
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
