package com.example.artere.cacheApi.cache;

import java.util.concurrent.*;

public class SimpleCache<V> {

    private final ConcurrentHashMap<String, V> map = new ConcurrentHashMap<>();
    private final DelayQueue<DelayedCacheItem> delayQueue = new DelayQueue<>();
    private final ExecutorService cleaner = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "cache-cleaner");
        t.setDaemon(true);
        return t;
    });

    public SimpleCache() {
        cleaner.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    DelayedCacheItem item = delayQueue.take(); // bloque jusqu'à expiration
                    map.remove(item.getKey());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public void put(String key, V value, long ttlMillis) {
        if (key == null || value == null) throw new IllegalArgumentException("key and value must not be null");
        map.put(key, value);
        long expireAt = System.currentTimeMillis() + Math.max(0, ttlMillis);
        delayQueue.put(new DelayedCacheItem(key, expireAt));
    }

    public V get(String key) {
        return map.get(key);
    }

    public void remove(String key) {
        map.remove(key);
        // Note: DelayQueue will still contain the DelayedCacheItem until it expires,
        // but removing from map is sufficient for correctness.
    }

    public void clear() {
        map.clear();
        delayQueue.clear();
    }

    public void shutdown() {
        cleaner.shutdownNow();
    }
}
