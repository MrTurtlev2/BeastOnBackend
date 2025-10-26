package com.beaston.backend.controllers;

import com.beaston.backend.DTO.api.ApiErrorResponseDto;
import com.beaston.backend.DTO.auth.LoginDto;
import com.beaston.backend.DTO.auth.RegisterDto;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${JWT_ACCESS_TOKEN_EXP}")
    private long ACCESS_TOKEN_EXP;

    @Value("${JWT_REFRESH_TOKEN_EXP}")
    private long REFRESH_TOKEN_EXP;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${JWT_SECRET_KEY}")
    private String jwtSecretKey;

    @Value("${SPRING_JWT_ISSUER}")
    private String jwtIssuer;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterDto registerDto,
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

        try {
            if (customerRepository.findByCustomerName(registerDto.getCustomerName()) != null) {
                ApiErrorResponseDto response = new ApiErrorResponseDto(
                        "Użytkownik o podanym loginie już istnieje",
                        ErrorTypeEnum.BUSINESS_ERROR,
                        HttpStatus.BAD_REQUEST.value()
                );
                return ResponseEntity.badRequest().body(response);
            }

            if (customerRepository.findByEmail(registerDto.getEmail()) != null) {
                ApiErrorResponseDto response = new ApiErrorResponseDto(
                        "Użytkownik o podanym e-mail już istnieje",
                        ErrorTypeEnum.BUSINESS_ERROR,
                        HttpStatus.BAD_REQUEST.value()
                );
                return ResponseEntity.badRequest().body(response);
            }

            Customer customer = new Customer();
            customer.setCustomerName(registerDto.getCustomerName());
            customer.setEmail(registerDto.getEmail());
            customer.setRole("client");
            customer.setPasswordHash(new BCryptPasswordEncoder().encode(registerDto.getPassword()));
            customerRepository.save(customer);

            String accessToken = createAccessToken(customer);
            String refreshToken = createRefreshToken(customer);

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
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
    public ResponseEntity<Object> login(@RequestBody LoginDto loginDto,
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

        try {
            Customer customer = customerRepository.findByCustomerName(loginDto.getCustomerName());
            if (customer == null) {
                ApiErrorResponseDto response = new ApiErrorResponseDto(
                        "Nieprawidłowy login lub hasło",
                        ErrorTypeEnum.AUTH_ERROR,
                        HttpStatus.UNAUTHORIZED.value()
                );
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getCustomerName(),
                            loginDto.getPassword()
                    )
            );

            String accessToken = createAccessToken(customer);
            String refreshToken = createRefreshToken(customer);

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("user", customer);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            ApiErrorResponseDto response = new ApiErrorResponseDto(
                    "Nieprawidłowy login lub hasło",
                    ErrorTypeEnum.AUTH_ERROR,
                    HttpStatus.UNAUTHORIZED.value()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        } catch (Exception ex) {
            ApiErrorResponseDto response = new ApiErrorResponseDto(
                    "Błąd serwera przy logowaniu",
                    ErrorTypeEnum.SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            ApiErrorResponseDto response = new ApiErrorResponseDto(
                    "Brak refresh tokena",
                    ErrorTypeEnum.VALIDATION_PROBLEM,
                    HttpStatus.BAD_REQUEST.value()
            );
            return ResponseEntity.badRequest().body(response);
        }

        try {
            var decoder = NimbusJwtDecoder.withSecretKey(
                    new javax.crypto.spec.SecretKeySpec(jwtSecretKey.getBytes(), "HmacSHA256")
            ).build();

            var jwt = decoder.decode(refreshToken);
            String tokenType = (String) jwt.getClaims().get("type");
            if (!"refresh".equals(tokenType)) {
                ApiErrorResponseDto response = new ApiErrorResponseDto(
                        "Nieprawidłowy typ tokena",
                        ErrorTypeEnum.AUTH_ERROR,
                        HttpStatus.UNAUTHORIZED.value()
                );
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String username = jwt.getSubject();
            Customer customer = customerRepository.findByCustomerName(username);
            if (customer == null) {
                ApiErrorResponseDto response = new ApiErrorResponseDto(
                        "Użytkownik nie istnieje",
                        ErrorTypeEnum.AUTH_ERROR,
                        HttpStatus.UNAUTHORIZED.value()
                );
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String newAccessToken = createAccessToken(customer);
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", newAccessToken);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiErrorResponseDto response = new ApiErrorResponseDto(
                    "Refresh token wygasł lub jest nieprawidłowy",
                    ErrorTypeEnum.AUTH_ERROR,
                    HttpStatus.UNAUTHORIZED.value()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    private String createAccessToken(Customer customer) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(ACCESS_TOKEN_EXP))
                .subject(customer.getCustomerName())
                .claim("id", customer.getId())
                .claim("role", customer.getRole())
                .claim("type", "access")
                .build();

        var encoder = new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey.getBytes()));
        var params = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return encoder.encode(params).getTokenValue();
    }

    private String createRefreshToken(Customer customer) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(REFRESH_TOKEN_EXP))
                .subject(customer.getCustomerName())
                .claim("type", "refresh")
                .build();

        var encoder = new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey.getBytes()));
        var params = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return encoder.encode(params).getTokenValue();
    }
}
