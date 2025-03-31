package com.shrimay.redis.service;

class CacheEntry {
    
    private final String value;

    public CacheEntry(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
