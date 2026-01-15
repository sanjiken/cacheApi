package com.artere.cacheApi.web.dto;

public class PutRequest {
    private String value;
    private long ttlMillis = 60000; // default 60s

    public PutRequest() {}

    public PutRequest(String value, long ttlMillis) {
        this.value = value;
        this.ttlMillis = ttlMillis;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getTtlMillis() {
        return ttlMillis;
    }

    public void setTtlMillis(long ttlMillis) {
        this.ttlMillis = ttlMillis;
    }
}
