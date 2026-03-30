package com.idat.pe.auth_service.service.impl;

import com.idat.pe.auth_service.entity.Usuario;
import com.idat.pe.auth_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion de UserDetailsService — patron del profesor.
 *
 * DIFERENCIA con el profesor:
 * - El profesor agrega prefijo "ROLE_" en getAuthorities() porque su BD guarda "ESTUDIANTE".
 * - Nosotros NO agregamos prefijo porque nuestra BD ya guarda "ROLE_ESTUDIANTE".
 *
 * Metodos publicos getAuthorities() y getUserSecurity() son reutilizados
 * directamente desde AuthController, igual que en el patron del profesor.
 */
@RequiredArgsConstructor
@Service
public class DetalleUsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + email));

        List<String> roles = usuario.getRoles().stream()
                .map(rol -> rol.getNombre())
                .toList();

        return getUserSecurity(usuario, getAuthorities(roles));
    }

    
    public List<GrantedAuthority> getAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String rol : roles) {
            // Si el rol ya tiene "ROLE_", úsalo tal cual
            // Si no, agrega el prefijo
            String rolConPrefijo = rol.startsWith("ROLE_") ? rol : "ROLE_" + rol;
            authorities.add(new SimpleGrantedAuthority(rolConPrefijo));
        }
        return authorities;
    }

    /**
     * Patron del profesor: construye el UserDetails de Spring Security.
     * Usa email como username (equivalente al nomusuario del profesor).
     */
    public UserDetails getUserSecurity(Usuario usuario, List<GrantedAuthority> authorities) {
        return new User(
                usuario.getEmail(),   // username = email
                usuario.getPassword(),
                true,                 // enabled
                true,                 // accountNonExpired
                true,                 // credentialsNonExpired
                true,                 // accountNonLocked
                authorities
        );
    }
}
