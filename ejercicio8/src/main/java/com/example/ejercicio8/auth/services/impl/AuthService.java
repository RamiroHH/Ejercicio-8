package com.example.ejercicio8.auth.services.impl;

import com.example.ejercicio8.auth.dtos.request.LoginRequestDto;
import com.example.ejercicio8.auth.dtos.request.RegisterRequestDto;
import com.example.ejercicio8.auth.dtos.response.AuthResponseDto;
import com.example.ejercicio8.auth.jwt.JwtProperties;
import com.example.ejercicio8.auth.jwt.JwtService;
import com.example.ejercicio8.auth.models.UserEntity;
import com.example.ejercicio8.auth.models.UserRole;
import com.example.ejercicio8.auth.respository.UserRepository;
import com.example.ejercicio8.auth.services.interfaces.IAuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService implements IAuthService {

    private static final String TOKEN_TYPE_BEARER = "Bearer";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;


    /**
     * Registro: rechaza username duplicado, codifica la contraseña y guarda el usuario con rol por defecto.
     */
    @Transactional
    @Override
    public void register(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException();
        }
        UserEntity user = UserEntity.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(UserRole.ROLE_USER)
                .build();
        userRepository.save(user);
    }

    /**
     * Login: el {@link AuthenticationManager} valida credenciales; si son correctas se emite un JWT con los
     * mismos nombres de rol que {@link UserDetails#getAuthorities()}.
     */
    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        try {
            // Encapsular la autenticación en un try-catch para manejar el caso de credenciales inválidas
            // Si las credenciales son inválidas, se lanza una excepción de tipo InvalidCredentialsException
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password())
            );

            // Obtener el usuario autenticado
            UserDetails principal = (UserDetails) authentication.getPrincipal();

            // Obtener los roles del usuario
            var roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

            // Generar el token JWT
            String accessToken = jwtService.generateToken(principal.getUsername(), roles);

            // Devolver el token JWT
            return new AuthResponseDto(accessToken, TOKEN_TYPE_BEARER, jwtProperties.expirationMs());

            // Si las credenciales son inválidas, se lanza una excepción de tipo InvalidCredentialsException
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }
    }
}