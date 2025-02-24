package com.beaston.backend.controllers;


import com.beaston.backend.DTO.api.ApiErrorResponseDto;
import com.beaston.backend.DTO.user.UserDetailsDto;
import com.beaston.backend.entities.Customer;
import com.beaston.backend.enums.ErrorTypeEnum;
import com.beaston.backend.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/details")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Brak autoryzacji");
        }

        String username = jwt.getSubject();
        Customer customer = customerRepository.findByCustomerName(username);

        if (customer == null) {
            ApiErrorResponseDto response = new ApiErrorResponseDto(
                    "Użytkownik nie znaleziony",
                    ErrorTypeEnum.BUSINESS_ERROR,
                    HttpStatus.BAD_REQUEST.value()
            );
            return ResponseEntity.badRequest().body(response);
        }

        UserDetailsDto customerDetails = new UserDetailsDto(
                customer.getId(),
                customer.getCustomerName(),
                customer.getEmail(),
                customer.getRole()
        );

        return ResponseEntity.ok(customerDetails);
    }

}
