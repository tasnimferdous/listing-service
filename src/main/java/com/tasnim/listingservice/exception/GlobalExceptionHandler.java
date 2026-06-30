package com.tasnim.listingservice.exception;

import com.tasnim.listingservice.dtos.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse<Object> handleNotFound(ResourceNotFoundException ex) {
        return CommonResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Object> handleBadRequest(BadRequestException ex) {
        return CommonResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public CommonResponse<Object> handleForbidden(ForbiddenException ex) {
        return CommonResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResponse<Object> handleUnauthorized(UnauthorizedException ex) {
        return CommonResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public CommonResponse<Object> handleBusinessException(BusinessException ex) {
        return CommonResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Object> handleValidation(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation failed");

        return CommonResponse.builder()
                .success(false)
                .message(errorMessage)
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse<Object> handleException(Exception ex) {
        log.error("Unhandled exception", ex);

        return CommonResponse.builder()
                .success(false)
                .message("Internal server error")
                .build();
    }
}
