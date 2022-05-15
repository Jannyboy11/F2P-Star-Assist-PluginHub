package com.janboerman.f2pstarassist.common;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

//what the server responds with when the client sends a StarRequest
public final class StarList {

    private Map<Set<CrashedStar>, Set<GroupKey>> freshStars;
    private Set<StarUpdate> starUpdates;
    private Set<StarKey> deletedStars;

    //the json representation of a Map<Set<CrashedStar>, Set<GroupKey>> could be something like [{ "stars" : [star1, star2, star3, ...], "owned by" : [group1, group2, group3, ...] }, ...]

    public StarList(Map<Set<CrashedStar>, Set<GroupKey>> freshStars,
                    Set<StarUpdate> starUpdates,
                    Set<StarKey> deletedStars) {
        this.freshStars = Objects.requireNonNull(freshStars);
        this.starUpdates = Objects.requireNonNull(starUpdates);
        this.deletedStars = Objects.requireNonNull(deletedStars);
    }

    public Map<Set<CrashedStar>, Set<GroupKey>> getFreshStars() {
        return Collections.unmodifiableMap(freshStars);
    }

    public Set<StarUpdate> getStarUpdates() {
        return Collections.unmodifiableSet(starUpdates);
    }

    public Set<StarKey> getDeletedStars() {
        return Collections.unmodifiableSet(deletedStars);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof StarList)) return false;

        StarList that = (StarList) o;
        return Objects.equals(this.getFreshStars(), that.getFreshStars())
                && Objects.equals(this.getStarUpdates(), that.getStarUpdates())
                && Objects.equals(this.getDeletedStars(), that.getDeletedStars());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFreshStars(), getStarUpdates(), getDeletedStars());
    }

    @Override
    public String toString() {
        return "StarList"
                + "{freshStars=" + getFreshStars()
                + ",starUpdates=" + getStarUpdates()
                + ",deletedStars=" + getDeletedStars()
                + "}";
    }
}
