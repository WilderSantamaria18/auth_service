package com.idat.pe.auth_service.remote.client;

import com.idat.pe.auth_service.remote.data.AlumnoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "academic-service")
public interface AlumnoClient {

    @GetMapping("/api/estudiantes") // Ruta ejemplo en academic_service
    List<AlumnoDto> obtenerAlumnos(
            @RequestHeader("Authorization") String token
    );
}
