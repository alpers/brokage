package com.brokage.config;

import com.brokage.entity.Customer;
import com.brokage.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserConfig implements UserDetailsService {

    private final CustomerService customerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.equals("admin")) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            return User.withUsername("admin")
                    .password(passwordEncoder.encode("admin"))
                    .roles("ADMIN")
                    .build();
        }

        Customer customer = customerService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));

        return User.withUsername(customer.getUsername())
                .password(customer.getPassword())
                .roles("CUSTOMER")
                .build();
    }
}
