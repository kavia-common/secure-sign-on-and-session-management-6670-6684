package com.example.authbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * PUBLIC_INTERFACE
 * Configuration properties for OAuth providers, redirect base url, and dev settings.
 */
@Configuration
@ConfigurationProperties(prefix = "oauth")
public class OAuthProperties {

    private Jwt jwt = new Jwt();
    private String redirectBaseUrl;
    private Dev dev = new Dev();
    private Map<String, Provider> providers;

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public String getRedirectBaseUrl() {
        return redirectBaseUrl;
    }

    public void setRedirectBaseUrl(String redirectBaseUrl) {
        this.redirectBaseUrl = redirectBaseUrl;
    }

    public Dev getDev() {
        return dev;
    }

    public void setDev(Dev dev) {
        this.dev = dev;
    }

    public Map<String, Provider> getProviders() {
        return providers;
    }

    public void setProviders(Map<String, Provider> providers) {
        this.providers = providers;
    }

    public static class Dev {
        private boolean loginEnabled;

        public boolean isLoginEnabled() {
            return loginEnabled;
        }

        public void setLoginEnabled(boolean loginEnabled) {
            this.loginEnabled = loginEnabled;
        }
    }

    public static class Jwt {
        private String issuer;
        private String secret;
        private long expirationSeconds = 3600;

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getExpirationSeconds() {
            return expirationSeconds;
        }

        public void setExpirationSeconds(long expirationSeconds) {
            this.expirationSeconds = expirationSeconds;
        }
    }

    public static class Provider {
        private String clientId;
        private String clientSecret;
        private String authUri;
        private String tokenUri;
        private String userInfoUri;
        private String emailUri;
        private String scopes;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getAuthUri() {
            return authUri;
        }

        public void setAuthUri(String authUri) {
            this.authUri = authUri;
        }

        public String getTokenUri() {
            return tokenUri;
        }

        public void setTokenUri(String tokenUri) {
            this.tokenUri = tokenUri;
        }

        public String getUserInfoUri() {
            return userInfoUri;
        }

        public void setUserInfoUri(String userInfoUri) {
            this.userInfoUri = userInfoUri;
        }

        public String getEmailUri() {
            return emailUri;
        }

        public void setEmailUri(String emailUri) {
            this.emailUri = emailUri;
        }

        public String getScopes() {
            return scopes;
        }

        public void setScopes(String scopes) {
            this.scopes = scopes;
        }
    }
}
