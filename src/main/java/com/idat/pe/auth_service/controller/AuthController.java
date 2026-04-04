package com.idat.pe.auth_service.controller;

import com.idat.pe.auth_service.dto.*;
import com.idat.pe.auth_service.entity.Usuario;
import com.idat.pe.auth_service.security.IJwtService;
import com.idat.pe.auth_service.service.IUsuarioService;
import com.idat.pe.auth_service.service.impl.DetalleUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de autenticacion — patron del profesor adaptado.
 */
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final IUsuarioService usuarioService;
    private final DetalleUsuarioService detalleUsuarioService;
    private final IJwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * HU-01: Registro de usuario.
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<GenericResponse<String>> register(
            @RequestBody RegisterRequest request) {
        GenericResponse<String> response;
        try {
            AuthResponse result = usuarioService.registrar(request);
            response = GenericResponse.<String>builder()
                    .response(result.getMensaje())
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response = GenericResponse.<String>builder()
                    .errorMessage(ErrorMessage.builder()
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .dateError(LocalDate.now())
                            .message(e.getMessage())
                            .build())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * HU-02: Inicio de sesion — retorna JWT.
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<GenericResponse<UsuarioJwtResponse>> login(
            @RequestBody LoginRequest login) throws Exception {
        GenericResponse<UsuarioJwtResponse> response;
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            login.getEmail(),
                            login.getPassword()));

            Usuario usuario = usuarioService.getUsuarioByEmail(login.getEmail());
            List<String> roles = usuarioService.getRolesByEmail(login.getEmail());
            List<GrantedAuthority> authorities = detalleUsuarioService.getAuthorities(roles);
            
            String token = jwtService.generateToken(usuario, authorities);

            UsuarioJwtResponse usuarioJwt = UsuarioJwtResponse.builder()
                    .id(usuario.getId())
                    .nombre(usuario.getNombre())
                    .email(usuario.getEmail())
                    .roles(roles)
                    .token(token)
                    .build();

            response = GenericResponse.<UsuarioJwtResponse>builder()
                    .response(usuarioJwt)
                    .build();
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            response = GenericResponse.<UsuarioJwtResponse>builder()
                    .errorMessage(ErrorMessage.builder()
                            .statusCode(HttpStatus.UNAUTHORIZED.value())
                            .dateError(LocalDate.now())
                            .message("El correo o la contraseña son inválidos")
                            .build())
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response = GenericResponse.<UsuarioJwtResponse>builder()
                    .errorMessage(ErrorMessage.builder()
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .dateError(LocalDate.now())
                            .message("Error al procesar login: " + e.getMessage())
                            .build())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * HU-09: Listado de usuarios para administradores.
     * GET /api/auth/usuarios
     */
    @GetMapping("/usuarios")
    public ResponseEntity<GenericResponse<List<UsuarioResponse>>> listarUsuarios() {
        try {
            List<UsuarioResponse> usuarios = usuarioService.listarUsuarios();
            return ResponseEntity.ok(GenericResponse.<List<UsuarioResponse>>builder()
                    .response(usuarios)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.<List<UsuarioResponse>>builder()
                            .errorMessage(ErrorMessage.builder()
                                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .dateError(LocalDate.now())
                                    .message("Error al listar usuarios: " + e.getMessage())
                                    .build())
                            .build());
        }
    }
}
