package com.example.AegisIQ.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RefreshTokenService {

    private static class Entry { String userId; long expiresAt; }

    private final Map<String, Entry> tokens = new ConcurrentHashMap<>();

    public String createRefreshToken(String userId, long ttlMillis) {
        String token = UUID.randomUUID().toString();
        Entry e = new Entry();
        e.userId = userId;
        e.expiresAt = Instant.now().toEpochMilli() + ttlMillis;
        tokens.put(token, e);
        return token;
    }

    public void storeTokenString(String token, String userId, long ttlMillis) {
        Entry e = new Entry();
        e.userId = userId;
        e.expiresAt = Instant.now().toEpochMilli() + ttlMillis;
        tokens.put(token, e);
    }

    public String validateAndGetUserId(String token) {
        Entry e = tokens.get(token);
        if (e == null) return null;
        if (e.expiresAt < Instant.now().toEpochMilli()) {
            tokens.remove(token);
            return null;
        }
        return e.userId;
    }

    public void revoke(String token) {
        tokens.remove(token);
    }
}
