package com.example.authbackend.oauth;

import com.example.authbackend.config.OAuthProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for available OAuth providers.
 */
@Component
public class ProviderRegistry {

    private final Map<String, ProviderClient> providers = new HashMap<>();

    public ProviderRegistry(OAuthProperties props) {
        if (props.getProviders() != null) {
            OAuthProperties.Provider google = props.getProviders().get("google");
            if (google != null) {
                providers.put("google", new SimpleOAuthProviderClient("google", google));
            }
            OAuthProperties.Provider github = props.getProviders().get("github");
            if (github != null) {
                providers.put("github", new SimpleOAuthProviderClient("github", github));
            }
            OAuthProperties.Provider microsoft = props.getProviders().get("microsoft");
            if (microsoft != null) {
                providers.put("microsoft", new SimpleOAuthProviderClient("microsoft", microsoft));
            }
        }
    }

    public ProviderClient get(String name) {
        ProviderClient c = providers.get(name);
        if (c == null) {
            throw new IllegalArgumentException("Unknown provider: " + name);
        }
        return c;
    }

    public Map<String, ProviderClient> all() {
        return providers;
    }
}
