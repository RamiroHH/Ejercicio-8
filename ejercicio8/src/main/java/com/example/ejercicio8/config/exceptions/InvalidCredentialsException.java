package com.example.ejercicio8.config.exceptions;

import java.util.List;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends CustomException {

    public InvalidCredentialsException() {
        super("Credenciales inválidas", HttpStatus.UNAUTHORIZED, List.of("Credenciales inválidas"));
    }
}