package com.idat.pe.auth_service.repository;

import com.idat.pe.auth_service.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // HU-02: Busca usuario por email para autenticacion
    Optional<Usuario> findByEmail(String email);

    // HU-01: Verifica si ya existe el email antes de registrar
    boolean existsByEmail(String email);
}
