package com.idat.pe.auth_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

// Estructura de error estandarizada — patron del profesor
@Builder
@Data
public class ErrorMessage {
    private Integer statusCode;
    private LocalDate dateError;
    private String message;
    private String description;
}
