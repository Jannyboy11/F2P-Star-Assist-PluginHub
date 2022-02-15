package com.janboerman.starhunt.common;

public interface User /*permits RunescapeUser, DiscordUser, UnknownUser*/ {

    public static User unknown() {
        return UnknownUser.INSTANCE;
    }
}

class UnknownUser implements User {

    static final UnknownUser INSTANCE = new UnknownUser();

    private UnknownUser() {
    }

}
