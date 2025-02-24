package com.beaston.backend;

import com.beaston.backend.DTO.api.ApiErrorResponseDto;
import com.beaston.backend.enums.ErrorTypeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        String fullMessage = String.join("; ", errorMessages);

        ApiErrorResponseDto response = new ApiErrorResponseDto(
                fullMessage,
                ErrorTypeEnum.VALIDATION_PROBLEM,
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponseDto> handleAccessDenied(AccessDeniedException ex) {
        ApiErrorResponseDto response = new ApiErrorResponseDto(
                "Brak uprawnień do wykonania tej operacji",
                ErrorTypeEnum.ACCESS_DENIED,
                HttpStatus.FORBIDDEN.value()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}


