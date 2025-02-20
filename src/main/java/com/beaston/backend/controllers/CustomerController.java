package com.beaston.backend.controllers;
import com.beaston.backend.DTO.LoginDto;
import com.beaston.backend.DTO.RegisterDto;
import com.beaston.backend.entities.Customer;
import com.beaston.backend.repositories.CustomerRepository;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
public class CustomerController {

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
            @RequestBody RegisterDto registerDto,
            BindingResult result) {

        if (result.hasErrors()) {
            var errorList = result.getAllErrors();
            var errorsMap = new HashMap<String, String>();

            for (int i = 0; i < errorList.size(); i++) {
                var error = (FieldError) errorList.get(i);
                errorsMap.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errorsMap);
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
                return ResponseEntity.badRequest().body("User with this name already exists");
            }
            var otherUserEmail = customerRepository.findByEmail(registerDto.getEmail());
            if (otherUserEmail != null) {
                return ResponseEntity.badRequest().body("User with this email already exists");
            }
            customerRepository.save(customer);
            String jwtToken  = createJwtToken(customer);
            var response = new HashMap<String, Object>();
            response.put("token", jwtToken);
            response.put("user", customer);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Exeption");
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Error");
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginDto loginDto, BindingResult result) {
        if (result.hasErrors()) {
            var errorList = result.getAllErrors();
            var errorsMap = new HashMap<String, String>();

            for (int i = 0; i < errorList.size(); i++) {
                var error = (FieldError) errorList.get(i);
                errorsMap.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errorsMap);
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
            System.out.println("exeption: ");
            ex.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Error");
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

//    private String createJwtToken(Customer customer) {
//        Instant now = Instant.now();
//
//        JWTClaimsSet claims = JWTClaimsSet.builder()
//                .issiuer(jwtIssuer)
//                .issiuedAt(now)
//                .expiresAt(now.plusSeconds(24 * 3600))
//                .subject(customer.getUsername())
//                .claim("role", customer.getRole())
//                .build();
//
//        var encoder = new NimbusJwtEncoder(
//                new ImmutableSecret<>(jwtSecretKey.getBytes()));
//        var params = JwtEncoderParameters.from(
//                JwsHeader.with(MacAlgorithm.HS256).build(), claims);
//        return encoder.encode(params).getTokenValue();
//    }

//    @PostMapping("/register")
//    public String registerUser(@RequestBody UserRegistrationDto authRequest) {
//       return "it worked";
//    }
//    @PostMapping("/register2")
//    public String registerUser2(@RequestBody UserRegistrationDto authRequest) {
//        return "it worked2";
//    }

}
