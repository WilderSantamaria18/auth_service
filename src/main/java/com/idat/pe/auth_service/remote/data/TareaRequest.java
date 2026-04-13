package com.idat.pe.auth_service.remote.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TareaRequest {
    private String titulo;
    private String descripcion;
    private String prioridad; // Enum en el otro lado, String aquí
}
