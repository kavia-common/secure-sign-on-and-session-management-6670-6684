package com.example.authbackend.oauth;

import java.util.Map;

/**
 * PUBLIC_INTERFACE
 * A strategy for provider-specific OAuth2 operations.
 */
public interface ProviderClient {
    /**
     * PUBLIC_INTERFACE
     * Build the provider authorization URL for redirect.
     */
    String buildAuthorizationUrl(String state);

    /**
     * PUBLIC_INTERFACE
     * Exchange authorization code for tokens.
     */
    Map<String, Object> exchangeCodeForToken(String code, String redirectUri) throws Exception;

    /**
     * PUBLIC_INTERFACE
     * Fetch user info map (must at least include sub/id, email if available, name, picture/avatar).
     */
    Map<String, Object> fetchUserInfo(Map<String, Object> tokenResponse) throws Exception;

    /**
     * PUBLIC_INTERFACE
     * Provider name identifier (e.g., google, github, microsoft).
     */
    String getName();

    /**
     * PUBLIC_INTERFACE
     * Whether the provider is properly configured.
     */
    boolean isConfigured();
}
