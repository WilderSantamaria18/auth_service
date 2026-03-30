package com.idat.pe.auth_service.repository;

import com.idat.pe.auth_service.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para la entidad Usuario.
 * Extiende JpaRepository para operaciones CRUD.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    /**
     * Busca un usuario por email.
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByEmail(String email);
    
    /**
     * Verifica si existe un usuario con el email dado.
     * @param email Email del usuario
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);
}
