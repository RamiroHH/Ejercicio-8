package com.example.ejercicio8.auth.controllers;

import com.example.ejercicio8.auth.dtos.request.LoginRequestDto;
import com.example.ejercicio8.auth.dtos.request.RegisterRequestDto;
import com.example.ejercicio8.auth.dtos.response.AuthResponseDto;
import com.example.ejercicio8.auth.services.interfaces.IAuthService;
import com.example.ejercicio8.config.BaseResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final IAuthService authService;

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Void>> register(@Valid @RequestBody RegisterRequestDto request) {
        authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ok(null, "Usuario registrado correctamente"));
    }

    // Devuelve access + refresh
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {

        AuthResponseDto body = authService.login(request);

        return ResponseEntity.ok(BaseResponse.ok(body, "Autenticación correcta"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<AuthResponseDto>> refresh(@RequestBody String refreshToken) {

        AuthResponseDto body = authService.refresh(refreshToken);

        return ResponseEntity.ok(BaseResponse.ok(body, "Token renovado correctamente"));
    }
}