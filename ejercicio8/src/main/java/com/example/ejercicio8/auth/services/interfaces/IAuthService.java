package com.example.ejercicio8.auth.services.interfaces;

import com.example.ejercicio8.auth.dtos.request.LoginRequestDto;
import com.example.ejercicio8.auth.dtos.request.RegisterRequestDto;
import com.example.ejercicio8.auth.dtos.response.AuthResponseDto;

public interface IAuthService {

    void register(RegisterRequestDto request);

    AuthResponseDto login(LoginRequestDto request);

    AuthResponseDto refresh(String refreshToken);
}