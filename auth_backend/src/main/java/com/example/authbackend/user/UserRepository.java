package com.example.authbackend.user;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory user repository to be replaced with JPA later.
 */
@Repository
public class UserRepository {

    private final Map<String, User> usersById = new ConcurrentHashMap<>();

    public Optional<User> findById(String id) {
        return Optional.ofNullable(usersById.get(id));
    }

    public Optional<User> findByProviderAndProviderUserId(String provider, String providerUserId) {
        return usersById.values().stream()
                .filter(u -> Objects.equals(u.getProvider(), provider) && Objects.equals(u.getProviderUserId(), providerUserId))
                .findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return usersById.values().stream()
                .filter(u -> Objects.equals(u.getEmail(), email))
                .findFirst();
    }

    public User save(User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
            user.setCreatedAt(Instant.now());
        }
        user.setUpdatedAt(Instant.now());
        usersById.put(user.getId(), user);
        return user;
    }

    public List<User> findAll() {
        return usersById.values().stream().collect(Collectors.toList());
    }
}
