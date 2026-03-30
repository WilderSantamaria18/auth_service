package com.idat.pe.auth_service.dto;

import lombok.Builder;
import lombok.Data;

// Wrapper generico de respuesta — patron del profesor
// T puede ser UsuarioJwtResponse, List<UsuarioResponse>, etc.
@Data
@Builder
public class GenericResponse<T> {
    private T response;
    private ErrorMessage errorMessage;
}
