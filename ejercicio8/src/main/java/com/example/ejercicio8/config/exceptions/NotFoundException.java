package com.example.ejercicio8.config.exceptions;

import java.util.List;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomException {

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, List.of(message));
    }

    public NotFoundException(String message, HttpStatus status, List<String> errors) {
        super(message, status, errors);
    }
}