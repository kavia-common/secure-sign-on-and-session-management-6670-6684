package com.example.authbackend.oauth;

import com.example.authbackend.config.OAuthProperties;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

/**
 * A simple generic OAuth2 client for providers using standard endpoints.
 * Note: This is a simplified implementation for demo purposes.
 */
public class SimpleOAuthProviderClient implements ProviderClient {

    private final String name;
    private final OAuthProperties.Provider p;

    public SimpleOAuthProviderClient(String name, OAuthProperties.Provider provider) {
        this.name = name;
        this.p = provider;
    }

    @Override
    public String buildAuthorizationUrl(String state) {
        String scopes = Optional.ofNullable(p.getScopes()).orElse("");
        String scopeParam = String.join(" ", Arrays.stream(scopes.split(",")).map(String::trim).toList());
        return UriComponentsBuilder.fromHttpUrl(p.getAuthUri())
                .queryParam("client_id", p.getClientId())
                .queryParam("response_type", "code")
                .queryParam("scope", scopeParam)
                .queryParam("state", state)
                .build()
                .toUriString();
    }

    @Override
    public Map<String, Object> exchangeCodeForToken(String code, String redirectUri) throws Exception {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // GitHub expects Accept header for JSON response
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("redirect_uri", redirectUri);
        form.add("client_id", p.getClientId());
        form.add("client_secret", p.getClientSecret());

        ResponseEntity<Map> resp = rt.postForEntity(p.getTokenUri(), new HttpEntity<>(form, headers), Map.class);
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException("Failed to exchange code for token for provider " + name);
        }
        return (Map<String, Object>) resp.getBody();
    }

    @Override
    public Map<String, Object> fetchUserInfo(Map<String, Object> tokenResponse) throws Exception {
        RestTemplate rt = new RestTemplate();
        String accessToken = Optional.ofNullable((String) tokenResponse.get("access_token"))
                .orElseThrow(() -> new IllegalStateException("Missing access token"));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        ResponseEntity<Map> userResp = rt.exchange(p.getUserInfoUri(), HttpMethod.GET, new HttpEntity<>(headers), Map.class);
        Map<String, Object> result = new HashMap<>();
        if (userResp.getStatusCode().is2xxSuccessful() && userResp.getBody() != null) {
            result.putAll((Map<String, Object>) userResp.getBody());
        }

        // GitHub emails endpoint if email may be missing
        if ("github".equals(name) && (result.get("email") == null) && p.getEmailUri() != null) {
            ResponseEntity<List> emailResp = rt.exchange(p.getEmailUri(), HttpMethod.GET, new HttpEntity<>(headers), List.class);
            if (emailResp.getStatusCode().is2xxSuccessful() && emailResp.getBody() != null) {
                List<Map<String, Object>> emails = (List<Map<String, Object>>) (List<?>) emailResp.getBody();
                Optional<Map<String, Object>> primary = emails.stream()
                        .filter(e -> Boolean.TRUE.equals(e.get("primary")))
                        .findFirst();
                primary.ifPresent(e -> result.put("email", e.get("email")));
                if (result.get("email") == null && !emails.isEmpty()) {
                    result.put("email", emails.get(0).get("email"));
                }
            }
        }
        return result;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isConfigured() {
        return p.getClientId() != null && !p.getClientId().isBlank()
                && p.getClientSecret() != null && !p.getClientSecret().isBlank()
                && p.getAuthUri() != null && !p.getAuthUri().isBlank()
                && p.getTokenUri() != null && !p.getTokenUri().isBlank()
                && p.getUserInfoUri() != null && !p.getUserInfoUri().isBlank();
    }
}
