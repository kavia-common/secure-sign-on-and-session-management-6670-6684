package com.example.authbackend.web;

import com.example.authbackend.config.OAuthProperties;
import com.example.authbackend.oauth.OAuthService;
import com.example.authbackend.security.JwtService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * PUBLIC_INTERFACE
 * REST endpoints for OAuth2 login flows, logout, and session validation.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
public class AuthController {

    private final OAuthService oauthService;
    private final OAuthProperties props;
    private final JwtService jwtService;

    public AuthController(OAuthService oauthService, OAuthProperties props, JwtService jwtService) {
        this.oauthService = oauthService;
        this.props = props;
        this.jwtService = jwtService;
    }

    /**
     * PUBLIC_INTERFACE
     * POST /auth/login/{provider} - returns a redirect URL to start OAuth2 login.
     */
    @PostMapping("/login/{provider}")
    @Operation(summary = "Initiate OAuth2 login", description = "Returns a redirect URL to the provider authorization page.")
    public ResponseEntity<Map<String, Object>> login(@PathVariable String provider, @RequestParam(value = "state", required = false) String state) {
        try {
            String redirect = oauthService.buildLoginRedirect(provider, state == null ? "state" : state);
            Map<String, Object> body = Map.of("redirectUrl", redirect);
            return ResponseEntity.ok(body);
        } catch (IllegalStateException e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "provider_not_configured");
            err.put("message", e.getMessage());
            err.put("instructions", "Set OAUTH_" + provider.toUpperCase() + "_CLIENT_ID and OAUTH_" + provider.toUpperCase() + "_CLIENT_SECRET");
            return ResponseEntity.badRequest().body(err);
        }
    }

    /**
     * PUBLIC_INTERFACE
     * GET /auth/callback/{provider} - handle OAuth2 callback and return JWT.
     */
    @GetMapping("/callback/{provider}")
    @Operation(summary = "OAuth2 callback", description = "Exchanges code for tokens, upserts user, and returns a JWT.")
    public ResponseEntity<Map<String, Object>> callback(@PathVariable String provider,
                                                        @RequestParam("code") String code,
                                                        @RequestParam(value = "state", required = false) String state) throws Exception {
        Map<String, Object> result = oauthService.handleCallback(provider, code);
        return ResponseEntity.ok(result);
    }

    /**
     * PUBLIC_INTERFACE
     * POST /auth/logout - For JWT stateless auth, logout is a client-side token discard.
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidates session. For JWT, instructs client to delete token.")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> body = Map.of("success", true, "message", "For JWT sessions, discard the token on client.");
        return ResponseEntity.ok(body);
    }

    /**
     * PUBLIC_INTERFACE
     * GET /auth/session - Validates the bearer token and returns session info.
     */
    @GetMapping("/session")
    @Operation(summary = "Validate session", description = "Validates the Bearer token and returns claims.")
    public ResponseEntity<Map<String, Object>> session(HttpServletRequest request, Authentication authentication) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "missing_authorization"));
        }
        String token = header.substring("Bearer ".length()).trim();
        Claims claims = jwtService.tryParseClaims(token);
        if (claims == null) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid_token"));
        }
        Map<String, Object> session = new HashMap<>();
        session.put("subject", claims.getSubject());
        session.put("email", claims.get("email"));
        session.put("name", claims.get("name"));
        session.put("avatarUrl", claims.get("avatarUrl"));
        session.put("roles", claims.get("roles"));
        session.put("expiresAt", Instant.ofEpochMilli(claims.getExpiration().getTime()).toString());
        return ResponseEntity.ok(session);
    }

    /**
     * PUBLIC_INTERFACE
     * GET /auth/health - returns readiness/config information.
     */
    @GetMapping("/health")
    @Operation(summary = "Health/config readiness", description = "Indicates if required provider configs are present.")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "ok");
        Map<String, Object> providers = new HashMap<>();
        Map<String, OAuthProperties.Provider> configured = props.getProviders();
        if (configured != null) {
            configured.forEach((k, v) -> {
                boolean ready = v.getClientId() != null && !v.getClientId().isBlank()
                        && v.getClientSecret() != null && !v.getClientSecret().isBlank();
                providers.put(k, Map.of("configured", ready));
            });
        }
        status.put("providers", providers);
        boolean dev = props.getDev() != null && props.getDev().isLoginEnabled();
        status.put("devLoginEnabled", dev);
        return ResponseEntity.ok(status);
    }

    /**
     * PUBLIC_INTERFACE
     * POST /auth/login/dev - Development-only email login to mint JWT.
     */
    @PostMapping("/login/dev")
    @Operation(summary = "Dev email login", description = "When DEV_LOGIN_ENABLED=true, accepts email to mint a JWT.")
    public ResponseEntity<Map<String, Object>> devLogin(@RequestBody Map<String, String> payload) {
        boolean enabled = props.getDev() != null && props.getDev().isLoginEnabled();
        if (!enabled) {
            return ResponseEntity.status(403).body(Map.of("error", "dev_login_disabled"));
        }
        String email = payload.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "email_required"));
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("name", email);
        claims.put("roles", java.util.List.of("USER"));
        String token = jwtService.generate("dev:" + email, claims);
        return ResponseEntity.ok(Map.of("token", token, "user", Map.of("email", email, "name", email, "roles", java.util.List.of("USER"))));
    }
}
