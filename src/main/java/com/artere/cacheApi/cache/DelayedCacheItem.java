package com.artere.cacheApi.cache;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayedCacheItem implements Delayed {

    private final String key;
    private final long expireAtMillis;

    public DelayedCacheItem(String key, long expireAtMillis) {
        this.key = key;
        this.expireAtMillis = expireAtMillis;
    }

    public String getKey() {
        return key;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = expireAtMillis - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (o == this) return 0;
        long d = getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
        return Long.compare(d, 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DelayedCacheItem)) return false;
        return key.equals(((DelayedCacheItem) obj).key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
