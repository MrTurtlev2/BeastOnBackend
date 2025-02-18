package com.beaston.backend.controllers;

import com.beaston.backend.DTO.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping("/register")
    public String registerUser(@RequestBody UserRegistrationDto authRequest) {
       return "it worked";
    }
    @PostMapping("/register2")
    public String registerUser2(@RequestBody UserRegistrationDto authRequest) {
        return "it worked2";
    }

}
