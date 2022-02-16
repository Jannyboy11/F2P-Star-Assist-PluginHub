package com.janboerman.f2pstarassist.common;

import java.util.Comparator;
import java.util.Objects;

public final class StarKey implements Comparable<StarKey>, Payload {

    private static final Comparator<StarKey> COMPARATOR = Comparator
            .comparing(StarKey::getLocation)
            .thenComparing(StarKey::getWorld);

    private final StarLocation location;
    private final int world;

    public StarKey(StarLocation location, int world) {
        this.location = Objects.requireNonNull(location);
        this.world = Objects.requireNonNull(world);
    }

    public StarLocation getLocation() {
        return location;
    }

    public int getWorld() {
        return world;
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, world);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof StarKey)) return false;

        StarKey that = (StarKey) obj;
        return this.location == that.location && this.world == that.world;
    }

    @Override
    public int compareTo(StarKey that) {
        return COMPARATOR.compare(this, that);
    }

    @Override
    public String toString() {
        return "StarKey{location=" + location + ",world=" + world + "}";
    }

}
