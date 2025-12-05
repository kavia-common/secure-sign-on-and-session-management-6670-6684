package com.example.authbackend.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple info endpoint to verify app running.
 */
@RestController
@Tag(name = "Info")
public class InfoController {

    @GetMapping("/")
    @Operation(summary = "Root", description = "Welcome endpoint")
    public String root() {
        return "Auth backend is running";
    }

    @GetMapping("/api/info")
    @Operation(summary = "Application info", description = "Returns application information")
    public String info() {
        return "Spring Boot Application: auth-backend (OAuth2 + JWT)";
    }
}
