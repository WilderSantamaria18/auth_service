package com.idat.pe.auth_service.repository;

import com.idat.pe.auth_service.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    // Busca el rol por nombre para asignarlo al registrar un usuario (HU-01)
    Optional<Rol> findByNombre(String nombre);
}
