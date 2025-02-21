package com.beaston.backend.controllers;
import com.beaston.backend.DTO.ApiErrorResponseDto;
import com.beaston.backend.DTO.LoginDto;
import com.beaston.backend.DTO.RegisterDto;
import com.beaston.backend.entities.Customer;
import com.beaston.backend.enums.ErrorTypeEnum;
import com.beaston.backend.repositories.CustomerRepository;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${security.jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${security.jwt.secret-key}")
    private String jwtIssuer;


    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<Object> register(
           @Valid @RequestBody RegisterDto registerDto,
            BindingResult result) {

        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(";"));

            ApiErrorResponseDto response = new ApiErrorResponseDto(
                    errorMessage,
                    ErrorTypeEnum.VALIDATION_PROBLEM,
                    HttpStatus.BAD_REQUEST.value()
            );
            return ResponseEntity.badRequest().body(response);
        }

        var bCryptEncoder = new BCryptPasswordEncoder();
        Customer customer = new Customer();
        customer.setCustomerName(registerDto.getCustomerName());
        customer.setEmail(registerDto.getEmail());
        customer.setRole("client");
        customer.setPasswordHash(bCryptEncoder.encode(registerDto.getPassword()));


        try {
            var otherUser = customerRepository.findByCustomerName(registerDto.getCustomerName());
            if (otherUser != null) {
                ApiErrorResponseDto response = new ApiErrorResponseDto(
                        "Użytkownik o podanym loginie już istnieje",
                        ErrorTypeEnum.BUSINESS_ERROR,
                        HttpStatus.BAD_REQUEST.value()
                );
                return ResponseEntity.badRequest().body(response);
            }
            var otherUserEmail = customerRepository.findByEmail(registerDto.getEmail());
            if (otherUserEmail != null) {
                ApiErrorResponseDto response = new ApiErrorResponseDto(
                        "Użytkownik o podanym e-mail już istnieje",
                        ErrorTypeEnum.BUSINESS_ERROR,
                        HttpStatus.BAD_REQUEST.value()
                );
                return ResponseEntity.badRequest().body(response);
            }
            customerRepository.save(customer);
            String jwtToken  = createJwtToken(customer);
            var response = new HashMap<String, Object>();
            response.put("token", jwtToken);
            response.put("user", customer);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiErrorResponseDto response = new ApiErrorResponseDto(
                    "Błąd serwera przy rejestracji",
                    ErrorTypeEnum.SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginDto loginDto, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(";"));

            ApiErrorResponseDto response = new ApiErrorResponseDto(
                    errorMessage,
                    ErrorTypeEnum.VALIDATION_PROBLEM,
                    HttpStatus.BAD_REQUEST.value()
            );
            return ResponseEntity.badRequest().body(response);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getCustomerName(),
                            loginDto.getPassword()
                    )
            );
            Customer customer = customerRepository.findByCustomerName(loginDto.getCustomerName());
            String jwtToken = createJwtToken(customer);
            var response = new HashMap<String, Object>();
            response.put("token", jwtToken);
            response.put("user", customer);

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            ApiErrorResponseDto response = new ApiErrorResponseDto(
                    "Błąd serwera przy logowaniu",
                    ErrorTypeEnum.SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private String createJwtToken(Customer customer) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(customer.getCustomerName())
                .issuer(jwtIssuer)
                .expiresAt(now.plusSeconds(24 * 3600))
                .subject(customer.getCustomerName())
                .claim("role", customer.getRole())
                .build();

        var encoder = new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey.getBytes()));
        var params = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return encoder.encode(params).getTokenValue();
    }
}
