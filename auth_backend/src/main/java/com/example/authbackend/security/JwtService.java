package com.example.authbackend.security;

import com.example.authbackend.config.OAuthProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * PUBLIC_INTERFACE
 * JWT service using HS256 for signing and validation.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final String issuer;
    private final long expirationSeconds;

    public JwtService(OAuthProperties properties) {
        String secret = properties.getJwt().getSecret();
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.issuer = properties.getJwt().getIssuer();
        this.expirationSeconds = properties.getJwt().getExpirationSeconds();
    }

    /**
     * PUBLIC_INTERFACE
     * Generate a JWT with given subject and custom claims.
     */
    public String generate(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expirationSeconds)))
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256);

        if (issuer != null && !issuer.isBlank()) {
            builder.setIssuer(issuer);
        }
        return builder.compact();
    }

    /**
     * PUBLIC_INTERFACE
     * Parse and validate a JWT, returning claims if valid; throws if invalid/expired.
     */
    public Jws<Claims> parse(String token) throws JwtException {
        return Jwts.parserBuilder()
                .requireIssuer(issuer != null && !issuer.isBlank() ? issuer : null)
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    /**
     * PUBLIC_INTERFACE
     * Extract claims without throwing checked exceptions; returns null if invalid.
     */
    public Claims tryParseClaims(String token) {
        try {
            return parse(token).getBody();
        } catch (JwtException e) {
            return null;
        }
    }
}
