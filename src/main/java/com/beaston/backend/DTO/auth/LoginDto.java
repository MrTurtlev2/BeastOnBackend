package com.beaston.backend.DTO.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {
    private String customerName;
    private String password;
}
