package com.idat.pe.auth_service.dto;

import lombok.Data;

// DTO para HU-02: Inicio de sesion
@Data
public class LoginRequest {
    private String email;
    private String password;
}
