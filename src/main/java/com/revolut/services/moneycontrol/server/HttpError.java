package com.revolut.services.moneycontrol.server;

public class HttpError extends RuntimeException {
    private static final long serialVersionUID = 8769596371344178179L;

    public HttpError(String var1) {
        super(var1);
    }
}