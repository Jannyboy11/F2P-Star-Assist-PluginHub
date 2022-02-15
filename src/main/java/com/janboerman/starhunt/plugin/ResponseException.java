package com.janboerman.starhunt.plugin;

import okhttp3.Call;

public class ResponseException extends Exception {

    private final Call call;

    public ResponseException(Call call, String message) {
        super(message);
        this.call = call;
    }

    public ResponseException(Call call, Exception cause) {
        super(cause);
        this.call = call;
    }

    public Call getCall() {
        return call;
    }
}
