package com.janboerman.f2pstarassist.common;

import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;

public final class CrashedStar implements Comparable<CrashedStar>, Payload, Cloneable {

    private static final Comparator<CrashedStar> COMPARATOR = Comparator
            .comparing(CrashedStar::getTier)
            .thenComparing(CrashedStar::getWorld)
            .thenComparing(CrashedStar::getLocation);

    private final StarLocation location;
    private final int world;
    private StarTier tier;

    private final Instant detectedAt;
    private final User discoveredBy;

    public CrashedStar(StarTier tier, StarLocation location, int world, Instant detectedAt, User discoveredBy) {
        Objects.requireNonNull(tier,"tier cannot be null");
        Objects.requireNonNull(location, "location cannot be null");
        Objects.requireNonNull(detectedAt, "detection timestamp cannot be null");

        this.tier = tier;
        this.location = location;
        this.world = world;
        this.detectedAt = detectedAt;
        this.discoveredBy = discoveredBy;   //differentiate between a Discord user and a RuneScape user?
        //number of miners?
    }

    public CrashedStar(StarKey key, StarTier tier, Instant detectedAt, User discoveredBy) {
        this(tier, key.getLocation(), key.getWorld(), detectedAt, discoveredBy);
    }

    @Override
    public CrashedStar clone() {
        return new CrashedStar(tier, location, world, detectedAt, discoveredBy);
    }

    public synchronized StarTier getTier() {
        return tier;
    }

    public synchronized void setTier(StarTier lowerTier) {
        assert lowerTier != null : "tier cannot be null";
        tier = lowerTier;
    }

    public StarLocation getLocation() {
        return location;
    }

    public int getWorld() {
        return world;
    }

    public Instant getDetectedAt() {
        return detectedAt;
    }

    public User getDiscoveredBy() {
        return discoveredBy;
    }

    public StarKey getKey() {
        return new StarKey(getLocation(), getWorld());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof CrashedStar)) return false;

        CrashedStar that = (CrashedStar) o;
        return this.getLocation() == that.getLocation()
                && this.getWorld() == that.getWorld()
                && this.getTier() == that.getTier();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocation(), getWorld(), getTier());
    }

    @Override
    public int compareTo(CrashedStar that) {
        return COMPARATOR.compare(this, that);
    }

    @Override
    public String toString() {
        return "CrashedStar"
                + "{tier=" + getTier()
                + ",location=" + getLocation()
                + ",world=" + getWorld()
                + ",detected at=" + getDetectedAt()
                + ",discovered by=" + getDiscoveredBy()
                + "}";
    }

}
