package com.janboerman.f2pstarassist.common;

import java.util.Objects;
import java.util.Set;

public class StarPacket {

    private final Set<GroupKey> groups;
    private final Payload payload;

    public StarPacket(Set<GroupKey> groups, Payload payload) {
        this.groups = Objects.requireNonNull(groups, "groups cannot be null");
        this.payload = Objects.requireNonNull(payload, "payload cannot be null");
    }

    public Set<GroupKey> getGroups() {
        return groups;
    }

    public Payload getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "StarPacket" +
                "{groups=" + groups +
                ",payload=" + payload +
                "}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(groups, payload);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof StarPacket)) return false;

        StarPacket that = (StarPacket) o;
        return Objects.equals(this.getGroups(), that.getGroups())
                && Objects.equals(this.getPayload(), that.getPayload());
    }

}
