package com.beaston.backend.DTO;
import lombok.Data;

@Data
public class RegisterDto {
    private String customerName;
    private String email;
    private String role;
    private String password;
}
