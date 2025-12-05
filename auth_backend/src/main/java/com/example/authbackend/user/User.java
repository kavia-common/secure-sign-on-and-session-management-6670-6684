package com.example.authbackend.user;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * PUBLIC_INTERFACE
 * Simple User entity for in-memory storage. Can be migrated to JPA later.
 */
public class User {
    /** Unique id (generated) */
    private String id;
    /** OAuth provider name (google|github|microsoft|dev) */
    private String provider;
    /** Provider specific subject (sub) or user id */
    private String providerUserId;
    private String email;
    private String name;
    private String avatarUrl;
    private Set<String> roles = new HashSet<>();
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public User() {}

    public User(String id, String provider, String providerUserId, String email, String name, String avatarUrl) {
        this.id = id;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.email = email;
        this.name = name;
        this.avatarUrl = avatarUrl;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getProvider() {
        return provider;
    }

    public User setProvider(String provider) {
        this.provider = provider;
        return this;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public User setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public User setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public User setRoles(Set<String> roles) {
        this.roles = roles;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public User setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public User setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User that = (User) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
