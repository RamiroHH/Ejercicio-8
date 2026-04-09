package com.example.ejercicio8.auth.services.impl;

import com.example.ejercicio8.auth.dtos.request.LoginRequestDto;
import com.example.ejercicio8.auth.dtos.request.RegisterRequestDto;
import com.example.ejercicio8.auth.dtos.response.AuthResponseDto;
import com.example.ejercicio8.auth.jwt.JwtService;
import com.example.ejercicio8.auth.models.UserEntity;
import com.example.ejercicio8.auth.models.UserRole;
import com.example.ejercicio8.auth.respository.UserRepository;
import com.example.ejercicio8.auth.services.interfaces.IAuthService;

import com.example.ejercicio8.config.exceptions.InvalidCredentialsException;
import com.example.ejercicio8.config.exceptions.InvalidTokenException;
import com.example.ejercicio8.config.exceptions.UserAlreadyExistsException;
import lombok.AllArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * REGISTER
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
     * LOGIN → devuelve ACCESS + REFRESH
     */
    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.username(),
                            request.password()
                    )
            );

            UserDetails principal = (UserDetails) authentication.getPrincipal();

            var roles = principal.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            String accessToken = jwtService.generateAccessToken(principal.getUsername(), roles);
            String refreshToken = jwtService.generateRefreshToken(principal.getUsername());

            return new AuthResponseDto(accessToken, refreshToken);

        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }
    }

    /**
     * REFRESH → devuelve nuevo ACCESS usando refresh token
     */
    @Override
    public AuthResponseDto refresh(String refreshToken) {

        String username = jwtService.extractUsername(refreshToken)
                .orElseThrow(InvalidTokenException::new);

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow();

        List<String> roles = List.of(user.getRole().name());

        String newAccessToken = jwtService.generateAccessToken(username, roles);

        return new AuthResponseDto(newAccessToken, refreshToken);
    }
}