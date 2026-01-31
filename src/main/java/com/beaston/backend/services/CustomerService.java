package com.beaston.backend.services;

import com.beaston.backend.entities.Customer;
import com.beaston.backend.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class CustomerService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(email);

        if (customer == null) {
            throw new UsernameNotFoundException("Użytkownik nie istnieje: " + email);
        }
        var springUser = User.withUsername(customer.getEmail())
                .password(customer.getPasswordHash())
                .roles(customer.getRole())
                .build();

        return springUser;
    }

    public Long getAuthenticatedCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaim("id");
        }
        throw new IllegalStateException("Brak uwierzytelnienia");
    }
}
