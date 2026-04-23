package com.example.proyectoJuegos.support;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public final class TestJwtSupport {

    private TestJwtSupport() {
    }

    public static String bearer(String token) {
        return "Bearer " + token;
    }

    public static UserDetails principal(String email, String... roles) {
        return User.withUsername(email)
                .password("password")
                .roles(roles)
                .build();
    }

    public static String tokenWithWrongSignature(String subject, String jwtSecret) {
        String wrongSecret = jwtSecret + "-wrong";
        var key = Keys.hmacShaKeyFor(wrongSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public static String expiredToken(String subject, String jwtSecret) {
        var key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis() - 7_200_000))
                .setExpiration(new Date(System.currentTimeMillis() - 3_600_000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}

