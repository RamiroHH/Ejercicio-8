package com.example.ejercicio8.config.exceptions;

import java.util.List;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends CustomException {

    public UserAlreadyExistsException() {
        super(
                "El nombre de usuario ya está registrado",
                HttpStatus.CONFLICT,
                List.of("El nombre de usuario ya está registrado"));
    }
}