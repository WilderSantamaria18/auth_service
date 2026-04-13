package com.idat.pe.auth_service.service;

import com.idat.pe.auth_service.dto.AuthResponse;
import com.idat.pe.auth_service.dto.RegisterRequest;
import com.idat.pe.auth_service.dto.UsuarioResponse;
import com.idat.pe.auth_service.entity.Usuario;

import java.util.List;

public interface IUsuarioService {

    // HU-01: Registro de usuario
    AuthResponse registrar(RegisterRequest request);

    // HU-02: Busca el usuario por email (usado en el controller para generar el token)
    Usuario getUsuarioByEmail(String email);

    // HU-02: Retorna la lista de nombres de roles del usuario
    List<String> getRolesByEmail(String email);

    // HU-09: Lista todos los usuarios (acceso ADMIN via gateway)
    List<UsuarioResponse> listarUsuarios();

    // NUEVO: Obtener usuario por ID (usado por Feign para validación entre microservicios)
    UsuarioResponse obtenerPorId(Integer id);

    // NUEVO: Actualizar contraseña
    void actualizarPassword(Integer id, com.idat.pe.auth_service.dto.UpdatePasswordRequest request);

    // NUEVO: Actualizar datos de perfil (nombre y email)
    void actualizarDatos(Integer id, com.idat.pe.auth_service.dto.UpdateUsuarioRequest request);
}
