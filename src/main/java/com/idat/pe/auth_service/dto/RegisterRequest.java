package com.idat.pe.auth_service.dto;

import lombok.Data;

// DTO para HU-01: Registro de usuario
@Data
public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
}
