package com.beaston.backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {
    private String customerName;
    private String password;
}
