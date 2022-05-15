package com.janboerman.f2pstarassist.common;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

//what the client sends to the server to get a StarList
public final class StarRequest implements Payload {

    private Set<CrashedStar> knownStars;

    public StarRequest(Set<CrashedStar> knownStars) {
        this.knownStars = Objects.requireNonNull(knownStars);
    }

    public Set<CrashedStar> getKnownStars() {
        return Collections.unmodifiableSet(knownStars);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof StarRequest)) return false;

        StarRequest that = (StarRequest) o;
        return Objects.equals(this.getKnownStars(), that.getKnownStars());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getKnownStars());
    }

    @Override
    public String toString() {
        return "StarRequest"
                + "{knownStars=" + getKnownStars()
                + "}";
    }

}
