package com.idat.pe.auth_service.security;

import com.idat.pe.auth_service.entity.Usuario;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface IJwtService {

    // HU-02: Genera token JWT con id y roles del usuario
    String generateToken(Usuario usuario, List<GrantedAuthority> authorities);

    // Extrae los claims (datos) del token
    Claims obtenerClaims(String token);

    // Valida que el token sea correcto y no este vencido
    boolean validarToken(String token);

    // Extrae el token del header Authorization
    String extraerTokenUsuario(HttpServletRequest request);

    // Genera la autenticacion en el SecurityContextHolder
    void generarAutenticacion(Claims claims);
}

