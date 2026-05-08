package com.beaston.backend.DTO.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDetailsDto {
    private Long id;
    private String email;
    private String role;
}
