package com.idat.pe.auth_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

// Equivalente al UsuarioJwt del profesor — datos del usuario autenticado + token
// HU-02: retornado al cliente tras login exitoso
@Data
@Builder
public class UsuarioJwtResponse {
    private Integer id;
    private String nombre;
    private String email;
    private List<String> roles;
    private String token;
}
