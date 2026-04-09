package com.example.ejercicio8.config.exceptions;

import java.util.List;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends CustomException {

    public InvalidTokenException() {
        super("Token inválido o expirado", HttpStatus.UNAUTHORIZED, List.of("Token inválido o expirado"));
    }
}