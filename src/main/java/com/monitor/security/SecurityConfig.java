package com.monitor.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // desativa CSRF para facilitar testes no Postman
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll() // ← ADICIONE ESTA LINHA
                        .requestMatchers("/api/medicoes/**").authenticated()
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers.frameOptions().disable()) // ← ADICIONE ESTA LINHA (para H2)
                .httpBasic(Customizer.withDefaults()); // autenticação via Basic Auth (forma atualizada)

        return http.build();
    }
}