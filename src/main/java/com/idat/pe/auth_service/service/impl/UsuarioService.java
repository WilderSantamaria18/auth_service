package com.idat.pe.auth_service.service.impl;

import com.idat.pe.auth_service.dto.AuthResponse;
import com.idat.pe.auth_service.dto.RegisterRequest;
import com.idat.pe.auth_service.dto.UsuarioResponse;
import com.idat.pe.auth_service.entity.Rol;
import com.idat.pe.auth_service.entity.Usuario;
import com.idat.pe.auth_service.repository.RolRepository;
import com.idat.pe.auth_service.repository.UsuarioRepository;
import com.idat.pe.auth_service.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * HU-01: Registro.
     * Valida unicidad del email, encripta password y asigna ROLE_ESTUDIANTE.
     */
    @Override
    public AuthResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El correo ya está en uso");
        }

        Rol rolEstudiante = rolRepository.findByNombre("ROLE_ESTUDIANTE")
                .orElseThrow(() -> new RuntimeException(
                        "Rol ROLE_ESTUDIANTE no encontrado. Ejecuta el script SQL."));

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRoles(Set.of(rolEstudiante));

        usuarioRepository.save(usuario);
        return new AuthResponse(null, "Usuario registrado exitosamente. Ya puede iniciar sesión.");
    }

    /**
     * Patron del profesor: getUsuarioByNomusuario — adaptado a email.
     * El AuthController lo usa para obtener la entidad y luego generar el token.
     */
    @Override
    public Usuario getUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
    }

    /**
     * Patron del profesor: getRolesByNomusuario — adaptado a email.
     * Retorna los nombres de los roles tal como estan en BD (ej. "ROLE_ESTUDIANTE").
     * El DetalleUsuarioService los usa en getAuthorities().
     */
    @Override
    public List<String> getRolesByEmail(String email) {
        Usuario usuario = getUsuarioByEmail(email);
        return usuario.getRoles().stream()
                .map(Rol::getNombre)
                .collect(Collectors.toList());
    }

    /**
     * HU-09: Lista todos los usuarios para el administrador.
     * Retorna DTO que no expone la contrasena.
     */
    @Override
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(u -> new UsuarioResponse(
                        u.getId(),
                        u.getNombre(),
                        u.getEmail(),
                        u.getRoles().stream()
                                .map(Rol::getNombre)
                                .collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());
    }
}
