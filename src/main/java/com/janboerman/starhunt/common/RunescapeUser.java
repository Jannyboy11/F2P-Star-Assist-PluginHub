package com.janboerman.starhunt.common;

import java.util.Objects;

public final class RunescapeUser implements User {

    private final String name; //rsn

    public RunescapeUser(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof RunescapeUser)) return false;

        RunescapeUser that = (RunescapeUser) o;
        return Objects.equals(this.getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
