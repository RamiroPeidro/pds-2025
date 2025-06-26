package com.pds.sportsmanager.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

/**
 * Utilidad para generar y validar JWT usando la forma “clásica”
 * de JJWT, compatible con cualquier versión (parser()).
 */
@Component
public class JwtUtil {

    private final Key   signingKey;
    private final long  expirationMillis;

    public JwtUtil(
            @Value("${app.jwt.secret}") String base64Secret,
            @Value("${app.jwt.expiration-ms}") long expirationMillis) {

        this.signingKey       = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret));
        this.expirationMillis = expirationMillis;
    }

    /** Genera un JWT HS256 con subject + expiración configurada. */
    public String generateToken(String subject) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Devuelve el subject (email/username) de un token. */
    public String getSubject(String token) {
        return parser().parseClaimsJws(token).getBody().getSubject();
    }

    /** Valida firma y expiración. */
    public boolean isValid(String token) {
        try {
            parser().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    /* -------- helpers -------- */

    private JwtParser parser() {
        // Forma clásica: Jwts.parser() + build()
        return Jwts.parser()
                .setSigningKey(signingKey)
                .build();
    }
}