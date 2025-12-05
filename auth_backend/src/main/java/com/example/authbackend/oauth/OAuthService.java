package com.example.authbackend.oauth;

import com.example.authbackend.config.OAuthProperties;
import com.example.authbackend.security.JwtService;
import com.example.authbackend.user.User;
import com.example.authbackend.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Coordinates OAuth2 login flow with providers and issues JWTs.
 */
@Service
public class OAuthService {

    private final ProviderRegistry registry;
    private final OAuthProperties props;
    private final UserRepository users;
    private final JwtService jwtService;

    public OAuthService(ProviderRegistry registry, OAuthProperties props, UserRepository users, JwtService jwtService) {
        this.registry = registry;
        this.props = props;
        this.users = users;
        this.jwtService = jwtService;
    }

    public String buildLoginRedirect(String provider, String state) {
        ProviderClient client = registry.get(provider);
        if (!client.isConfigured()) {
            throw new IllegalStateException("Provider " + provider + " is not configured. Set env vars for client id/secret.");
        }
        String cb = buildCallbackUrl(provider);
        // append redirect_uri if required by provider (most do)
        String url = UriComponentsBuilder.fromHttpUrl(client.buildAuthorizationUrl(state))
                .queryParam("redirect_uri", cb)
                .build()
                .toUriString();
        return url;
    }

    public Map<String, Object> handleCallback(String provider, String code) throws Exception {
        ProviderClient client = registry.get(provider);
        if (!client.isConfigured()) {
            throw new IllegalStateException("Provider " + provider + " is not configured.");
        }
        String redirectUri = buildCallbackUrl(provider);
        Map<String, Object> tokenResp = client.exchangeCodeForToken(code, redirectUri);
        Map<String, Object> profile = client.fetchUserInfo(tokenResp);

        // Map profile fields
        String sub = stringOrNull(profile.get("sub"));
        if (sub == null) {
            // GitHub id
            sub = stringOrNull(profile.get("id"));
        }
        String email = stringOrNull(profile.get("email"));
        String name = stringOrNull(profile.get("name"));
        String picture = stringOrNull(profile.get("picture"));
        if (picture == null) {
            picture = stringOrNull(profile.get("avatar_url"));
        }

        // Upsert user (use effectively final variables inside lambda)
        final String providerFinal = provider;
        final String subFinal = sub;
        User user = users.findByProviderAndProviderUserId(providerFinal, subFinal)
                .orElseGet(() -> new User().setProvider(providerFinal).setProviderUserId(subFinal));
        user.setEmail(email);
        user.setName(name);
        user.setAvatarUrl(picture);
        if (user.getRoles().isEmpty()) {
            user.setRoles(Set.of("USER"));
        }
        users.save(user);

        // Create JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());
        claims.put("avatarUrl", user.getAvatarUrl());
        claims.put("uid", user.getId());
        claims.put("provider", user.getProvider());
        claims.put("roles", user.getRoles());

        String jwt = jwtService.generate(user.getId(), claims);

        Map<String, Object> result = new HashMap<>();
        result.put("token", jwt);
        result.put("user", Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "name", user.getName(),
                "avatarUrl", user.getAvatarUrl(),
                "provider", user.getProvider(),
                "roles", user.getRoles()
        ));
        return result;
    }

    public String buildCallbackUrl(String provider) {
        String base = props.getRedirectBaseUrl();
        return UriComponentsBuilder.fromUriString(base)
                .path("/auth/callback/" + provider)
                .build()
                .toUriString();
    }

    private String stringOrNull(Object o) {
        return o == null ? null : String.valueOf(o);
    }
}
