package com.beaston.backend.DTO.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDto {

    @NotBlank(message = "Imię nie może być puste")
    @Size(min = 2, max = 50, message = "Imię musi mieć od 2 do 50 znaków")
    private String customerName;

    @NotBlank(message = "Email nie może być pusty")
    @Email(message = "Niepoprawny format email")
    private String email;

    @NotBlank(message = "Rola nie może być pusta")
    private String role;

    @NotBlank(message = "Hasło nie może być puste")
    @Size(min = 8, message = "Hasło musi mieć co najmniej 8 znaków")
    private String password;
}
