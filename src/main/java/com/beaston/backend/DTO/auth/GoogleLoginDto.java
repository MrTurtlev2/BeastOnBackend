package com.beaston.backend.DTO.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleLoginDto {

    @NotBlank(message = "brak provider id")
    private String idToken;
}
