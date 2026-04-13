package com.idat.pe.auth_service.remote.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoDto {
    private Integer id;
    private String nombre;
    private String serie;
    private Integer usuarioId;
}
