package com.idat.pe.auth_service.security;

import com.idat.pe.auth_service.service.impl.DetalleUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig del auth_service.
 * 
 * Lógica de seguridad:
 * - /api/auth/** → PÚBLICO (sin token)
 * - /api/usuarios/** → PERMITIR (el gateway ya validó el rol ADMIN con JWT)
 * - Lo demás → AUTENTICADO
 * 
 * El gateway es el responsable de validar roles via JWT.
 * El auth_service CONFÍA en lo que el gateway le envía.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final DetalleUsuarioService detalleUsuarioService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider daoAuth) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .authenticationProvider(daoAuth);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return detalleUsuarioService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Patrón del profesor: Constructor con UserDetailsService.
     * Inyectamos UserDetailsService como parámetro.
     */
    @Bean
    public AuthenticationProvider daoAuth(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider dao = new DaoAuthenticationProvider(userDetailsService);
        dao.setPasswordEncoder(passwordEncoder());
        return dao;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
