# Auth Backend (Spring Boot)

OAuth2/SSO authentication backend supporting Google, GitHub, Microsoft and JWT-based stateless sessions.
Includes a development login mode to mint JWTs without a provider.

## Features

- POST /auth/login/{provider}: returns redirect URL to provider auth page
- GET /auth/callback/{provider}: handles code exchange, upserts user, returns `{ token, user }`
- POST /auth/logout: stateless logout (client discards token)
- GET /auth/session: validates Bearer token and returns session info
- GET /auth/health: provider readiness + dev login status
- POST /auth/login/dev: mint a JWT for provided email when `DEV_LOGIN_ENABLED=true`
- JWT filter validates tokens on incoming requests and attaches principal

OpenAPI docs at `/swagger-ui.html`.

## Configuration

Copy `.env.example` to your environment and set values (or use container env):

Required variables:
- JWT_SECRET
- OAUTH_JWT_ISSUER (optional)
- OAUTH_REDIRECT_BASE_URL
- DEV_LOGIN_ENABLED (optional)

Providers:
- OAUTH_GOOGLE_CLIENT_ID / OAUTH_GOOGLE_CLIENT_SECRET
- OAUTH_GITHUB_CLIENT_ID / OAUTH_GITHUB_CLIENT_SECRET
- OAUTH_MICROSOFT_CLIENT_ID / OAUTH_MICROSOFT_CLIENT_SECRET

CORS:
- CORS_ALLOWED_ORIGINS (default `*`)

## Running

This project uses Gradle and Java 17.

```
./gradlew bootRun
```

## Example curl

- Health
```
curl -s http://localhost:3001/auth/health | jq
```

- Dev login
```
curl -s -X POST http://localhost:3001/auth/login/dev \
  -H 'Content-Type: application/json' \
  -d '{"email":"dev@example.com"}'
```

- Session validation
```
TOKEN=... # from login/dev or callback
curl -s http://localhost:3001/auth/session -H "Authorization: Bearer $TOKEN"
```

- Start OAuth login (Google)
```
curl -s -X POST http://localhost:3001/auth/login/google
# Returns {"redirectUrl":"https://accounts.google.com/..."}
```

## Notes

- App starts without provider secrets; login endpoints return clear error explaining required setup; `/auth/health` shows missing config.
- JWT-based sessions are default; an in-memory session store could be added behind a feature flag if needed (Spring Session Map).
- CSRF disabled for API; CORS is permissive by default (TODO: restrict in production).

