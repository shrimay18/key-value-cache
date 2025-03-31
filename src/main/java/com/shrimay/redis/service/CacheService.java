package com.shrimay.redis.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class CacheService {

    private final ConcurrentHashMap<String, CacheEntry> cacheStore = new ConcurrentHashMap<>();

    public Map<String, String> put(String key, String value) {
        if (key == null || value == null || key.length() > 256 || value.length() > 256) {
            return Map.of("status", "ERROR", "message", "Invalid key or value");
        }
        
        cacheStore.put(key, new CacheEntry(value));
        return Map.of("status", "OK", "message", "Key inserted/updated successfully");
    }

    public Map<String, String> get(String key) {
        CacheEntry entry = cacheStore.get(key);
        
        if (entry == null) {
            return Map.of("status", "ERROR", "message", "Key not found");
        }
        
        return Map.of("status", "OK", "key", key, "value", entry.getValue());
    }
}
