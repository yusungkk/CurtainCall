package com.backstage.curtaincall.security;

import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    private Key signingKey;

    private final long EXPIRATION_TIME = 1000 * 60 * 30; // 30분

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("Secret key must not be null or empty");
        }
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserEmail(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            log.error("########## JWT expired {}", e.getMessage());
            throw new CustomException(CustomErrorCode.EXPIRED_TOKEN);
        } catch (JwtException e) {
            log.error("########## Invalid JWT token {}", e.getMessage());
            throw new CustomException(CustomErrorCode.INVALID_TOKEN);
        }
    }

    public String getUserRole(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("role", String.class);
        } catch (ExpiredJwtException e) {
            log.error("JWT expired {}", e.getMessage());
            throw new CustomException(CustomErrorCode.EXPIRED_TOKEN);
        } catch (JwtException e) {
            log.error("Invalid JWT token {}", e.getMessage());
            throw new CustomException(CustomErrorCode.INVALID_TOKEN);
        }
    }

    public String extractEmail(String token) {
        return (token != null) ? getUserEmail(token) : null;
    }
}