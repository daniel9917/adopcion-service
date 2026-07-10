package com.example.adoption.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/pets", "/pets/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/applications").permitAll()
                        .requestMatchers(HttpMethod.POST, "/pets").hasRole("ORG_MEMBER")
                        .requestMatchers(HttpMethod.PATCH, "/pets/**", "/applications/**").hasRole("ORG_MEMBER")
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {});
        return http.build();
    }
}
