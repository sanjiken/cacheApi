package com.artere.cacheApi.cache;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class SimpleCacheTest {

    private final SimpleCache<String> cache = new SimpleCache<>();

    @AfterEach
    void tearDown() {
        cache.clear();
        cache.shutdown();
    }

    @Test
    void putAndGetShouldReturnValue() {
        cache.put("k1", "v1", 5000);
        assertEquals("v1", cache.get("k1"));
    }

    @Test
    void valueShouldExpireAfterTtl() throws InterruptedException {
        cache.put("k2", "v2", 200);
        assertEquals("v2", cache.get("k2"));
        Thread.sleep(350);
        assertNull(cache.get("k2"));
    }

    @Test
    void concurrentPutGetShouldWork() throws InterruptedException, ExecutionException {
        int threads = 10;
        ExecutorService ex = Executors.newFixedThreadPool(threads);
        try {
            Callable<Void> writer = () -> {
                for (int i = 0; i < 100; i++) {
                    cache.put("key" + i, "val" + i, 5000);
                }
                return null;
            };
            Callable<Void> reader = () -> {
                for (int i = 0; i < 100; i++) {
                    cache.get("key" + i);
                }
                return null;
            };

            Future<?>[] futures = new Future<?>[threads];
            for (int i = 0; i < threads; i++) {
                futures[i] = ex.submit(i % 2 == 0 ? writer : reader);
            }
            for (Future<?> f : futures) f.get();
        } finally {
            ex.shutdownNow();
        }
    }
}
