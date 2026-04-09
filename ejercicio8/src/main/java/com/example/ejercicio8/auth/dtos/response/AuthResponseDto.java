package com.example.ejercicio8.auth.dtos.response;

public record AuthResponseDto(
        String accessToken,
        String refreshToken
) {}