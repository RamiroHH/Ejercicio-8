package com.example.ejercicio8.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final int MIN_SECRET_BYTES = 32;

    private final JwtProperties properties;

    private SecretKey signingKey;

    @PostConstruct
    void initSigningKey() {
        byte[] keyBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "jwt.secret debe tener al menos " + MIN_SECRET_BYTES + " bytes");
        }

        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // MÉTODO BASE
    private String generateToken(String username, Collection<String> roles, long expiration) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expiration);

        return Jwts.builder()
                .subject(username)
                .claim(JwtClaimNames.ROLES, List.copyOf(roles))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    // ACCESS TOKEN
    public String generateAccessToken(String username, Collection<String> roles) {
        return generateToken(username, roles, properties.getAccessExpiration());
    }

    // REFRESH TOKEN
    public String generateRefreshToken(String username) {
        return generateToken(username, List.of(), properties.getRefreshExpiration());
    }

    // VALIDACIÓN
    public Optional<Claims> parseValidClaims(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(
                    Jwts.parser()
                            .verifyWith(signingKey)
                            .build()
                            .parseSignedClaims(token)
                            .getPayload()
            );
        } catch (JwtException ignored) {
            return Optional.empty();
        }
    }

    // USERNAME
    public Optional<String> extractUsername(String token) {
        return parseValidClaims(token).map(Claims::getSubject);
    }

    //  ROLES
    public List<String> extractRoles(Claims claims) {
        Object raw = claims.get(JwtClaimNames.ROLES);
        if (raw instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }
}