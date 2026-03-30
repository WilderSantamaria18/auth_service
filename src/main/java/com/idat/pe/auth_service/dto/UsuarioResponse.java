package com.idat.pe.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

// DTO para HU-09: Consulta de usuarios (ADMIN)
// No expone el campo password por seguridad
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponse {
    private Integer id;
    private String nombre;
    private String email;
    private Set<String> roles;
}
