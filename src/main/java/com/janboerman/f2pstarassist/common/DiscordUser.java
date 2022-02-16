package com.janboerman.f2pstarassist.common;

import java.util.Objects;

public final class DiscordUser implements User {

    private final String name; //display name
    //possibly: private final String tag;

    public DiscordUser(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof DiscordUser)) return false;

        DiscordUser that = (DiscordUser) o;
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
