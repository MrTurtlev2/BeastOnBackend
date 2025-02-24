package com.beaston.backend.DTO.api;

import com.beaston.backend.enums.ErrorTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiErrorResponseDto {
    private String message;
    private ErrorTypeEnum type;
    private int status;
}
