package com.revolut.services.moneycontrol.exception;

/**
 * Custom Exception class for Exceptions from Service Layer
 */
public class ServiceException extends Exception {

    private static final long serialVersionUID = 3L;

    public ServiceException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
