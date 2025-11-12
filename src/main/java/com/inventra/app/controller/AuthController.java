package com.inventra.app.controller;


import com.inventra.app.config.payload.ApiResponse;
import com.inventra.app.config.payload.ErrorItemDTO;
import com.inventra.app.config.payload.ResponseHandler;
import com.inventra.app.entity.dto.AuthResponse;
import com.inventra.app.entity.dto.LoginRequest;
import com.inventra.app.service.impl.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody @Valid LoginRequest request, BindingResult result) {
        UUID uuid = UUID.randomUUID();
        if (result.hasErrors()) {
            List<ErrorItemDTO> errores = result.getFieldErrors().stream()
                    .map(error -> new ErrorItemDTO(
                            "E400",
                            error.getDefaultMessage(),
                            error.getField()))
                    .collect(Collectors.toList());

            return ResponseHandler.badRequestResponse(errores, uuid.toString());
        }
        AuthResponse authResponse = authService.login(request);
        return ResponseHandler.successResponse(authResponse, uuid.toString());
    }
}
