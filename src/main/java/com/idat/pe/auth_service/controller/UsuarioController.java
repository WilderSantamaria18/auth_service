package com.idat.pe.auth_service.controller;

import com.idat.pe.auth_service.dto.ErrorMessage;
import com.idat.pe.auth_service.dto.GenericResponse;
import com.idat.pe.auth_service.dto.UsuarioResponse;
import com.idat.pe.auth_service.service.impl.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * HU-09: Consulta de usuarios registrados - PROTEGIDO PARA ROLE_ADMIN
 * T-010: Implementación de autorización en endpoint
 * GET /api/usuarios → Solo ROLE_ADMIN
 */
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
@RestController
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping({"", "/"})
    public ResponseEntity<GenericResponse<List<UsuarioResponse>>> listar() {
        GenericResponse<List<UsuarioResponse>> response;
        try {
            List<UsuarioResponse> usuarios = usuarioService.listarUsuarios();
            
            if (usuarios.isEmpty()) {
                response = GenericResponse.<List<UsuarioResponse>>builder()
                        .errorMessage(ErrorMessage.builder()
                                .statusCode(HttpStatus.NO_CONTENT.value())
                                .dateError(LocalDate.now())
                                .message("No hay usuarios registrados")
                                .build())
                        .build();
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
            }
            response = GenericResponse.<List<UsuarioResponse>>builder()
                    .response(usuarios)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response = GenericResponse.<List<UsuarioResponse>>builder()
                    .errorMessage(ErrorMessage.builder()
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .dateError(LocalDate.now())
                            .message("Error al consultar usuarios")
                            .description(e.getMessage())
                            .build())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }
}
