package com.pilog.mdm.exception;

public class UserServiceException extends RuntimeException {

    public UserServiceException(String message) {
        super(message);
    }

    public UserServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

