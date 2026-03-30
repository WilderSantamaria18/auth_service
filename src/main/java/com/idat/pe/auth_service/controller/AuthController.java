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
 *
 * Flujo de login (igual que el profesor):
 * 1. authManager.authenticate() — valida email + password con DaoAuthenticationProvider
 * 2. usuarioService.getUsuarioByEmail() — obtiene la entidad
 * 3. usuarioService.getRolesByEmail() — obtiene los roles
 * 4. detalleUsuarioService.getAuthorities() — convierte roles a GrantedAuthority
 * 5. jwtService.generateToken() — genera el JWT
 * 6. Retorna GenericResponse<UsuarioJwtResponse>
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
     * Estructura 1:1 con el patron del profesor.
     */
    @PostMapping("/login")
    public ResponseEntity<GenericResponse<UsuarioJwtResponse>> login(
            @RequestBody LoginRequest login) throws Exception {
        GenericResponse<UsuarioJwtResponse> response;
        try {
            System.out.println("1. Iniciando login para email: " + login.getEmail());
            
            // 1. Valida credenciales usando AuthenticationManager inyectado
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            login.getEmail(),
                            login.getPassword()));
            System.out.println("2. Credenciales validadas en authManager");

            // 2. Obtiene entidad y roles
            Usuario usuario = usuarioService.getUsuarioByEmail(login.getEmail());
            System.out.println("3. Usuario obtenido: " + usuario.getNombre());
            
            List<String> roles = usuarioService.getRolesByEmail(login.getEmail());
            System.out.println("4. Roles obtenidos: " + roles);

            // 3. Convierte roles a GrantedAuthority y genera token
            List<GrantedAuthority> authorities = detalleUsuarioService.getAuthorities(roles);
            System.out.println("5. Authorities convertidas: " + 
                authorities.stream().map(GrantedAuthority::getAuthority).toList());
            System.out.println("5b. Generando token...");
            
            String token = jwtService.generateToken(usuario, authorities);
            System.out.println("6. Token generado exitosamente");

            // 4. Arma respuesta con datos del usuario + token
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
            System.out.println("7. Login completado exitosamente");
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            System.out.println("ERROR: Autenticacion fallida: " + e.getMessage());
            // HU-02: Credenciales incorrectas
            response = GenericResponse.<UsuarioJwtResponse>builder()
                    .errorMessage(ErrorMessage.builder()
                            .statusCode(HttpStatus.UNAUTHORIZED.value())
                            .dateError(LocalDate.now())
                            .message("El correo o la contraseña son inválidos")
                            .build())
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            // HU-02: Cualquier otro error durante login
            System.err.println("ERROR EN LOGIN: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
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
}
