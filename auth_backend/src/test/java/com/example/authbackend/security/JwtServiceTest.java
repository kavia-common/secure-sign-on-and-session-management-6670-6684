package com.example.authbackend.security;

import com.example.authbackend.config.OAuthProperties;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

public class JwtServiceTest {

    @Test
    void generateAndParseToken() {
        OAuthProperties props = new OAuthProperties();
        OAuthProperties.Jwt jwt = new OAuthProperties.Jwt();
        jwt.setIssuer("test-issuer");
        jwt.setSecret("01234567890123456789012345678901"); // 32 bytes
        jwt.setExpirationSeconds(60);
        props.setJwt(jwt);

        JwtService service = new JwtService(props);
        String token = service.generate("subj", Map.of("email", "a@example.com", "roles", java.util.List.of("USER")));
        assertNotNull(token);

        Claims claims = service.tryParseClaims(token);
        assertNotNull(claims);
        assertEquals("subj", claims.getSubject());
        assertEquals("a@example.com", claims.get("email"));
    }
}
