package com.beaston.backend.services;

import com.beaston.backend.entities.Customer;
import com.beaston.backend.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class CustomerService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByCustomerName(username);

        if (customer != null) {
            var springUser = User.withUsername(customer.getCustomerName())
                    .password(customer.getPasswordHash())
                    .roles(customer.getRole())
                    .build();

            return springUser;
        }

        return null;
    }

    public Long getAuthenticatedCustomerId(Principal principal) {
        String username = principal.getName();
        Customer customer = customerRepository.findByCustomerName(username);
        if (customer == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return customer.getId();
    }
}
