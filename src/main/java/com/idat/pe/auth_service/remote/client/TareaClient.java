package com.idat.pe.auth_service.remote.client;

import com.idat.pe.auth_service.remote.data.TareaRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "task-service")
public interface TareaClient {

    @PostMapping("/api/tareas")
    Object crearTarea(
            @RequestBody TareaRequest request,
            @RequestHeader("Authorization") String token
    );
}
