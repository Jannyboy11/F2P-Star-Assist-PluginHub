package com.janboerman.starhunt.common;

import java.util.Objects;

public final class StarUpdate implements Payload {

    private final StarLocation location;
    private final int world;
    private final StarTier tier;

    public StarUpdate(StarTier tier, StarLocation location, int world) {
        this.tier = Objects.requireNonNull(tier);
        this.location = Objects.requireNonNull(location);
        this.world = world;
    }

    public StarUpdate(StarKey starKey, StarTier tier) {
        this(tier, starKey.getLocation(), starKey.getWorld());
    }

    public StarLocation getLocation() {
        return location;
    }

    public int getWorld() {
        return world;
    }

    public StarTier getTier() {
        return tier;
    }

    public StarKey getKey() {
        return new StarKey(getLocation(), getWorld());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocation(), getWorld(), getTier());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof StarUpdate)) return false;

        StarUpdate that = (StarUpdate) o;
        return this.getLocation() == that.getLocation()
                && this.getWorld() == that.getWorld()
                && this.getTier() == that.getTier();
    }

    @Override
    public String toString() {
        return "StarUpdate{location=" + getLocation() + ",world=" + getWorld() + ",tier=" + getTier() + "}";
    }
}
