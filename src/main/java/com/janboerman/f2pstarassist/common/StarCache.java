package com.janboerman.f2pstarassist.common;

import com.google.common.cache.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class StarCache {

    private final Cache<StarKey, CrashedStar> cache;

    private static CacheBuilder<StarKey, CrashedStar> newCacheBuilder() {
        return (CacheBuilder<StarKey, CrashedStar>) (CacheBuilder) CacheBuilder.newBuilder();
    }

    private StarCache(CacheBuilder<StarKey, CrashedStar> cacheBuilder) {
        this.cache = cacheBuilder
                .expireAfterWrite(2, TimeUnit.HOURS)
                .build();
    }

    public StarCache(RemovalListener<StarKey, CrashedStar> removalListener) {
        this(newCacheBuilder().removalListener(removalListener));
    }

    public StarCache() {
        this(newCacheBuilder());
    }

    //returns the old star
    public CrashedStar add(CrashedStar newStar) {
        StarKey key = newStar.getKey();
        CrashedStar oldStar = cache.getIfPresent(key);
        if (oldStar != null) {
            if (newStar.getTier().getSize() > oldStar.getTier().getSize()) {
                cache.put(key, newStar);
            } //else: old star had a higher tier already
        } else {
            cache.put(key, newStar);
        }
        return oldStar;
    }

    //returns true if a star was added, false otherwise
    public boolean addAll(Collection<CrashedStar> stars) {
        boolean result = false;
        for (CrashedStar star : stars) {
            result |= (add(star) == null);
        }
        return result;
    }

    public CrashedStar get(StarKey key) {
        CrashedStar crashedStar = cache.getIfPresent(key);
        if (crashedStar == null /*does not exist*/
                || crashedStar.getDetectedAt().isBefore(Instant.now().minus(2, ChronoUnit.HOURS))) /*more than two hours ago*/ {
            return null;
        } else {
            return crashedStar;
        }
    }

    //returns the old star
    public CrashedStar forceAdd(CrashedStar star) {
        return cache.asMap().put(star.getKey(), star);
    }

    //returns the old star
    public CrashedStar remove(StarKey starKey) {
        CrashedStar existing = get(starKey);
        cache.invalidate(starKey);
        return existing;
    }

    public boolean contains(StarKey starKey) {
        return get(starKey) != null;
    }

    public Set<CrashedStar> getStars() {
        return new HashSet<>(cache.asMap().values());
    }

    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(", ", "StarCache{", "}");
        for (Map.Entry<StarKey, CrashedStar> entry : cache.asMap().entrySet()) {
            stringJoiner.add(entry.getKey() + "=" + entry.getValue().getTier());
        }
        return stringJoiner.toString();
    }
}
