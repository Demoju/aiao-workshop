package com.tableorder.security;

import com.tableorder.customer.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final SessionService sessionService;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(
                new SessionAuthenticationFilter(sessionService),
                UsernamePasswordAuthenticationFilter.class
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/customer/login").permitAll()
                .requestMatchers("/api/customer/menus").permitAll()
                .requestMatchers("/api/customer/categories").permitAll()
                .requestMatchers("/api/customer/**").authenticated()
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().permitAll()
            );
        
        return http.build();
    }
}
