package com.example.authbackend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the authentication backend application.
 * Provides OAuth2 login flows with JWT sessions and optional in-memory session store.
 */
@SpringBootApplication(scanBasePackages = "com.example.authbackend")
@OpenAPIDefinition(
        info = @Info(
                title = "Auth Backend",
                version = "0.1.0",
                description = "OAuth2/SSO Authentication backend with JWT sessions"
        ),
        servers = {
                @Server(url = "/", description = "Default Server")
        }
)
public class AuthBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthBackendApplication.class, args);
    }
}
