package com.janboerman.f2pstarassist.common;

import java.util.Objects;

public final class GroupKey {

    private String key;

    public GroupKey(String key) {
        this.key = Objects.requireNonNull(key, "key cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof GroupKey)) return false;

        GroupKey that = (GroupKey) o;
        return Objects.equals(this.key, that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return key;
    }
}
